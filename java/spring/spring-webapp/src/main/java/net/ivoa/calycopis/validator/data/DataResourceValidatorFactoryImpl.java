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
package net.ivoa.calycopis.validator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.Validator.ResultEnum;
import net.ivoa.calycopis.validator.Validator.ResultSet;
import net.ivoa.calycopis.validator.ValidatorBaseImpl;
import net.ivoa.calycopis.validator.compute.SimpleComputeResourceValidator;

/**
 * A factory for data resource validators.
 * 
 */
@Component
public class DataResourceValidatorFactoryImpl
    extends ValidatorBaseImpl<IvoaAbstractDataResource>
    implements FactoryBase, Validator<IvoaAbstractDataResource>
    {
    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * 
     */
    public DataResourceValidatorFactoryImpl()
        {
        super();
        this.validators.add(
            new SimpleDataResourceValidator()
            );
        }
    
    @Override
    public Validator.ResultSet<IvoaAbstractDataResource> unknown(
        final OfferSetRequestParserState state,
        final IvoaAbstractDataResource resource
        ){
        return unknown(
            state,
            resource.getType(),
            resource.getClass().getName()
            );
        }

    @Override
    public void save(
        final OfferSetRequestParserState state,
        final IvoaAbstractDataResource resource
        ){
        state.getValidatedOfferSetRequest().getResources().addDataItem(
            resource
            );
        }
    }
