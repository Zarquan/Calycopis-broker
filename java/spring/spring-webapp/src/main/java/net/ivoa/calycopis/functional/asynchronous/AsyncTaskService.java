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

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Service
public class AsyncTaskService
    {

    private final SimpleExecutionSessionEntityFactory factory ;

    /**
     * 
     */
    @Autowired
    public AsyncTaskService(final SimpleExecutionSessionEntityFactory factory)
        {
        super();
        this.factory = factory ;
        }

    @Async("TaskExecutor-21")
    void phaseReset(final UUID uuid)
        {
        log.debug("Reset requested for [" + uuid + "]");
        try {
            log.debug("Sleeping ....");
            Thread.sleep(Duration.ofSeconds(120));
            log.debug("... done");
            }
        catch (Exception ouch)
            {
            log.debug("Thread interupted [" + ouch.getClass().getSimpleName() + "][" + ouch.getMessage() + "]");
            }
        log.debug("Resetting phase for [" + uuid + "]");
        Optional<SimpleExecutionSessionEntity> found = factory.select(uuid);
        if (found.isPresent())
            {
            SimpleExecutionSessionEntity entity = found.get(); 
            entity.setPhase(
                IvoaSimpleExecutionSessionPhase.OFFERED
                );
            entity = factory.save(
                entity
                );
            }
        else {
            log.debug("Unable to find entity [" + uuid + "]");
            }
        }
    }
