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
package net.ivoa.calycopis.functional.validator;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;

/**
 * Public interface for a Validator.
 *  
 */
public interface Validator<ObjectType, EntityType extends ComponentEntity>
    {
    /**
     * Result enum for the validation process.
     * CONTINUE means this validator didn't recognise the object. 
     * ACCEPTED means this validator recognised and validated the object. 
     * FAILED means this validator recognised but failed the object.
     * 
     */
    enum ResultEnum{
        CONTINUE(),
        ACCEPTED(),
        FAILED();
        }

    /**
     * Public interface for a validation result.
     * 
     */
    public static interface Result<ObjectType, EntityType>
        {
        /**
         * Get the object identifier.
         * 
         */
        public String getIdent();

        /**
         * Get the validation result enum.
         * 
         */
        public ResultEnum getEnum();

        /**
         * Get the validated object.
         * 
         */
        public ObjectType getObject();
        
        /**
         * Get the corresponding Entity.
         * 
         */
        public EntityType getEntity();

        /**
         * Get the preparation time for this resource.
         *  
         */
        public Long getPreparationTime();

        /**
         * Get the total preparation time for this resource.
         *  
         */
        public Long getTotalPreparationTime();
        
        }

    /**
     * Validate a component.
     *
     */
    public void validate(
        final ObjectType requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Simple bean implementation of Result.
     *  
     */
    public static class ResultBean<ObjectType, EntityType extends ComponentEntity>
    implements Result<ObjectType, EntityType>
        {
        public ResultBean(final ResultEnum result)
            {
            this(result, null);
            }

        public ResultBean(final ResultEnum result, final ObjectType object)
            {
            this.result = result;
            this.object = object;
            }

        private final ResultEnum result;
        @Override
        public ResultEnum getEnum()
            {
            return this.result;
            }

        private final ObjectType object;
        @Override
        public ObjectType getObject()
            {
            return this.object;
            }
        
        protected EntityType entity;
        @Override
        public EntityType getEntity()
            {
            return this.entity;
            }
        
        @Override
        public Long getPreparationTime()
            {
            return 0L;
            }

        @Override
        public Long getTotalPreparationTime()
            {
            return this.getPreparationTime();
            }

        @Override
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
            /*
             * 
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
             * 
             */
            return "unknown";
            }
        }
    }
