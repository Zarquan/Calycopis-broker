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

package net.ivoa.calycopis.functional.processing;

import net.ivoa.calycopis.functional.processing.compute.ComputeResourceProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.data.DataResourceProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.executable.ExecutableProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.storage.StorageResourceProcessingRequestFactory;

/**
 * 
 */
public interface RequestProcessingPlatform
    {
    public ProcessingRequestFactory                 getProcessingRequestFactory();
    public SessionProcessingRequestFactory          getSessionProcessingRequestFactory();
    public StorageResourceProcessingRequestFactory  getStorageProcessingRequestFactory();
    public DataResourceProcessingRequestFactory     getDataProcessingRequestFactory();
    public ExecutableProcessingRequestFactory       getExecutableProcessingRequestFactory();
    public ComputeResourceProcessingRequestFactory  getComputeProcessingRequestFactory();

    }
