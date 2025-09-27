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

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "teststeps"
    )
@DiscriminatorValue(
    value="uri:test-execution-step"
    )
public class TestExecutionStepEntity
extends AbstractPlanningStepEntity
implements PlanningStep
    {

    /**
     * Public constructor.
     * 
     */
    public TestExecutionStepEntity(
        final ExecutionSessionEntity session,
        final ComponentEntity component,
        final Duration offset,
        final Duration duration,
        final String message
        ){
        super(
            session,
            component,
            offset,
            duration
            );
        this.message = message;
        }

    /**
     * Protected constructor.
     * 
     */
    protected TestExecutionStepEntity()
        {
        super();
        }

    @Column(name = "message")
    private String message ;
    
    @Override
    public void execute()
        {
        this.start();
        try {
            log.debug("sleeping ...");
            Thread.sleep(
                this.getStepDuration()
                );
            log.debug(".... awake");
            }
        catch (Exception ouch)
            {
            log.debug("Exception [{}]", ouch);
            setPhase(Phase.FAILED);
            }
        this.done();
        }

    public void start()
        {
        log.debug("Start [{}][{}][{}]", this.getStartOffset(), this.getStepDuration(), this.message);
        this.setPhase(
            Phase.EXECUTING
            );
        }

    public void done()
        {
        log.debug("Done  [{}][{}][{}]", this.getStartOffset(), this.getStepDuration(), this.message);
        if (this.getPhase() == Phase.EXECUTING)
            {
            this.setPhase(
                Phase.COMPLETED
                );
            }
        }
    }

