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

import java.util.Iterator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;

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

    public PlanningStepSequenceEntity()
        {
        super();
        }
    
    public PlanningStepSequenceEntity(final SessionEntity session, final ComponentEntity component)
        {
        super(
            session,
            component
            );
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

    public void addStep(final AbstractPlanningStepEntity step)
        {
        if (this.last != null)
            {
            step.setNext(this.last);
            this.last.setPrev(step);
            }
        this.last = step ;
        }
    
    @Override
    public Iterable<PlanningStep> forwards()
        {
        return new Iterable<PlanningStep>()
            {
            @Override
            public Iterator<PlanningStep> iterator()
                {
                return new Iterator<PlanningStep>()
                    {
                    AbstractPlanningStepEntity step = first;
                    @Override
                    public boolean hasNext()
                        {
                        return (step != null);
                        }
                    @Override
                    public PlanningStep next()
                        {
                        AbstractPlanningStepEntity temp = step ;
                        if (step != null)
                            {
                            step = step.getNext();
                            }
                        return temp;
                        }
                    } ;
                }
            };
        }

    @Override
    public Iterable<PlanningStep> backwards()
        {
        return new Iterable<PlanningStep>()
            {
            @Override
            public Iterator<PlanningStep> iterator()
                {
                return new Iterator<PlanningStep>()
                    {
                    AbstractPlanningStepEntity step = last;
                    @Override
                    public boolean hasNext()
                        {
                        return (step != null);
                        }
                    @Override
                    public PlanningStep next()
                        {
                        AbstractPlanningStepEntity temp = step ;
                        if (step != null)
                            {
                            step = step.getPrev();
                            }
                        return temp;
                        }
                    } ;
                }
            };
        }

    @Override
    public void schedule()
        {
        }

    @Override
    public void activate()
        {
        }

    @Override
    public void execute()
        {
        }
    }
