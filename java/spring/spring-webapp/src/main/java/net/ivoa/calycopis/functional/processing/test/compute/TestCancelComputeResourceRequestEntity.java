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

package net.ivoa.calycopis.functional.processing.test.compute;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResource;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.CancelComponentRequestEntity;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "canceltestcomputerequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class CancelTestComputeResourceRequestEntity
extends CancelComponentRequestEntity
implements CancelTestComputeResourceRequest
    {

    protected CancelTestComputeResourceRequestEntity()
        {
        super();
        }

    protected CancelTestComputeResourceRequestEntity(final AbstractComputeResourceEntity computeResource)
        {
        super(
            CancelTestComputeResourceRequest.KIND,
            computeResource
            );
        }

    @Override
    public AbstractComputeResource getComputeResource()
        {
        if (this.getComponent() instanceof AbstractComputeResource)
            {
            return (AbstractComputeResource) this.getComponent();
            }
        else {
            log.error(
                "Unexpected component type [{}][{}] for processing request [{}][{}]",
                this.getComponent().getUuid(),
                this.getComponent().getClass().getSimpleName(),
                this.getUuid(),
                this.getClass().getSimpleName()
                );
            throw new IllegalStateException();
            }
        }

    @Override
    protected ProcessingAction makeAction()
        {
        // No further Action required to cancel a TestComputeResource.
        // The preProcess() method should have set the phase the CANCELLED and scheduled a CancelSession request for the parent Session.
        // In which case, can this class be replaced by the CancelComponentRequestEntity base class ? 
        return ProcessingAction.NO_ACTION;
        }
    }
