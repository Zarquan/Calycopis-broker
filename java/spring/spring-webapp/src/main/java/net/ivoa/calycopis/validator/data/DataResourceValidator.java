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

import net.ivoa.calycopis.builder.Builder;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.validator.Validator;

/**
 * Public interface for DataResource validators and results.
 * 
 */
public interface DataResourceValidator
extends Validator<IvoaAbstractDataResource, ExecutionSessionEntity, AbstractDataResourceEntity>
    {

    /**
     * Public interface for a DataResourceValidator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractDataResource, ExecutionSessionEntity, AbstractDataResourceEntity> 
        {
        // A reference to the storage resource for this data.
        }

    /**
     * Simple Bean implementation of a DataResourceValidator result.
     * 
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractDataResource, ExecutionSessionEntity, AbstractDataResourceEntity>
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
            final IvoaAbstractDataResource object,
            final Builder<ExecutionSessionEntity, AbstractDataResourceEntity> builder
            ){
            super(
                result,
                object,
                builder
                );
            }
        }
    }
