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
import net.ivoa.calycopis.execution.Execution;

/**
 * Public interface for a SimpleComputeResource.
 *
 */
public interface SimpleComputeResource
    extends Component
    {
    /**
     * The database table name for SimpleComputeResources.
     * 
     */
    public static final String TABLE_NAME = "simplecompute" ;

    /**
     * The type discriminator for SimpleComputeResources.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/executables/jupyter-notebook-1.0.yaml" ;


    
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

