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

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityRepository;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;

/**
 * AsyncLifecycleComponentHandler runs it's prepare() method in a new Thread. 
 *  
 */
@Slf4j
@Deprecated
public abstract class AsyncLifecycleComponentHandlerImpl<EntityType extends LifecycleComponentEntity>
extends FactoryBaseImpl
implements AsyncLifecycleComponentHandler<EntityType>
    {

    private final InnerLifecycleHandler<EntityType> inner;

    AsyncLifecycleComponentHandlerImpl(
        final InnerLifecycleHandler<EntityType> inner
        ){
        this.inner = inner;
        }

    @Override
    @Async("TaskExecutor-21")
    public void prepare(final UUID uuid, final AtomicInteger counter)
        {
        log.debug("Executable process(UUID) [{}][{}]", uuid, counter);

        inner.prePreparing(
            uuid
            );
        
        inner.setPreparing(
            uuid
            );

        inner.doPreparing(
            uuid,
            counter
            );
        
        inner.setAvailable(
            uuid,
            counter
            );

        inner.donePreparing(
            uuid,
            counter
            );

        counter.decrementAndGet();
        log.debug("Notifying all [{}][{}]", uuid, counter);
        synchronized (counter)
            {
            counter.notifyAll();
            }
        }

    /**
     * We need this inner class registered as a Component so that the methods are wrapped in a Hibernate Session.
     * This avoids encountering a LazyInitializationException when we access lists of related resources.
     *  
     */
    @Slf4j
    static abstract class InnerLifecycleHandler<EntityType extends LifecycleComponentEntity>
        {
        protected final LifecycleComponentEntityRepository<EntityType> repository;
        
        InnerLifecycleHandler(
            final LifecycleComponentEntityRepository<EntityType> repository
            ){
            this.repository = repository;
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void prePreparing(final UUID uuid)
            {
            LifecycleComponentEntity entity = repository.findById(
                uuid
                ).orElseThrow();
    
            Instant instant = entity.getPrepareStartInstant();
            long seconds = entity.getPrepareStartInstantSeconds();
            long delta = seconds - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("[{}][{}][{}] prePrepare() start is in the future [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("[{}][{}][{}] prePrepare() sleep [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("[{}][{}][{}] prePrepare() awake [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("[{}][{}][{}] prePrepare() Exception during sleep [{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), ouch.getMessage());
                        }
                    delta = seconds - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("[{}][{}][{}] prePrepare() start is in the past [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta );
                // Do we fail if it is too late ?
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setPreparing(final UUID uuid)
            {
            EntityType entity = repository.findById(
                uuid
                ).orElseThrow();
            switch (entity.getPhase())
                {
                case INITIALIZING:
                case WAITING:
                    log.debug("[{}][{}][{}] setPreparing() phase changed [{}]->[PREPARING]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase());
                    entity.setPhase(
                        IvoaLifecyclePhase.PREPARING
                        );
                    entity = this.repository.save(
                        entity
                        );
                    break;
                default:
                    log.error("[{}][{}][{}] setPreparing() invalid transition [{}]->[PREPARING]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase());
                    break;
                }
            }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setAvailable(final UUID uuid, final AtomicInteger counter)
            {
            EntityType entity = repository.findById(
                uuid
                ).orElseThrow();
            switch (entity.getPhase())
                {
                case PREPARING:
                    log.debug("[{}][{}][{}] setAvailable() phase changed [{}]->[AVAILABLE]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase());
                    entity.setPhase(
                        IvoaLifecyclePhase.AVAILABLE
                        );
                    entity = this.repository.save(
                        entity
                        );
                    break;
                default:
                    log.error("[{}][{}][{}] setAvailable() invalid transition [{}]->[AVAILABLE]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), entity.getPhase());
                    break;
                }
            } 

        abstract void doPreparing(final UUID uuid, final AtomicInteger counter);

        abstract void donePreparing(final UUID uuid, final AtomicInteger counter);

        //
        // Placeholder that waits until the component should be available.
        void prepareWait(final LifecycleComponentEntity entity)
            {
            Instant instant = entity.getAvailableStartInstant();
            long seconds = entity.getAvailableStartInstantSeconds();
            long delta = seconds - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("[{}][{}][{}] prepareWait() start is in the future [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("[{}][{}][{}] prepareWait() sleep [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("[{}][{}][{}] prepareWait() awake [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("[{}][{}][{}] prepareWait() Exception during sleep [{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), ouch.getMessage());
                        }
                    delta = seconds - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("[{}][{}][{}] prepareWait() start is in the past [{}][{}]", entity.getClass().getSimpleName(), entity.getUuid(), entity.getName(), instant, delta);
                // Do we fail if it is too late ?
                }
            }
        }
    }
