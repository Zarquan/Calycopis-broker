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

/**
 * Public interface for a bi-directional list of steps. 
 * 
 */
public interface PlanningStepSequence
extends PlanningStep, Iterable<PlanningStep>, PlanningStepCollection
    {
    /**
     * Get the first step in the list.
     *
     */
    public PlanningStep getFirst();

    /**
     * Get the last step in the list.
     *
     */
    public PlanningStep getLast();

    /**
     * Get a forwards iterator for the list.
     *
     */
    public Iterator<PlanningStep> forwards();

    /**
     * Get a backwards iterator for the list.
     *
     */
    public Iterator<PlanningStep> backwards();
    
    }
