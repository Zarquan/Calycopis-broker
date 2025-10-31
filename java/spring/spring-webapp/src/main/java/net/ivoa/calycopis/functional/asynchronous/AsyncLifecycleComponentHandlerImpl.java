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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
@Service
public class AsyncLifecycleComponentHandlerImpl
extends FactoryBaseImpl
implements AsyncLifecycleComponentHandler
    {

    private final InnerLifecycleHandler inner;

    @Autowired
    AsyncLifecycleComponentHandlerImpl(
        final InnerLifecycleHandler inner
        ){
        this.inner = inner;
        }

    @Override
    @Async("TaskExecutor-21")
    public void prepare(final UUID uuid, final AtomicInteger counter)
        {
        log.debug("Executable process(UUID) [{}][{}]", uuid, counter);

        log.debug("Before preparing [{}]", uuid);
        inner.prePreparing(
            uuid
            );
        
        log.debug("Start preparing [{}]", uuid);
        inner.setPreparing(
            uuid
            );

        log.debug("Do preparing [{}]", uuid);
        inner.doPreparing(
            uuid
            );
        
        log.debug("Done preparing [{}]", uuid);
        inner.setAvailable(
            uuid
            );
        counter.decrementAndGet();
        synchronized (counter)
            {
            log.debug("Notifying all [{}]", uuid);
            counter.notifyAll();
            }
        }

    /**
     * We need this inner class registered as a Component so that the methods are wrapped in a Hibernate Session.
     * This avoids encountering a LazyInitializationException when we access lists of related resources.
     *  
     */
    @Slf4j
    @Component
    static class InnerLifecycleHandler
        {
        private final LifecycleComponentEntityRepository repository;
        
        @Autowired
        InnerLifecycleHandler(
            final LifecycleComponentEntityRepository repository
            ){
            this.repository = repository;
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void prePreparing(final UUID uuid)
            {
            log.debug("prePreparing(UUID) [{}]", uuid);
            LifecycleComponentEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Entity found [{}][{}][{}][{}]", entity.getUuid(), entity.getClass().getSimpleName(), entity.getName(),  entity.getPhase());
    
            Instant instant = entity.getPrepareStartInstant();
            long seconds = entity.getPrepareStartInstantSeconds();
            long delta = seconds - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Prepare start is in the future [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Sleeping [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Awake [{}][{}][{}]", uuid, entity.getName(), instant);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Exception during sleep", uuid, ouch.getMessage());
                        }
                    delta = seconds - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Prepare start is already past [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta );
                // Do we fail if it is too late ?
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setPreparing(final UUID uuid)
            {
            log.debug("setPreparing(UUID) [{}]", uuid);
            LifecycleComponentEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Entity found [{}][{}][{}][{}]", entity.getUuid(), entity.getClass().getSimpleName(), entity.getName(),  entity.getPhase());
            switch (entity.getPhase())
                {
                case INACTIVE:
                    log.debug("Phase is INACTIVE [{}][{}]", uuid, entity.getName());
                    entity.setPhase(
                        IvoaLifecyclePhase.PREPARING
                        );
                    entity = this.repository.save(
                        entity
                        );
                    log.debug("Phase changed to PREPARING [{}][{}]", uuid, entity.getName());
                    break;
                default:
                    log.error("Invalid phase transition [{}][{}][{}][{}]", uuid, entity.getName(), entity.getPhase(), IvoaLifecyclePhase.PREPARING);
                    break;
                }
            }
    
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setAvailable(final UUID uuid)
            {
            log.debug("setReady(UUID) [{}]", uuid);
            LifecycleComponentEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Entity found [{}][{}][{}][{}]", entity.getUuid(), entity.getClass().getSimpleName(), entity.getName(),  entity.getPhase());
            switch (entity.getPhase())
                {
                case PREPARING:
                    log.debug("Phase is PREPARING [{}][{}]", uuid, entity.getName());
                    entity.setPhase(
                        IvoaLifecyclePhase.AVAILABLE
                        );
                    entity = this.repository.save(
                        entity
                        );
                    log.debug("Phase changed to READY [{}][{}]", uuid, entity.getName());
                    break;
                default:
                    log.error("Invalid phase transition [{}][{}][{}][{}]", uuid, entity.getName(), entity.getPhase(), IvoaLifecyclePhase.AVAILABLE);
                    break;
                }
            } 
    
        //
        // Placeholder that waits until the component is available.
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doPreparing(final UUID uuid)
            {
            log.debug("doPreparing(UUID) [{}]", uuid);
            LifecycleComponentEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Entity found [{}][{}][{}][{}]", entity.getUuid(), entity.getClass().getSimpleName(), entity.getName(),  entity.getPhase());
    
            Instant instant = entity.getAvailableStartInstant();
            long seconds = entity.getAvailableStartInstantSeconds();
            long delta = seconds - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Available start is in the future [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Sleeping [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Awake [{}][{}][{}]", uuid, entity.getName(), instant);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Exception during sleep", uuid, ouch.getMessage());
                        }
                    delta = seconds - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Available start is already past [{}][{}][{}][{}]", uuid, entity.getName(), instant, delta );
                // Do we fail if it is too late ?
                }
            }
        }
    }
