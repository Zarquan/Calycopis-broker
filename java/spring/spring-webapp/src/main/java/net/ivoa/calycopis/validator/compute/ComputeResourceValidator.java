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

import net.ivoa.calycopis.builder.Builder;
import net.ivoa.calycopis.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.validator.Validator;

/**
 * 
 */
public interface ComputeResourceValidator
extends Validator<IvoaAbstractComputeResource, ExecutionSessionEntity, AbstractComputeResourceEntity>
    {
    /**
     * Public interface for a ComputeResourceValidator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractComputeResource, ExecutionSessionEntity, AbstractComputeResourceEntity> 
        {
        // A list of the volume mounts ...
        }

    /**
     * Simple Bean implementation of a DataResourceValidator result.
     * 
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractComputeResource, ExecutionSessionEntity, AbstractComputeResourceEntity>
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
            final IvoaAbstractComputeResource object,
            final Builder<ExecutionSessionEntity, AbstractComputeResourceEntity> builder
            ){
            super(
                result,
                object,
                builder
                );
            }
        }
    }
