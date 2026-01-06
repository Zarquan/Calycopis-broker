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

package net.ivoa.calycopis.functional.processing.compute;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;

/**
 *
 */
public interface ComputeResourceProcessingRequestFactory
extends FactoryBase
    {

    public ComponentProcessingRequest createPrepareComputeResourceRequest(final AbstractComputeResourceEntity computeResource);

    public ComponentProcessingRequest createReleaseComputeResourceRequest(final AbstractComputeResourceEntity computeResource);

    public ComponentProcessingRequest createCancelComputeResourceRequest(final AbstractComputeResourceEntity computeResource);

    public ComponentProcessingRequest createFailComputeResourceRequest(final AbstractComputeResourceEntity computeResource);

    }
