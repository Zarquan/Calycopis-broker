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

package net.ivoa.calycopis.datamodel.session;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 *
 * TODO Refactor this to use EntityManager.refresh() and Version.
 * https://www.baeldung.com/spring-data-jpa-refresh-fetch-entity-after-save
 *  
 */
@Slf4j
@Service
public class AsyncSessionHandlerImpl
extends FactoryBaseImpl
implements AsyncSessionHandler
    {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final SessionEntityFactory sessionFactory;
    @Autowired
    private final SessionEntityRepository sessionRepository;

    /**
     * 
     */
    public AsyncSessionHandlerImpl(
        final EntityManager entityManager,
        final SessionEntityFactory sessionFactory,
        final SessionEntityRepository sessionRepository
        ){
        this.entityManager = entityManager;
        this.sessionFactory = sessionFactory;
        this.sessionRepository = sessionRepository;
        }

    @Override
    @Async("TaskExecutor-21")
    public void process(final UUID uuid)
        {
        log.debug("Session process(UUID) [{}]", uuid);

        //
        // Wait to begin preparing.
        
        
        log.debug("Begin preparing [{}]", uuid);
        setPreparing(
            uuid
            );

        //
        // Wait for 30 seconds to simulate preparation work.
        
        for (int i = 0 ; i < 60 ; i++)
            {
            try {
                log.debug("Session preparing - sleep [{}]", uuid);
                Thread.sleep(1000);
                log.debug("Session preparing - awake [{}]", uuid);
                }
            catch (Exception ouch)
                {
                log.error("Exception during sleep", uuid, ouch.getMessage());
                }
            }
        
        log.debug("Done preparing [{}]", uuid);
        setReady(
            uuid
            );
        
        }
        
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setPreparing(final UUID uuid)
        {
        log.debug("setPreparing(UUID) [{}]", uuid);
        boolean loop = true;
        for (int count = 0 ; (loop && (count < 10)); count++)
            {
            log.debug("preparing loop [{}]", uuid);
            SessionEntity entity = sessionRepository.findById(
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
                        Thread.sleep(100);
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
                    entity = this.sessionRepository.save(
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
        }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setReady(final UUID uuid)
        {
        log.debug("setReady(UUID) [{}]", uuid);
        SessionEntity entity = sessionRepository.findById(
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
                entity = this.sessionRepository.save(
                    entity
                    );
                log.debug("Phase changed to READY [{}]", uuid);
                break;
            default:
                log.error("Invalid phase transition [{}][{}][{}]", uuid, entity.getPhase(), IvoaExecutionSessionPhase.READY);
                break;
            }
        } 
    }
