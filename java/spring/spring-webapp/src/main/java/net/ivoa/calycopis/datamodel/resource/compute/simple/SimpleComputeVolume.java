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
package net.ivoa.calycopis.datamodel.resource.compute.simple;

import net.ivoa.calycopis.datamodel.component.Component;
import net.ivoa.calycopis.datamodel.resource.data.simple.SimpleDataResource;
import net.ivoa.calycopis.datamodel.resource.storage.simple.SimpleStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume.ModeEnum;

/**
 * Public interface for a SimpleComputeResource volume mount.
 * 
 */
public interface SimpleComputeVolume
    extends Component
    {
    /**
     * Reference to the parent SimpleComputeResource. 
     *
     */
    public SimpleComputeResource getParent();

    /**
     * Reference to the associated SimpleDataResource.
     * TODO make this abstract, allowing different types of DataResource.
     *
     */
    public SimpleDataResource getDataResource();

    /**
     * Reference to the associated SimpleStorageResource.
     * TODO make this abstract, allowing different types of StorageResource.
     *
     */
    public SimpleStorageResource getStorageResource();

    /**
     * Get the access mode
     * 
     */
    public ModeEnum getMode();

    /**
     * Get the mount path.
     * 
     */
    public String getPath();
    
    
    }
