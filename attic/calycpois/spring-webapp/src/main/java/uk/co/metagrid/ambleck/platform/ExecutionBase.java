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
import java.util.List;
import java.util.ArrayList;

import java.time.Duration;

import uk.co.metagrid.ambleck.model.OfferSetAPI;
import uk.co.metagrid.ambleck.model.ExecutionResponseAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ExecutionBase<StepType extends PreparationStep<?>>
    implements Execution<StepType>
    {

    public ExecutionBase(final OfferSetAPI offerset, final ExecutionResponseAPI response)
        {
        this.offerset = offerset ;
        this.response = response ;
        }

    private OfferSetAPI offerset;
    public OfferSetAPI getOfferSet()
        {
        return this.offerset;
        }

    private ExecutionResponseAPI response;
    public  ExecutionResponseAPI getResponse()
        {
        return this.response;
        }
    public void setResponse(final ExecutionResponseAPI response)
        {
        this.response = response;
        }

    private UUID uuid;
    public UUID getUuid()
        {
        return this.uuid;
        }

    private List<StepType> stepList = new ArrayList<StepType>();
    public List<StepType> getPreparationSteps()
        {
        return this.stepList;
        }

    public void addPreparationStep(final StepType nextStep)
        {
        log.debug("addPreparationStep [{}][{}]", nextStep.getClass().getName(), nextStep.getUuid());
        if (nextStep != null)
            {
            if (this.firstStep == null)
                {
                this.firstStep = nextStep ;
                }
            if (this.lastStep != null)
                {
                this.lastStep.setNextStep(
                    nextStep
                    );
                }
            this.lastStep = nextStep;
            this.stepList.add(
                nextStep
                );
            }
        }

    // The last item to be added.
    private StepType lastStep;
    public StepType getLastStep()
        {
        return this.lastStep;
        }

    // The first item to be added.
    private StepType firstStep;
    public StepType getFirstStep()
        {
        return this.firstStep;
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
    }

