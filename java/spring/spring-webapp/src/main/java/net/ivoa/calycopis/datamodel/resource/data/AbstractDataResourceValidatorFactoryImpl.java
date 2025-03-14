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
package net.ivoa.calycopis.datamodel.resource.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.data.simple.SimpleDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.resource.data.simple.SimpleDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.functional.validator.ValidatorFactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;

/**
 * A factory implementation for DataResource validators.
 * 
 */
@Component
public class AbstractDataResourceValidatorFactoryImpl
    extends ValidatorFactoryBaseImpl<IvoaAbstractDataResource, AbstractDataResourceEntity>
    implements AbstractDataResourceValidatorFactory
    {

    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * 
     */
    @Autowired
    public AbstractDataResourceValidatorFactoryImpl(
        final SimpleDataResourceEntityFactory simpleDataEntityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super();
        this.validators.add(
            new SimpleDataResourceValidator(
                simpleDataEntityFactory,
                storageValidators
                )
            );
        }
    
    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractDataResource resource
        ){
        unknown(
            context,
            resource.getType(),
            resource.getClass().getName()
            );
        }
    }
