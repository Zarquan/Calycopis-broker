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

package net.ivoa.calycopis.functional.processing.storage;

import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.functional.processing.component.CancelComponentRequest;
import net.ivoa.calycopis.functional.processing.component.FailComponentRequest;
import net.ivoa.calycopis.functional.processing.component.PrepareComponentRequest;
import net.ivoa.calycopis.functional.processing.component.ReleaseComponentRequest;

/**
 *
 */
public interface StorageResourceProcessingRequestFactory
extends FactoryBase
    {

    public PrepareComponentRequest createPrepareStorageResourceRequest(final AbstractStorageResourceEntity storageResource);

    public ReleaseComponentRequest createReleaseStorageResourceRequest(final AbstractStorageResourceEntity storageResource);

    public CancelComponentRequest  createCancelStorageResourceRequest(final AbstractStorageResourceEntity storageResource);

    public FailComponentRequest  createFailStorageResourceRequest(final AbstractStorageResourceEntity storageResource);

    }
