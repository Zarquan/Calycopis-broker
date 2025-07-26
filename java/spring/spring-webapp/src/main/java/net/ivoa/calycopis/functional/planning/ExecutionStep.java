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

package net.ivoa.calycopis.functional.planning;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.component.Component;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * Public interface for an execution step. 
 * 
 */
public interface ExecutionStep
    {
    /**
     * Get the UUID identifier.
     * 
     */
    public UUID getUuid();
    
    /**
     * Get the ExecutionSession this step is linked to.  
     *
     */
    public ExecutionSessionEntity getSession();
    
    /**
     * Get the Executable, DataResource, or ComputeResource this step is linked to.
     * TODO Should these all have a common base class/interface ? 
     *
     */
    public Component getComponent();

    /**
     * Get the previous step.
     *
     */
    public ExecutionStep getPrev();

    /**
     * Set the previous step.
     *
    public void setPrev(final ExecutionStep step);
     */

    /**
     * Get the next step.
     *
     */
    public ExecutionStep getNext();

    /**
     * Set the next step.
     *
    public void setNext(final ExecutionStep step);
     */
    
    /**
     * The step state/phase.
     *
     */
    enum Phase {
        WAITING(),
        EXECUTING(),
        COMPLETED(),
        CANCELLED(),
        FAILED();
        };

    /**
     * Get the step phase.
     *
     */
    public Phase getPhase();

    /**
     * Get the start time, calculated from the Session execution plus the offset.
     *
     */
    public Instant  getStart();
    
    /**
     * Get the start offset, relative to the Session execution.
     *
     */
    public Duration getOffset();

    /**
     * Get the step duration.
     *
     */
    public Duration getDuration();

    /**
     * Execute this step.
     *
     */
    void execute();


    }
