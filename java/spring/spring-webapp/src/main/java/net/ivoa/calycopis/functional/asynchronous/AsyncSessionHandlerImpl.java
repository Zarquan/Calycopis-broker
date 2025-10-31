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
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntityRepository;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResource;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * AsyncSessionHandler runs it's process() method in a new Thread. 
 *  
 */
@Slf4j
@Service
public class AsyncSessionHandlerImpl
extends FactoryBaseImpl
implements AsyncSessionHandler
    {

    private final InnerSessionHandler inner ;

    @Autowired
    AsyncSessionHandlerImpl(
        final InnerSessionHandler inner
        ){
        this.inner = inner;
        }

    @Override
    @Async("TaskExecutor-21")
    public void process(final UUID uuid)
        {
        log.debug("Session process(UUID) [{}]", uuid);

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
        inner.setReady(
            uuid
            );
        
        }

    /**
     * We need this inner class registered as a Component so that the methods are wrapped in a Hibernate Session.
     * This avoids encountering a LazyInitializationException when we access the list of storage resources.
     *  
     */
    @Slf4j
    @Component
    static class InnerSessionHandler
        {

        private final SessionEntityRepository repository;

        private final AsyncLifecycleComponentHandler lifecycler;

        @Autowired
        InnerSessionHandler(
            final SessionEntityRepository repository,
            final AsyncLifecycleComponentHandler lifecycler
            ){
            this.repository = repository;
            this.lifecycler = lifecycler;
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void prePreparing(final UUID uuid)
            {
            log.debug("prePreparing(UUID) [{}]", uuid);
            SessionEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Session found [{}][{}]", entity.getUuid(), entity.getPhase());
    
            Instant prepare = entity.getPrepareStartInstant();
            long target = entity.getPrepareStartInstantSeconds();
            long delta = target - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Prepare start is in the future [{}][{}][{}]", uuid, prepare, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Sleeping [{}][{}][{}]", uuid, prepare, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Awake [{}][{}]", uuid, prepare);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Exception during sleep", uuid, ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Prepare start is already past [{}][{}][{}]", uuid, prepare, delta );
                // Do we fail if it is too late ?
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setPreparing(final UUID uuid)
            {
            log.debug("setPreparing(UUID) [{}]", uuid);
            boolean loop = true;
            for (int count = 0 ; (loop && (count < 10)); count++)
                {
                log.debug("preparing loop [{}]", uuid);
                SessionEntity entity = repository.findById(
                    uuid
                    ).orElseThrow();
                log.debug("Session found [{}][{}]", entity.getUuid(), entity.getPhase());
                switch (entity.getPhase())
                    {
                    case OFFERED:
                        log.debug("Phase is still OFFERED [{}]", uuid);
                        loop = true ;
                        try {
                            log.debug("Sleeping [{}]", uuid);
                            Thread.sleep(10);
                            log.debug("Awake [{}]", uuid);
                            }
                        catch (Exception ouch)
                            {
                            log.error("Exception during sleep", uuid, ouch.getMessage());
                            }
                        break;
                    case ACCEPTED:
                        log.debug("Phase is ACCEPTED [{}]", uuid);
                        loop = false ;
                        entity.setPhase(
                            IvoaExecutionSessionPhase.PREPARING
                            );
                        entity = this.repository.save(
                            entity
                            );
                        log.debug("Phase changed to PREPARING [{}]", uuid);
                        break;
                    default:
                        loop = false ;
                        log.error("Invalid phase transition [{}][{}][{}]", uuid, entity.getPhase(), IvoaExecutionSessionPhase.PREPARING);
                        break;
                    }
                } 
    
            // TODO What do we do if the state it still OFFERED ?
            // If it takes too long we need to fail the session.
            // We can use PrepareStartInstant as a time limit.
            
            }
    
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setReady(final UUID uuid)
            {
            log.debug("setReady(UUID) [{}]", uuid);
            SessionEntity entity = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Session found [{}][{}]", entity.getUuid(), entity.getPhase());
            switch (entity.getPhase())
                {
                case PREPARING:
                    log.debug("Phase is PREPARING [{}]", uuid);
                    entity.setPhase(
                        IvoaExecutionSessionPhase.READY
                        );
                    entity = this.repository.save(
                        entity
                        );
                    log.debug("Phase changed to READY [{}]", uuid);
                    break;
                default:
                    log.error("Invalid phase transition [{}][{}][{}]", uuid, entity.getPhase(), IvoaExecutionSessionPhase.READY);
                    break;
                }
            } 
    
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doPreparing(final UUID uuid)
            {
            log.debug("doPreparing(UUID) [{}]", uuid);
            SessionEntity session = repository.findById(
                uuid
                ).orElseThrow();
            log.debug("Session found [{}][{}]", session.getUuid(), session.getPhase());
            
            final AtomicInteger preparing = new AtomicInteger();
            
            AbstractExecutableEntity executable = session.getExecutable();
            log.debug("Executable found [{}][{}]", executable.getUuid(), executable.getPhase());
    
            log.debug("Preparing executable [{}]", executable.getUuid());
            preparing.incrementAndGet();
            lifecycler.prepare(executable.getUuid(), preparing);
    
            log.debug("Preparing session storage [{}]", session.getUuid());
            for (AbstractStorageResource storage : session.getStorageResources())
                {
                log.debug("Preparing session storage [{}][{}]", session.getUuid(), storage.getUuid());
                preparing.incrementAndGet();
                lifecycler.prepare(storage.getUuid(), preparing);
                }
            
            log.debug("Checking count [{}][{}]", uuid, preparing);
            while(preparing.get() > 0)
                {
                log.debug("Waiting for notify [{}][{}]", uuid, preparing);
                try {
                    // TODO Adjust wait based on the expected duration.
                    // TODO Exit and fail if we are too late. 
                    synchronized (preparing)
                        {
                        preparing.wait(10000);
                        }
                    }
                catch (Exception ouch)
                    {
                    log.error("Exception during sleep [{}][{}]", uuid, ouch.getMessage());
                    }
                log.debug("Awake from notify [{}][{}]", uuid, preparing);
                }
            log.debug("Prepare done [{}]", session.getUuid());
            }
        }
    }
