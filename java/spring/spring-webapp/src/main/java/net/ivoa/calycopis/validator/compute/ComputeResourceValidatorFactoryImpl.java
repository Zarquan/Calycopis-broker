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
package net.ivoa.calycopis.validator.compute;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorBaseImpl;

/**
 * A factory for compute resource validators.
 * 
 */
@Component
public class ComputeResourceValidatorFactoryImpl
    extends ValidatorBaseImpl<IvoaAbstractComputeResource>
    implements FactoryBase, Validator<IvoaAbstractComputeResource>
    {
    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * 
     */
    public ComputeResourceValidatorFactoryImpl()
        {
        super();
        this.validators.add(
            new SimpleComputeResourceValidator()
            );
        }

    @Override
    public Validator.ResultSet<IvoaAbstractComputeResource> unknown(
        final OfferSetRequestParserState state,
        final IvoaAbstractComputeResource resource
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
        final IvoaAbstractComputeResource resource
        ){
        state.getValidatedOfferSetRequest().getResources().addComputeItem(
            resource
            );
        }
    }
