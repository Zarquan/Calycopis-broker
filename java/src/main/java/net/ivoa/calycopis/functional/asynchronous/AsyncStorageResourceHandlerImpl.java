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

package net.ivoa.calycopis.functional.asynchronous;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityRepository;

/**
 * Experiments with asynchronous processing.
 * 
 */
@Component
@Deprecated
public class AsyncStorageResourceHandlerImpl
extends AsyncLifecycleComponentHandlerImpl<AbstractStorageResourceEntity>
implements AsyncStorageResourceHandler
    {
    
    @Autowired
    AsyncStorageResourceHandlerImpl(
        final InnerLifecycleHandler inner
        ){
        super(inner);
        }

    @Slf4j
    @Component
    static class InnerLifecycleHandler
    extends AsyncLifecycleComponentHandlerImpl.InnerLifecycleHandler<AbstractStorageResourceEntity>
        {
        private final AsyncDataResourceHandler dataHandler ;
        private final AbstractStorageResourceEntityRepository storageRepository ;

        @Autowired
        InnerLifecycleHandler(
            final AbstractStorageResourceEntityRepository storageRepository,
            final AsyncDataResourceHandler dataHandler
            ){
            super(storageRepository);
            this.dataHandler = dataHandler;
            this.storageRepository = storageRepository;
            }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doPreparing(UUID uuid, final AtomicInteger counter)
            {
            AbstractStorageResourceEntity entity = this.storageRepository.findById(
                uuid
                ).orElseThrow();
            log.debug("[{}][{}][{}] doPreparing() [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase(), counter);

            //
            // Wait until the storage is available ...
            prepareWait(entity);
            
            }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void donePreparing(UUID uuid, AtomicInteger counter)
            {
            AbstractStorageResourceEntity entity = this.storageRepository.findById(
                uuid
                ).orElseThrow();
            log.debug("[{}][{}][{}] donePreparing() [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase(), counter);

            //
            // Prepare our data resources.
            // Note that the data resources should already be counted.
            log.debug("[{}][{}][{}] data count [{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getDataResources().size());
            
            for (AbstractDataResourceEntity data : entity.getDataResources())
                {
                log.debug("[{}][{}][{}] prepare [{}][{}]", data.getClass().getSimpleName(), data.getUuid(), data.getName(), data.getPhase(), counter);
                dataHandler.prepare(
                    data.getUuid(),
                    counter
                    );
                }
            }
        }
    }
