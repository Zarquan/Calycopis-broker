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

        /*
        inner.prePreparing(
            uuid
            );
         */
        
        boolean success = false;
        for (int count = 0 ; ((success  == false) && (count < 1000)); count++)
            {
            success = inner.setPreparing(
                uuid
                );
            if (success == false)
                {
                try {
                    log.debug("Session [{}] setPreparing() sleep", uuid);
                    Thread.sleep(1);
                    log.debug("Session [{}] setPreparing() awake", uuid);
                    }
                catch (Exception ouch)
                    {
                    log.error("Session [{}] Exception during setPreparing() sleep [{}]", uuid, ouch.getMessage());
                    }
                }
            }
        
        inner.doPreparing(
            uuid
            );

        inner.setAvailable(
            uuid
            );

        inner.doAvailable(
            uuid
            );

        /*
        inner.setRunning(
            uuid
            );
        
        inner.doRunning(
            uuid
            );
         */

        inner.setReleasing(
            uuid
            );

        inner.doReleasing(
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
        
        /*
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
         */
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        boolean setPreparing(final UUID uuid)
            {
            log.debug("Session [{}] setPreparing()", uuid);
            boolean success = false ;
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case OFFERED:
                    log.debug("Session [{}] setPreparing() phase is still OFFERED", session.getUuid());
                    success = false ;
                    break;
                case ACCEPTED:
                    log.debug("Session [{}] setPreparing() phase is [ACCEPTED]", session.getUuid());
                    success = true ;
                    session.setPhase(
                        IvoaExecutionSessionPhase.PREPARING
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    log.debug("Session [{}] setPreparing() phase changed [ACCEPTED]->[PREPARING]", session.getUuid());
                    break;
                default:
                    // Technically not true, but we want to break out of the loop.
                    success = true ;
                    log.error("Session [{}] setPreparing() invalid transition [{}]->[PREPARING]", session.getUuid(), session.getPhase());
                    break;
                }
    
            // TODO What do we do if the state it still OFFERED ?
            // If it takes too long we need to fail the session.
            // We can use PrepareStartInstant as a time limit.
            return success ; 
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
        void setAvailable(final UUID uuid)
            {
            log.debug("Session [{}] setAvailable()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case PREPARING:
                    log.debug("Session [{}] setAvailable() phase changed [{}]->[AVAILABLE]", session.getUuid(), session.getPhase());
                    session.setPhase(
                        IvoaExecutionSessionPhase.AVAILABLE
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    break;
                default:
                    log.error("Session [{}] setAvailable() invalid transition [{}]->[AVAILABLE]", session.getUuid(), session.getPhase());
                    break;
                }
            log.debug("Session [{}] setAvailable() done", session.getUuid());
            } 

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doAvailable(final UUID uuid)
            {
            log.debug("Session [{}] doAvailable()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
    
            /*
             * Keep the session available for the time we offered to.
            long target = session.getAvailableStartInstantSeconds() + session.getAvailableDurationSeconds();
            long delta  = target - Instant.now().getEpochSecond();
            Instant instant = Instant.ofEpochSecond(target);
             */

            Instant instant = session.getReleaseStartInstant();
            long target = session.getReleaseStartInstantSeconds();
            long delta = target - Instant.now().getEpochSecond();

            if (delta > 0)
                {
                log.debug("Session [{}] doAvailable() end is in the future [{}][{}]", session.getUuid(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Session [{}] doAvailable() sleeping [{}][{}]", session.getUuid(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Session [{}] doAvailable() awake [{}][{}]", session.getUuid(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Session [{}] doAvailable() Exception during sleep", session.getUuid(), ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Session [{}] doAvailable() end is already past [{}][{}]", session.getUuid(), instant, delta);
                }
            }

        /*
         * 
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setRunning(final UUID uuid)
            {
            log.debug("Session [{}] setRunning()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case AVAILABLE:
                    log.debug("Session [{}] setRunning() phase changed [{}]->[RUNNING]", session.getUuid(), session.getPhase());
                    session.setPhase(
                        IvoaExecutionSessionPhase.RUNNING
                        );
                    session = this.sessionRepository.save(
                        session
                        );
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
    
            // TODO ReleaseStart is not set.
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
         * 
         */

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void setReleasing(final UUID uuid)
            {
            log.debug("Session [{}] setReleasing()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
            switch (session.getPhase())
                {
                case AVAILABLE:
                case RUNNING:
                    log.debug("Session [{}] setReleasing() phase changed [{}]->[RELEASING]", session.getUuid(), session.getPhase());
                    session.setPhase(
                        IvoaExecutionSessionPhase.RELEASING
                        );
                    session = this.sessionRepository.save(
                        session
                        );
                    break;
                default:
                    log.error("Session [{}] setReleasing() invalid transition [{}]->[RELEASING]", session.getUuid(), session.getPhase());
                    break;
                }
            log.debug("Session [{}] setReleasing() done", session.getUuid());
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void doReleasing(final UUID uuid)
            {
            log.debug("Session [{}] doReleasing()", uuid);
            SessionEntity session = sessionRepository.findById(
                uuid
                ).orElseThrow();
    
            Instant  instant = session.getReleaseStartInstant().plusSeconds(session.getReleaseDurationSeconds());
            long target = session.getReleaseStartInstantSeconds() + session.getReleaseDurationSeconds();
            long delta = target - Instant.now().getEpochSecond();
    
            if (delta > 0)
                {
                log.debug("Session [{}] doReleasing() end is in the future [{}][{}]", session.getUuid(), instant, delta);
                while (delta > 0)
                    {
                    try {
                        log.debug("Session [{}] doReleasing() sleeping [{}][{}]", session.getUuid(), instant, delta);
                        Thread.sleep(delta * 500);
                        log.debug("Session [{}] doReleasing() awake [{}][{}]", session.getUuid(), instant, delta);
                        }
                    catch (Exception ouch)
                        {
                        log.error("Session [{}] doReleasing() Exception during sleep", session.getUuid(), ouch.getMessage());
                        }
                    delta = target - Instant.now().getEpochSecond();
                    }
                }
            else {
                log.debug("Session [{}] doReleasing() end is already past [{}][{}]", session.getUuid(), instant, delta);
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
                case AVAILABLE:
                case RUNNING:
                case RELEASING:
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
