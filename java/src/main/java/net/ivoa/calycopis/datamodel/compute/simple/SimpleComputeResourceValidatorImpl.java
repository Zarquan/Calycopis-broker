/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.compute.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
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

    private final AbstractComputeResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleComputeResourceValidatorImpl(final AbstractComputeResourceEntityFactory entityFactory)
        {
        super();
        this.entityFactory = entityFactory;
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", requested);
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not subclass types.
        // This prevents a parent type's validator from intercepting requests
        // that should be handled by a more specific subclass validator.
        if (requested.getClass() == IvoaSimpleComputeResource.class)
            {
            return validate(
                (IvoaSimpleComputeResource) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

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
    public ResultEnum validate(
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
                    this.entity = entityFactory.create(
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
            return ResultEnum.ACCEPTED;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return ResultEnum.FAILED;
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
