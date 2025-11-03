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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResource;
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
@Component
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
        log.debug("Processing session [{}]", uuid);

        inner.prePreparing(
            uuid
            );
        
        inner.setPreparing(
            uuid
            );

        inner.doPreparing(
            uuid
            );

        inner.setReady(
            uuid
            );

        inner.preRunning(
            uuid
            );

        inner.setRunning(
            uuid
            );
        
        inner.doRunning(
            uuid
            );
        
        inner.setCompleted(
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

        private final SessionEntityRepository sessionRepository;
        private final AsyncComputeHandler computeHandler;
        private final AsyncExecutableHandler executableHandler;
        private final AsyncStorageResourceHandler storageHandler;

        @Autowired
        InnerSessionHandler(
            final SessionEntityRepository sessionRepository,
            final AsyncComputeHandler computeHandler,
            final AsyncExecutableHandler executableHandler,
            final AsyncStorageResourceHandler storageHandler
            ){
            this.sessionRepository = sessionRepository;
            this.computeHandler = computeHandler;
            this.executableHandler = executableHandler;
            this.storageHandler = storageHandler;
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void prePreparing(final UUID uuid)
            {
            log.debug("Session [{}] prePreparing()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
    
            Instant instant = session.getPrepareStartInstant();
            long target = session.getPrepareStartInstantSeconds();
            long delta = target - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Session [{}] prePreparing() start is in the future [{}][{}]", session.getUuid(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Session [{}] prePreparing() sleeping [{}][{}]", session.getUuid(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Session [{}] prePreparing() awake [{}][{}]", session.getUuid(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Session [{}] prePreparing() Exception during sleep", session.getUuid(), ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Session [{}] prePreparing() start is already past [{}][{}]", session.getUuid(), instant, delta);
                // Do we fail if it is too late ?
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setPreparing(final UUID uuid)
            {
            log.debug("Session [{}] setPreparing()", uuid);
            boolean loop = true;
            for (int count = 0 ; (loop && (count < 10)); count++)
                {
                SessionEntity session = sessionRepository.findById(
                    uuid
                    ).orElseThrow();
                switch (session.getPhase())
                    {
                    case OFFERED:
                        log.debug("Session [{}] setPreparing() phase is still OFFERED", session.getUuid());
                        loop = true ;
                        try {
                            log.debug("Session [{}] setPreparing() sleep", session.getUuid());
                            Thread.sleep(10);
                            log.debug("Session [{}] setPreparing() awake", session.getUuid());
                            }
                        catch (Exception ouch)
                            {
                            log.error("Session [{}] Exception during setPreparing() sleep [{}]", session.getUuid(), ouch.getMessage());
                            }
                        break;
                    case ACCEPTED:
                        log.debug("Session [{}] setPreparing() phase is [ACCEPTED]", session.getUuid());
                        loop = false ;
                        session.setPhase(
                            IvoaExecutionSessionPhase.PREPARING
                            );
                        session = this.sessionRepository.save(
                            session
                            );
                        log.debug("Session [{}] setPreparing() phase changed [ACCEPTED]->[PREPARING]", session.getUuid());
                        break;
                    default:
                        loop = false ;
                        log.error("Session [{}] setPreparing() invalid transition [{}]->[PREPARING]", session.getUuid(), session.getPhase());
                        break;
                    }
                } 
    
            // TODO What do we do if the state it still OFFERED ?
            // If it takes too long we need to fail the session.
            // We can use PrepareStartInstant as a time limit.
            log.debug("Session [{}] setPreparing() done", uuid);
            }
    
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doPreparing(final UUID uuid)
            {
            log.debug("Session [{}] doPreparing()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            
            final AtomicInteger preparing = new AtomicInteger();
            
            AbstractExecutableEntity executable = session.getExecutable();
            preparing.incrementAndGet();
            log.debug("Session [{}] doPreparing() executable [{}][{}][{}]", session.getUuid(), executable.getName(), executable.getUuid(), preparing);
            executableHandler.prepare(
                executable.getUuid(),
                preparing
                );

            //
            // TODO compute should wait for the executable to be ready.
            // TODO compute should wait for storage and data to be ready.
            AbstractComputeResourceEntity compute = session.getComputeResource();
            preparing.incrementAndGet();
            log.debug("Session [{}] doPreparing() compute [{}][{}][{}]", session.getUuid(), compute.getName(), compute.getUuid(), preparing);
            computeHandler.prepare(
                compute.getUuid(),
                preparing
                );
            
            for (AbstractDataResource data : session.getDataResources())
                {
                preparing.incrementAndGet();
                log.debug("Session [{}] doPreparing() data [{}][{}][{}]", session.getUuid(), data.getName(), data.getUuid(), preparing);
                }
            
            for (AbstractStorageResource storage : session.getStorageResources())
                {
                preparing.incrementAndGet();
                log.debug("Session [{}] doPreparing() storage [{}][{}][{}]", session.getUuid(), storage.getName(), storage.getUuid(), preparing);
                storageHandler.prepare(
                    storage.getUuid(),
                    preparing
                    );
                }
            
            while(preparing.get() > 0)
                {
                try {
                    // TODO Adjust wait based on the expected duration.
                    // TODO Exit and fail if we are too late. 
                    log.debug("Session [{}] waiting on counter [{}]", session.getUuid(), preparing);
                    synchronized (preparing)
                        {
                        preparing.wait(10000);
                        }
                    log.debug("Session [{}] awake from wait [{}]", session.getUuid(), preparing);
                    }
                catch (Exception ouch)
                    {
                    log.error("Session [{}] Exception during wait [{}]", session.getUuid(), ouch.getMessage());
                    }
                }
            log.debug("Session [{}] doPreparing() done", session.getUuid());
            }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setReady(final UUID uuid)
            {
            log.debug("Session [{}] setReady()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case PREPARING:
                    session.setPhase(
                        IvoaExecutionSessionPhase.READY
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    log.debug("Session [{}] setReady() phase changed [PREPARING]->[READY]", session.getUuid());
                    break;
                default:
                    log.error("Session [{}] setReady() invalid transition [{}]->[READY]", session.getUuid(), session.getPhase());
                    break;
                }
            log.debug("Session [{}] setReady() done", session.getUuid());
            } 

        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void preRunning(final UUID uuid)
            {
            log.debug("Session [{}] preRunning()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
    
            Instant instant = session.getAvailableStartInstant();
            long target = session.getAvailableStartInstantSeconds();
            long delta = target - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Session [{}] preRunning() start is in the future [{}][{}]", session.getUuid(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Session [{}] preRunning() sleeping [{}][{}]", session.getUuid(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Session [{}] preRunning() awake [{}][{}]", session.getUuid(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Session [{}] preRunning() Exception during sleep", session.getUuid(), ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Session [{}] preRunning() start is already past [{}][{}]", session.getUuid(), instant, delta);
                // Do we fail if it is too late ?
                // How late is too late ?
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setRunning(final UUID uuid)
            {
            log.debug("Session [{}] setRunning()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case READY:
                    session.setPhase(
                        IvoaExecutionSessionPhase.RUNNING
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    log.debug("Session [{}] setRunning() phase changed [READY]->[RUNNING]", session.getUuid());
                    break;
                default:
                    log.error("Session [{}] setRunning() invalid transition [{}]->[RUNNING]", session.getUuid(), session.getPhase());
                    break;
                }
            log.debug("Session [{}] setRunning() done", session.getUuid());
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doRunning(final UUID uuid)
            {
            log.debug("Session [{}] doRunning()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
    
            Instant instant = session.getReleaseStartInstant();
            long target = session.getReleaseStartInstantSeconds();
            long delta = target - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Session [{}] doRunning() end is in the future [{}][{}]", session.getUuid(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Session [{}] doRunning() sleeping [{}][{}]", session.getUuid(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Session [{}] doRunning() awake [{}][{}]", session.getUuid(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Session [{}] doRunning() Exception during sleep", session.getUuid(), ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Session [{}] doRunning() end is already past [{}][{}]", session.getUuid(), instant, delta);
                }
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setCompleted(final UUID uuid)
            {
            log.debug("Session [{}] setCompleted()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case RUNNING:
                    session.setPhase(
                        IvoaExecutionSessionPhase.COMPLETED
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    log.debug("Session [{}] setCompleted() phase changed [RUNNING]->[COMPLETED]", session.getUuid());
                    break;
                default:
                    log.error("Session [{}] setCompleted() invalid transition [{}]->[COMPLETED]", session.getUuid(), session.getPhase());
                    break;
                }
            log.debug("Session [{}] setCompleted() done", session.getUuid());
            }
        
        }
    }
