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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntityFactory;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * Experimental implementation of a background task.
 * https://www.geeksforgeeks.org/advance-java/spring-boot-handling-background-tasks-with-spring-boot/
 *  
 */
@Slf4j
@Service
public class ScheduledTaskService
    {

    private final ExecutionSessionEntityFactory factory ;
    private final AsyncTaskService other ;
    
    /**
     * 
     */
    @Autowired
    public ScheduledTaskService(final ExecutionSessionEntityFactory factory, final AsyncTaskService other)
        {
        super();
        this.factory = factory;
        this.other = other ;
        }

    @Scheduled(fixedRate = 20000)
    public void performScheduledTask()
        {
        log.debug("Looking for ACCEPTED offers");
        List<ExecutionSessionEntity> found = factory.select(
            IvoaExecutionSessionPhase.ACCEPTED
            );

        if (found.isEmpty())
            {
            log.debug("None found");
            }
        else {
            for (ExecutionSessionEntity entity : found)
                {
                log.debug("Found [" + entity.getUuid() + "][" + entity.getPhase() + "]");
                // This bypasses the phase checking in factory. 
                entity.setPhase(
                    IvoaExecutionSessionPhase.PREPARING
                    );
                entity = factory.save(
                    entity
                    );
                //
                // Schedule a reset back to ACCEPTED.
                other.phaseReset(
                    entity.getUuid()
                    );
                }
            }
        }    
    }
