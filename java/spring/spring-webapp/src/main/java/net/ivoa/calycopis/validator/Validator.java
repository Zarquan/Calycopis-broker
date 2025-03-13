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
package net.ivoa.calycopis.validator;

import net.ivoa.calycopis.builder.Builder;
import net.ivoa.calycopis.offerset.OfferSetRequestParserContext;

/**
 * Public interface for a Validator.
 *  
 */
public interface Validator<ObjectType, EntityType>
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
         * Get the corresponding Builder to build an entity.
         * 
         */
        public Builder<EntityType> getBuilder();
        
        }

    /**
     * Simple bean implementation of Result.
     *  
     */
    public static class ResultBean<ObjectType, EntityType>
    implements Result<ObjectType, EntityType>
        {
        public ResultBean(final ResultEnum result)
            {
            this(result, null, null);
            }

        public ResultBean(final ResultEnum result, ObjectType object, final Builder<EntityType> builder)
            {
            this.result = result;
            this.object = object;
            this.builder = builder;
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
        private final Builder<EntityType> builder;
        @Override
        public Builder<EntityType> getBuilder()
            {
            return this.builder;
            }
        }

    /**
     * Validate a component.
     *
     */
    public Validator.Result<ObjectType, EntityType> validate(
        final ObjectType requested,
        final OfferSetRequestParserContext context
        );
    }
