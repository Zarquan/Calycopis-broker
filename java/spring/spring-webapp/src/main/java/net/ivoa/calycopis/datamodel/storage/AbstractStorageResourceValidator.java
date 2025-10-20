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

package net.ivoa.calycopis.datamodel.storage;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;

/**
 * Public interface for StorageResource validators and results.
 * 
 */
public interface AbstractStorageResourceValidator
extends Validator<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
    {
    /**
     * Public interface for an entity builder.
     * 
     */
    public static interface EntityBuilder
        {
        /**
         * Build an entity based on our validation result. 
         *
         */
        public AbstractStorageResourceEntity build(final ExecutionSessionEntity session);
        }

    /**
     * Public interface for a validator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity> 
        {
        /**
         * Create a builder with the validation result.
         * 
         */
        public EntityBuilder getBuilder();

        /**
         * Get the corresponding entity.
         * 
         */
        public AbstractStorageResourceEntity getEntity();

        /**
         * Build an entity based on our validation result. 
         *
         */
        public AbstractStorageResourceEntity build(final ExecutionSessionEntity session);

        /**
         * Add a data resource validation result.
         * 
         */
        public void addDataResourceResult(final AbstractDataResourceValidator.Result result);
        
        /**
         * Get a List of the validated data resources associated with this storage resource.
         * 
         */
        public List<AbstractDataResourceValidator.Result> getDataResourceResults();
        
        }
    
    /**
     * Simple Bean implementation of a StorageResourceValidator result.
     * 
     */
    @Slf4j
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
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
        
        private AbstractStorageResourceEntity entity;
        public AbstractStorageResourceEntity getEntity()
            {
            return this.entity;
            }

        public AbstractStorageResourceEntity build(final ExecutionSessionEntity session)
            {
            this.entity = this.builder.build(
                session
                );
            return this.entity;
            }
        
        private List<AbstractDataResourceValidator.Result> dataResourceResults = new ArrayList<AbstractDataResourceValidator.Result>();
        @Override
        public void addDataResourceResult(AbstractDataResourceValidator.Result result)
            {
            this.dataResourceResults.add(
                result
                );
            }
        @Override
        public List<AbstractDataResourceValidator.Result> getDataResourceResults()
            {
            return this.dataResourceResults;
            }

        @Override
        public Long getTotalPreparationTime()
            {
            log.debug("AbstractStorageResourceValidator.getTotalPrepareTime() [{}]", this.getIdent());
            
            Long maxDataPrepareTime = 0L;
            for (AbstractDataResourceValidator.Result dataResult : this.getDataResourceResults())
                {
                Long dataPrepareTime = dataResult.getPreparationTime();
                log.debug("Data prepare time [{}][{}]", dataResult.getIdent(), dataPrepareTime);
                if ((dataPrepareTime != null) && (dataPrepareTime > maxDataPrepareTime))
                    {
                    maxDataPrepareTime = dataPrepareTime;
                    }
                }
            return this.getPreparationTime() + maxDataPrepareTime;
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
    
    @Override
    public Result validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        );
    }
