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

package net.ivoa.calycopis.functional.processing;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Service
public abstract class AbstractProcessorServiceImpl
extends FactoryBaseImpl
implements AbstractProcessorService
    {

    protected AbstractProcessorServiceImpl()
        {
        super();
        }

    @Autowired
    private TransactionalInnerClass inner ;
    
    public void loop()
        {
        log.info("++++++++");
        log.debug("Starting processing loop [{}]", this.getUuid());
        UUID found = inner.getNext(this) ;
        //log.debug("Found processing task [{}]", found);
        while (found != null)
            {
            inner.process(    
                this,
                found
                );
            found = inner.getNext(this) ;
            }
        log.debug("Exiting processing loop [{}]", this.getUuid());
        log.info("--------");
        }
    
    @Service
    public static class TransactionalInnerClass
        {
        @Autowired
        private AbstractProcessorEntityRepository repository;

        TransactionalInnerClass()
            {
            super();
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        protected UUID getNext(final AbstractProcessorService service)
            {
            // Find the next active processor with this thread ID.
            log.debug("Finding next active task for service [{}]", service.getUuid());
            UUID found = this.repository.selectNextActive(
                service.getUuid(),
                service.getKinds()
                );
            // If we didn't find an active processor, try to claim one.
            if (found == null)
                {
                log.debug("No active tasks found for service [{}]", service.getUuid());
                // Claim the next active processor.
                log.debug("Claiming next active task for service [{}]", service.getUuid());
                int count = this.repository.updateNextActive(
                    service.getUuid(),
                    service.getKinds()
                    ) ;
                // Try again.
                log.debug("[{}] tasks claimed for service [{}]", count, service.getUuid());
                if (count > 0)
                    {
                    log.debug("Finding next active task for service [{}]", service.getUuid());
                    found = this.repository.selectNextActive(
                        service.getUuid(),
                        service.getKinds()
                        ) ;
                    }
                }

            if (found != null)
                {
                log.debug("Found active task [{}] for service [{}]", found, service.getUuid());
                }
            else {
                log.debug("No active tasks for service [{}]", service.getUuid());
                }
            
            return found ;
            }
        
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        protected void process(final AbstractProcessorService service, final UUID uuid)
            {
            AbstractProcessorEntity entity = this.repository.findById(uuid).orElseThrow();
            log.debug("Processing service [{}] processing task [{}][{}]", service.getUuid(), entity.getUuid(), entity.getClass().getSimpleName());
            entity.process();
            log.debug("Saving task [{}] with activation [{}]", entity.getUuid(), entity.getActivation());
            repository.save(entity);
            }
        }
    }
