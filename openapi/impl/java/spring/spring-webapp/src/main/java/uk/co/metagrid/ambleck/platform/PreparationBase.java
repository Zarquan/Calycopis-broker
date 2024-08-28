/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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
package uk.co.metagrid.ambleck.platform;

import java.util.UUID;
import java.time.Duration;

import com.github.f4b6a3.uuid.UuidCreator;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for a PreparationStep.
 *
 */
@Slf4j
public abstract class PreparationBase<ParentType extends Execution>
    implements PreparationStep<ParentType>
    {
    public Duration DEFAULT_STEP_COST = Duration.ofMinutes(5) ;

    public PreparationBase(final ParentType parent)
        {
        this.uuid = UuidCreator.getTimeBased();
        this.parent = parent ;
        this.prepCost = DEFAULT_STEP_COST ;
        }

    private UUID uuid;
    public UUID getUuid()
        {
        return this.uuid;
        }

    private ParentType parent;
    public ParentType getParent()
        {
        return this.parent ;
        }

    private StateEnum state;
    public StateEnum getState()
        {
        return this.state;
        }

    private PreparationStep nextStep;
    public PreparationStep getNextStep()
        {
        return this.nextStep;
        }

    public void setNextStep(final PreparationStep nextStep)
        {
        this.nextStep = nextStep;
        }

    private Duration prepCost;
    public Duration getPrepCost()
        {
        return this.prepCost;
        }
    public void setPrepCost(final Duration cost)
        {
        this.prepCost = cost ;
        }

    /**
     * Start the step execution.
     *
     */
    public PreparationStep.StateEnum start()
        {
        switch (this.state)
            {
            case StateEnum.CREATED:
                this.state = StateEnum.RUNNING ;
                // Launch the run() method in a new Thread.
                break ;

            default:
            // Error message ..
            }
        return this.state;
        }

    /**
     * Complete the step execution.
     *
     */
    public StateEnum complete()
        {
        // Set the status to COMPLETED.
        return this.state;
        }

    /**
     * Cancel the step execution.
     *
     */
    public StateEnum cancel()
        {
        // Set the status to CANCELLED.
        return this.state;
        }

    /**
     * Fail the step execution.
     *
     */
    public StateEnum fail()
        {
        // Set the status to FAILED.
        return this.state;
        }
    }

