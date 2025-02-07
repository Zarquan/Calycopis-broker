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

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.component.ComponentEntity;
import net.ivoa.calycopis.execution.ExecutionEntity;

/**
 * A SimpleComputeResource Entity.
 *
 */
@Entity
@Table(
    name = SimpleComputeResource.TABLE_NAME
    )
@DiscriminatorValue(
    value = SimpleComputeResource.TYPE_DISCRIMINATOR
    )
public class SimpleComputeResourceEntity
    extends ComponentEntity
    implements SimpleComputeResource
    {

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionEntity parent;

    @Override
    public ExecutionEntity getParent()
        {
        return this.parent;
        }

    public void setParent(final ExecutionEntity parent)
        {
        this.parent = parent;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public SimpleComputeResourceEntity(final ExecutionEntity parent, final String name, Long requestedcores, Long offeredcores, Long requestedmemory, Long offeredmemory)
        {
        super(name);
        this.parent = parent;
        this.requestedcores = requestedcores;
        this.offeredcores   = offeredcores;
        this.requestedmemory = requestedmemory;
        this.offeredmemory   = offeredmemory;
        }

    // Does this also have a start and end time ?
    // Does this also go through a similar set of state changes as the parent execution ?
    
    @Column(name="requestedcores")
    private Long requestedcores;
    @Override
    public Long getRequestedCores()
        {
        return this.requestedcores;
        }

    @Column(name="offeredcores")
    private Long offeredcores;
    @Override
    public Long getOfferedCores()
        {
        return this.offeredcores;
        }

    @Column(name="requestedmemory")
    private Long requestedmemory;
    @Override
    public Long getRequestedMemory()
        {
        return this.requestedmemory;
        }

    @Column(name="offeredmemory")
    private Long offeredmemory;
    @Override
    public Long getOfferedMemory()
        {
        return this.offeredmemory;
        }
    }

