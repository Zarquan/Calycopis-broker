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

package net.ivoa.calycopis.compute.simple;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.compute.AbstractComputeResource;
import net.ivoa.calycopis.execution.ExecutionSession;

/**
 * Public interface for a SimpleComputeResource.
 *
 */
public interface SimpleComputeResource
    extends Component, AbstractComputeResource
    {

    /**
     * The minimum number of CPU cores requested.
     *
     */
    public Long getMinRequestedCores();

    /**
     * The maximum number of CPU cores requested. 
     *
     */
    public Long getMaxRequestedCores();

    /**
     * The minimum number of CPU cores offered. 
     *
     */
    public Long getMinOfferedCores();

    /**
     * The maximum number of CPU cores offered. 
     *
     */
    public Long getMaxOfferedCores();

    /**
     * The minimum amount of memory requested, in bytes, 
     *
     */
    public Long getMinRequestedMemory();

    /**
     * The maximum amount of memory requested, in bytes, 
     *
     */
    public Long getMaxRequestedMemory();

    /**
     * The minimum amount of memory offered, in bytes. 
     *
     */
    public Long getMinOfferedMemory();

    /**
     * The maximum amount of memory offered, in bytes. 
     *
     */
    public Long getMaxOfferedMemory();

    /**
     * The minimal flag for the CPU cores request. 
     *
     */
    public Boolean getMinimalCores();
    
    /**
     * The minimal flag for the memory request. 
     *
     */
    public Boolean getMinimalMemory();
    
    // volumes ...

    }

