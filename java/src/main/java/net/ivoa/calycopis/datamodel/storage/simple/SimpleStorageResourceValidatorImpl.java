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
package net.ivoa.calycopis.datamodel.storage.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;

/**
 * A Validator implementation to handle simple storage resources.
 * 
 */
@Slf4j
public class SimpleStorageResourceValidatorImpl
extends AbstractStorageResourceValidatorImpl
implements SimpleStorageResourceValidator
    {
    /**
     * Factory for creating Entities.
     * 
     */
    final SimpleStorageResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleStorageResourceValidatorImpl(
        final SimpleStorageResourceEntityFactory entityFactory
        ){
        super();
        this.entityFactory = entityFactory ;
        }
    
    @Override
    public void validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractStorageResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleStorageResource simple:
                validate(
                    simple,
                    context
                    );
                break;
            default:
                break;
            }
        }

    /**
     * Validate an IvoaSimpleStorageResource.
     *
     */
    public void validate(
        final IvoaSimpleStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleStorageResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleStorageResource validated = new IvoaSimpleStorageResource()
            .kind(SimpleStorageResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );
        
        //
        // TODO Check available size.
        //
        
        //
        // Calculate the preparation time.
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
            AbstractStorageResourceValidator.Result storageResult = new AbstractStorageResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public AbstractStorageResourceEntity build(SimpleExecutionSessionEntity session)
                    {
                    entity = entityFactory.create(
                        session,
                        this
                        );
                    return entity;
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
            context.addStorageValidatorResult(
                storageResult 
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
    }
