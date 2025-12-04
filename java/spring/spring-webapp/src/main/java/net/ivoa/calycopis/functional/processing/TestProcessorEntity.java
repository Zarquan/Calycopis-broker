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

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "TestProcessors"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class TestProcessorEntity
extends AbstractProcessorEntity
implements TestProcessor
    {

    public TestProcessorEntity()
        {
        super();
        }

    public TestProcessorEntity(URI kind)
        {
        super(kind);
        }

    int pollLimit = 20 ;
    
    @Override
    public void process()
        {
        log.debug("Processing task [{}] process()", this.uuid);
        
        if (pollCount < pollLimit)
            {
            this.pollCount++;
            log.debug("Processing task [{}] poll count [{}]", this.uuid, this.pollCount);
            try {
                log.debug("Processing task [{}] sleep", this.uuid);
                Thread.sleep(
                    (long) (Math.random() * 5000)
                    );
                log.debug("Processing task [{}] awake", this.uuid);
                }
            catch (Exception ouch)
                {
                log.debug("Exception processing [{}][{}][{}]", this.uuid, ouch.getClass().getSimpleName(), ouch.getMessage());
                }
            this.activate(
                30
                );
            }
        else
            {
            log.debug("Processing task [{}] reached poll limit [{}][{}]", this.getUuid(), pollCount, pollLimit);
            this.completed();
            }
        }

    int pollCount;
    @Override
    public int getPollCount()
        {
        return this.pollCount;
        }

    int pollTime;
    @Override
    public int getPollTime()
        {
        return this.pollTime;
        }

    @Override
    public void reset()
        {
        this.pollCount = 0;
        this.pollTime = 0;
        }
    }
