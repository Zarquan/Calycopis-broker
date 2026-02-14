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
package net.ivoa.calycopis.datamodel.data.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * A Validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements SimpleDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     * 
     */
    final SimpleDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleDataResourceValidatorImpl(
        final SimpleDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            storageValidators
            );
        this.entityFactory = entityFactory ;
        }
    
    @Override
    public void validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        if (requested instanceof IvoaSimpleDataResource)
            {
            validate(
                (IvoaSimpleDataResource) requested,
                context
                );
            }
        }

    public void validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;
        
        IvoaSimpleDataResource validated = new IvoaSimpleDataResource()
            .kind(SimpleDataResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );
        
        success &= duplicateCheck(
            requested,
            context
            );

        AbstractStorageResourceValidator.Result storage = storageCheck(
            requested,
            validated,
            context
            );
        success &= (storage != null) && ResultEnum.ACCEPTED.equals(storage.getEnum());

        String location = trim(
            requested.getLocation()
            );
        validated.setLocation(location);
        if ((location == null) || (location.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            success = false ;
            }

        //
        // Calculate the preparation time.
        // TODO Move this to after we have validated everything.
        /*
         * 
        validated.setSchedule(
            new IvoaComponentSchedule()
            );
        success &= setPrepareDuration(
            context,
            validated.getSchedule(),
            this.predictPrepareTime(
                validated
                )
            );
         * 
         */
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new validator Result.
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public SimpleDataResourceEntity build(final SimpleExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        storage.getEntity(),
                        this
                        );
                    }

                @Override
                public Long getPreparationTime()
                    {
                    // TODO This will be platform dependent.
                    return DEFAULT_PREPARE_TIME;
                    }
                };
            //
            // Add our Result to our context.
            context.addDataValidatorResult(
                dataResult
                );
            //
            // Add the DataResource to the StorageResource.
            storage.addDataResourceResult(
                dataResult
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

    /*
     * TODO This will be platform dependent.
     * 
     */
    public static final Long DEFAULT_PREPARE_TIME = 5L;
    @Deprecated
    protected Long predictPrepareTime(final IvoaAbstractDataResource validated)
        {
        return DEFAULT_PREPARE_TIME;
        }
    }
