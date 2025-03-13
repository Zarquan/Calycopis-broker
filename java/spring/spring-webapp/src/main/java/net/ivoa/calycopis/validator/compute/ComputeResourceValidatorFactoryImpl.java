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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.validator.ValidatorFactoryBaseImpl;

/**
 * A factory for compute resource validators.
 * 
 */
@Slf4j
@Component
public class ComputeResourceValidatorFactoryImpl
    extends ValidatorFactoryBaseImpl<IvoaAbstractComputeResource, AbstractComputeResourceEntity>
    implements ComputeResourceValidatorFactory
    {

    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * 
     */
    @Autowired
    public ComputeResourceValidatorFactoryImpl(final SimpleComputeResourceEntityFactory simpleComputeEntityFactory)
        {
        super();
        this.validators.add(
            new SimpleComputeResourceValidator(
                simpleComputeEntityFactory
                )
            );
        }

    @Override
    public void unknown(
        final OfferSetRequestParserState state,
        final IvoaAbstractComputeResource resource
        ){
        unknown(
            state,
            resource.getType(),
            state.makeComputeValidatorResultKey(
                resource
                )
            );
        }
    }
