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
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;

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
    public AbstractStorageResourceValidator.Result validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractStorageResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleStorageResource simple:
                return validate(
                        simple,
                        context
                        );
            default:
                return new ResultBean(
                    Validator.ResultEnum.CONTINUE
                    );
            }
        }

    /**
     * Validate an IvoaSimpleStorageResource.
     *
     */
    public AbstractStorageResourceValidator.Result validate(
        final IvoaSimpleStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleStorageResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleStorageResource validated = new IvoaSimpleStorageResource(
            SimpleStorageResource.TYPE_DISCRIMINATOR
            );

        //
        // TODO Check available size.
        //

        validated.setName(requested.getName());

        
        //
        // Calculate the preparation time.
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
        
        //
        // Everything is good.
        // Create our result and add it to our context.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public SimpleStorageResourceEntity build(final ExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        validated
                        );
                    }
                }; 
            
            AbstractStorageResourceValidator.Result storageResult = new AbstractStorageResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );

            context.addStorageValidatorResult(
                storageResult 
                );

            return storageResult;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }

    /*
     * TODO This will be platform dependent.
     * 
     */
    public static final Long DEFAULT_PREPARE_TIME = 5L;
    protected Long predictPrepareTime(final IvoaSimpleStorageResource resource)
        {
        return DEFAULT_PREPARE_TIME;
        }
    }
