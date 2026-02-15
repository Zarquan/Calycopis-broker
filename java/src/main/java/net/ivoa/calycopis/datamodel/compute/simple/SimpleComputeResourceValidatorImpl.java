/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 *
 */
package net.ivoa.calycopis.datamodel.compute.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 * A validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public abstract class SimpleComputeResourceValidatorImpl
extends AbstractComputeResourceValidatorImpl
implements SimpleComputeResourceValidator
    {

    private final Platform platform;

    /**
     * Public constructor.
     * 
     */
    public SimpleComputeResourceValidatorImpl(final Platform platform)
        {
        super();
        this.platform = platform;
        }
    
    @Override
    public void validate(
        final IvoaAbstractComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", requested);
        if (requested instanceof IvoaSimpleComputeResource )
            {
            validate(
                (IvoaSimpleComputeResource) requested,
                context
                );
            }
        }

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MIN_CORES_DEFAULT =  1L ;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MAX_CORES_LIMIT   = 16L ;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MIN_MEMORY_DEFAULT =  1L;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MAX_MEMORY_LIMIT   = 16L;

    protected abstract boolean validateCores(
        final IvoaSimpleComputeResource requested,
        final IvoaSimpleComputeResource validated,
        final OfferSetRequestParserContext context
        );

    protected abstract boolean validateMemory(
        final IvoaSimpleComputeResource requested,
        final IvoaSimpleComputeResource validated,
        final OfferSetRequestParserContext context
        );

    /**
     * Validate an IvoaAbstractComputeResource.
     *
     */
    public void validate(
        final IvoaSimpleComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleComputeResource)");
        log.debug("Resource [{}]", requested);

        boolean success = true ;

        IvoaSimpleComputeResource validated = new IvoaSimpleComputeResource()
            .kind(SimpleComputeResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );
        
        success &= validateCores(
            requested,
            validated,
            context
            );

        success &= validateMemory(
            requested,
            validated,
            context
            );
        
        //
        // Process the volume mounts.
        log.debug("Processing the volume mounts");
        if (requested.getVolumes() != null)
            {
            /*
             * TODO
            for (IvoaSimpleComputeVolume volumeRequest : requested.getVolumes())
                {
                // Try finding a storage resource.
                AbstractStorageResourceValidator.Result storage = context.findStorageValidatorResult(volumeRequest.getResource());
                // If we din't find a storage resource.
                if (storage == null)
                    {
                    // Try finding a data resource.
                    AbstractDataResourceValidator.Result data = context.findDataValidatorResult(volumeRequest.getResource());
                    // If we found a data resource.
                    if (data != null)
                        {
                        // Try finding a corresponding storage resource.
                        storage = null;
                        }
                    }
                
                if (storage  != null)
                    {
                    // Create a volume linking the storage resource to the compute resource.
                    
                    }
                else {
                    // error unmatched data resource ...
                    }
                }
             * 
             */
            }

        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new validator Result.
            AbstractComputeResourceValidator.Result result = new AbstractComputeResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public AbstractComputeResourceEntity build(final SimpleExecutionSessionEntity session, final ComputeResourceOffer offer)                
                    {
                    this.entity = platform.getComputeResourceEntityFactory().create(
                        session,
                        this,
                        offer
                        );
                    return this.entity;
                    }

                @Override
                public Long getPreparationTime()
                    {
                    return estimatePrepareTime(
                        validated
                        );
                    }
                };
            //
            // Add our Result to our context.
            context.addComputeValidatorResult(
                result
                );
            context.dispatched(true);
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            context.dispatched(true);
            }
        }

    /**
     * Predict the time to prepare a DockerContainer for execution.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long estimatePrepareTime(final IvoaSimpleComputeResource validated);

    /**
     * Predict the time to release a DockerContainer.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long estimateReleaseTime(final IvoaSimpleComputeResource validated);
    
    }
