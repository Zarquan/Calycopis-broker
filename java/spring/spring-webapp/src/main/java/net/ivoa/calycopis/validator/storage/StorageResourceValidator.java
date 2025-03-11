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

package net.ivoa.calycopis.validator.storage;

import net.ivoa.calycopis.builder.Builder;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.validator.Validator;

/**
 * Public interface for StorageResource validators and results.
 * 
 */
public interface StorageResourceValidator
extends Validator<IvoaAbstractStorageResource, ExecutionSessionEntity, AbstractStorageResourceEntity>
    {

    /**
     * Public interface for a StorageResourceValidator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractStorageResource, ExecutionSessionEntity, AbstractStorageResourceEntity> 
        {
        // A list of the data resources stored in this resource.
        }

    /**
     * Simple Bean implementation of a StorageResourceValidator result.
     * 
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractStorageResource, ExecutionSessionEntity, AbstractStorageResourceEntity>
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
            final IvoaAbstractStorageResource object,
            final Builder<ExecutionSessionEntity, AbstractStorageResourceEntity> builder
            ){
            super(
                result,
                object,
                builder
                );
            }
        }
    }
