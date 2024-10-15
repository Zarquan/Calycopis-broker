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

package uk.co.metagrid.calycopis.compute.simple;

import uk.co.metagrid.calycopis.component.Component;
import uk.co.metagrid.calycopis.execution.Execution;

/**
 * Public interface for a SimpleComputeResource.
 *
 */
public interface SimpleComputeResource
    extends Component
    {
    /**
     * Reference to the parent Execution. 
     *
     */
    public Execution getParent();

    /**
     * The number of CPU cores requested. 
     *
     */
    public Long getRequestedCores();

    /**
     * The number of CPU cores offered. 
     *
     */
    public Long getOfferedCores();

    /**
     * The amount of memory requested, in bytes. 
     *
     */
    public Long getRequestedMemory();

    /**
     * The amount of memory offered, in bytes. 
     *
     */
    public Long getOfferedMemory();

    // volumes ...

    }

