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
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * 
 */
@Entity
@Table(
    name = "AbstractProcessors"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractProcessorEntity
implements AbstractProcessor
    {

    protected AbstractProcessorEntity()
        {
        super();
        }
    
    protected AbstractProcessorEntity(final URI kind)
        {
        super();
        this.kind = kind;
        }

    private URI kind;
    @Override
    public URI getKind()
        {
        return this.kind;
        }

    @Id
    @GeneratedValue
    protected UUID uuid;
    @Override
    public UUID getUuid()
        {
        return this.uuid;
        }

    private UUID threadId;
    @Override
    public UUID getThreadId()
        {
        return this.threadId;
        }

    private ProcessorPhase phase;
    @Override
    public ProcessorPhase getPhase()
        {
        return this.phase;
        }

    private OffsetDateTime created;
    @Override
    public OffsetDateTime getCreated()
        {
        return this.created;
        }

    private OffsetDateTime modified;
    @Override
    public OffsetDateTime getModified()
        {
        return this.modified;
        }

    private OffsetDateTime activation;
    @Override
    public OffsetDateTime getActivation()
        {
        return this.activation;
        }

    public void activate()
        {
        this.activate(0, false);
        }

    public void activate(boolean reset)
        {
        this.activate(0, reset);
        }

    public void activate(int delay)
        {
        this.activate(delay, false);
        }

    public void activate(int delay, boolean reset)
        {
        this.phase = ProcessorPhase.ACTIVE;
        this.activation = OffsetDateTime.now().plusSeconds(delay);
        if (reset)
            {
            this.reset();
            }
        }

    public void completed()
        {
        this.phase = ProcessorPhase.COMPLETED;
        }

    public void failed()
        {
        this.phase = ProcessorPhase.FAILED;
        }

    @Override
    public abstract void process();

    public abstract void reset();
    
    }
