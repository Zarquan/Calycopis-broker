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
import java.util.Iterator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * 
 */
@Slf4j
@Entity
@Table(name = "planningsequence")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PlanningStepSequenceEntity
extends AbstractPlanningStepEntity
implements PlanningStep, PlanningStepSequence
    {

    public PlanningStepSequenceEntity(final ExecutionSessionEntity session, final  AbstractPlanningStepEntity prev, final AbstractPlanningStepEntity template)
        {
        super(
            session,
            prev,
            template
            );
        }

    public PlanningStepSequenceEntity(final ExecutionSessionEntity session, final  AbstractPlanningStepEntity prev)
        {
        super(
            session,
            prev
            );
        }

    public PlanningStepSequenceEntity(ExecutionSessionEntity session)
        {
        super(session);
        }

    @JoinColumn(name = "first", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity first;
    @Override
    public PlanningStep getFirst()
        {
        return this.first;
        }

    @JoinColumn(name = "last", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity last;
    @Override
    public PlanningStep getLast()
        {
        return this.last;
        }

    @Override
    public void addStep(final PlanningStep step)
        {
        if (step instanceof AbstractPlanningStepEntity)
            {
            this.addStep((AbstractPlanningStepEntity) step);
            }
        else {
            log.warn("Unexpected step class [" + step.getClass().getName() + "]");
            }
        }
    
    protected void addStep(final AbstractPlanningStepEntity step)
        {
        if (this.last != null)
            {
            step.setNext(this.last);
            this.last.setPrev(step);
            }
        this.last = step ;
        }
    
    @Override
    public Iterator<PlanningStep> iterator()
        {
        return null;
        }
    
    @Override
    public Iterator<PlanningStep> forwards()
        {
        return null;
        }

    @Override
    public Iterator<PlanningStep> backwards()
        {
        return null;
        }

    @Override
    public void execute()
        {
        }

    }
