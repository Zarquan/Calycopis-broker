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

import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
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
     * Public interface for an entity builder.
     * 
     */
    public static interface EntityBuilder
        {
        /**
         * Build an entity based on a validation result. 
         *
         */
        public AbstractComputeResourceEntity build(final ExecutionSessionEntity session, final ComputeResourceOffer offer);
        }
   
    /**
     * Public interface for a validator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity> 
        {
        // TODO A list of the volume mounts ...
        /**
         * Create a builder with the validation result.
         * 
         */
        public EntityBuilder getBuilder();
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
            final IvoaAbstractComputeResource object,
            final EntityBuilder builder
            ){
            super(
                result,
                object
                );
            this.builder = builder;
            }
        
        private EntityBuilder builder ;
        public EntityBuilder getBuilder()
            {
            return this.builder;
            }

        // TODO Move this to the base class.
        private AbstractComputeResourceEntity entity;
        public AbstractComputeResourceEntity getEntity()
            {
            return this.entity;
            }

        // TODO Move this to the base class.
        public AbstractComputeResourceEntity build(final ExecutionSessionEntity session, final ComputeResourceOffer offer)
            {
            this.entity = this.builder.build(
                session,
                offer
                );
            return this.entity;
            }
        
        // TODO Move this to the base class.
        public String getIdent()
            {
            if (this.getEntity() != null)
                {
                if (this.getEntity().getUuid() != null)
                    {
                    return this.getEntity().getUuid().toString();
                    }
                else if (this.getEntity().getName() != null)
                    {
                    return this.getEntity().getName();
                    }
                }
            if (this.getObject() != null)
                {
                if (this.getObject().getUuid() != null)
                    {
                    return this.getObject().getUuid().toString();
                    }
                else if (this.getObject().getName() != null)
                    {
                    return this.getObject().getName();
                    }
                }
            return "unknown";
            }
        }
    }
