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
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * Public interface for an execution step. 
 * 
 */
public interface PlanningStep
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
     * Get the Component this step is linked to.  
     *
     */
    public ComponentEntity getComponent();
    
    /**
     * Get the previous step.
     *
     */
    public PlanningStep getPrev();

    /**
     * Get the next step.
     *
     */
    public PlanningStep getNext();

    /**
     * The step state/phase.
     *
     */
    enum Phase {
        PREPARING(),
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
     * Get the step duration.
     *
     */
    public Duration getStepDuration();

    /**
     * Get the start offset, relative to the Session execution.
     *
     */
    public Duration getStartOffset();

    /**
     * Get the start time, calculated from the Session execution.
     *
     */
    public Instant getStartInstant();

    /**
     * Set the start time, calculated from the Session execution.
     *
     */
    public void setStartInstant(final Instant instant);
    
    /**
     * Schedule this step.
     *
     */
    void schedule();
    
    /**
     * Activate this step.
     *
     */
    void activate();

    /**
     * Execute this step.
     *
     */
    void execute();
    
    }
