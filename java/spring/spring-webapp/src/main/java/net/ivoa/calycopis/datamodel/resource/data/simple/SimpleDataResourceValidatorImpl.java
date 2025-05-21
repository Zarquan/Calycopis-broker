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
package net.ivoa.calycopis.datamodel.resource.data.simple;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.Validator.ResultEnum;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;

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
    public AbstractDataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", context.makeDataValidatorResultKey(requested), requested.getClass().getName());
        if (requested instanceof IvoaSimpleDataResource)
            {
            return validate(
                (IvoaSimpleDataResource) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    public AbstractDataResourceValidator.Result validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}]", context.makeDataValidatorResultKey(requested));

        boolean success = true ;
        
        IvoaSimpleDataResource validated = new IvoaSimpleDataResource(
            SimpleDataResource.TYPE_DISCRIMINATOR
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
        success &= ResultEnum.ACCEPTED.equals(storage.getEnum());
        
        validated.setUuid(
            requested.getUuid()
            );
        validated.setName(
            trim(requested.getName())
            );

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
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public SimpleDataResourceEntity build(final ExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        storage.getEntity(),
                        validated
                        );
                    }
                }; 
            
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );
            //
            // Save the DataResource in the state.
            context.addDataValidatorResult(
                dataResult
                );

            return dataResult ;
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
    }
