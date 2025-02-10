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
import net.ivoa.calycopis.execution.ExecutionSessionEntity;

/**
 * A Simple compute resource.
 *
 */
@Entity
@Table(
    name = "simplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:simple-compute-resources"
    )
public class SimpleComputeResourceEntity
    extends ComponentEntity
    implements SimpleComputeResource
    {

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionSessionEntity parent;

    @Override
    public ExecutionSessionEntity getParent()
        {
        return this.parent;
        }

    public void setParent(final ExecutionSessionEntity parent)
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
    public SimpleComputeResourceEntity(
        final ExecutionSessionEntity parent,
        final String name,
        final Long minrequestedcores,
        final Long maxrequestedcores,
        final Long minofferedcores,
        final Long maxofferedcores,
        final Long minrequestedmemory,
        final Long maxrequestedmemory,
        final Long minofferedmemory,
        final Long maxofferedmemory,
        final Boolean minimalcores,
        final Boolean minimalmemory
        ){
        super(name);
        this.parent = parent;
        this.minrequestedcores = minrequestedcores;
        this.maxrequestedcores = maxrequestedcores;
        this.minofferedcores   = minofferedcores;
        this.maxofferedcores   = maxofferedcores;
        this.minrequestedmemory = minrequestedmemory;
        this.maxrequestedmemory = maxrequestedmemory;
        this.minofferedmemory   = minofferedmemory;
        this.maxofferedmemory   = maxofferedmemory;
        this.minimalcores  = minimalcores;
        this.minimalmemory = minimalmemory;
        }

    // Does this also have a start and end time ?
    // Does this also go through a similar set of state changes as the parent execution ?
    
    @Column(name="minrequestedcores")
    private Long minrequestedcores;
    @Override
    public Long getMinRequestedCores()
        {
        return this.minrequestedcores;
        }

    @Column(name="maxrequestedcores")
    private Long maxrequestedcores;
    @Override
    public Long getMaxRequestedCores()
        {
        return this.maxrequestedcores;
        }
    
    @Column(name="minofferedcores")
    private Long minofferedcores;
    @Override
    public Long getMinOfferedCores()
        {
        return this.minofferedcores;
        }

    @Column(name="maxofferedcores")
    private Long maxofferedcores;
    @Override
    public Long getMaxOfferedCores()
        {
        return this.maxofferedcores;
        }
    
    @Column(name="minrequestedmemory")
    private Long minrequestedmemory;
    @Override
    public Long getMinRequestedMemory()
        {
        return this.minrequestedmemory;
        }

    @Column(name="maxrequestedmemory")
    private Long maxrequestedmemory;
    @Override
    public Long getMaxRequestedMemory()
        {
        return this.maxrequestedmemory;
        }
    
    @Column(name="minofferedmemory")
    private Long minofferedmemory;
    @Override
    public Long getMinOfferedMemory()
        {
        return this.minofferedmemory;
        }

    @Column(name="maxofferedmemory")
    private Long maxofferedmemory;
    @Override
    public Long getMaxOfferedMemory()
        {
        return this.maxofferedmemory;
        }

    @Column(name="minimalcores")
    private Boolean minimalcores;
    @Override
    public Boolean getMinimalCores()
        {
        return this.minimalcores;
        }

    @Column(name="minimalmemory")
    private Boolean minimalmemory;
    @Override
    public Boolean getMinimalMemory()
        {
        return this.minimalmemory;
        }
    }

