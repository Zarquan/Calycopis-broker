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

package net.ivoa.calycopis.datamodel.compute;

import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;

/**
 * 
 */
public interface AbstractComputeResourceValidator
extends Validator<IvoaAbstractComputeResource, AbstractComputeResourceEntity>
    {
   
    /**
     * Public interface for a validator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity> 
        {
        /**
         * Build an entity based on our validation result.
         * 
         */
        public AbstractComputeResourceEntity build(final SessionEntity session, final ComputeResourceOffer offer);

        }

    /**
     * Simple Bean implementation of a ComputeResourceValidator result.
     * 
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractComputeResource, AbstractComputeResourceEntity>
    implements Result
        {
        /**
         * Public constructor.
         * 
         */
        public ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor.
         * 
         */
        public ResultBean(
            final ResultEnum result,
            final IvoaAbstractComputeResource object
            ){
            super(
                result,
                object
                );
            }

        @Override
        // Here because we need to create Results with just a status and no entity
        public AbstractComputeResourceEntity build(final SessionEntity session, final ComputeResourceOffer offer)
            {
            return null;
            }
        }
    }
