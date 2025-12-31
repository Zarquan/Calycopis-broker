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

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "cancelcomputerequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class CancelComputeResourceRequestEntity
extends ComputeResourceProcessingRequestEntity
implements CancelComputeResourceRequest
    {

    protected CancelComputeResourceRequestEntity()
        {
        super();
        }

    protected CancelComputeResourceRequestEntity(final AbstractComputeResourceEntity compute)
        {
        super(
            PrepareComputeResourceRequest.KIND,
            compute
            );
        }

    @Override
    public ProcessingAction preProcess(final RequestProcessingPlatform platform)
        {
        log.debug("Pre-processing request [{}][{}] for [{}][{}]", this.getUuid(), this.getClass().getSimpleName(), this.getComputeResource().getUuid(), getComputeResource().getClass().getSimpleName());
        //
        // Mark our resource as CANCELLED.
        this.getComputeResource().setPhase(IvoaLifecyclePhase.CANCELLED);
        //
        // No additional action required.
        return null;
        }

    @Override
    public void postProcess(final RequestProcessingPlatform platform)
        {
        log.debug("Post-processing request [{}][{}] for [{}][{}]", this.getUuid(), this.getClass().getSimpleName(), this.getComputeResource().getUuid(), getComputeResource().getClass().getSimpleName());
        //
        // Close this request.
        super.postProcess(platform);
        }
    }
