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

package uk.co.metagrid.calycopis.execution;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceEntity;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;

/**
 * An Execution Entity.
 *
 */
@Entity
@Table(
    name = "executions"
    )
@DiscriminatorValue(
    value="urn:execution"
    )
public class ExecutionEntity
    extends ComponentEntity
    implements Execution
    {

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OfferSetEntity parent;

    @Override
    public OfferSetEntity getParent()
        {
        return this.parent;
        }

    public void setParent(final OfferSetEntity parent)
        {
        this.parent = parent;
        }

    /**
     * Protected constructor
     *
     */
    protected ExecutionEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public ExecutionEntity(final OfferSetEntity parent)
        {
        super();
        this.parent = parent;
        }


    @Column(name = "expires")
    private OffsetDateTime expires;

    @Override
    public OffsetDateTime getExpires()
        {
        return this.expires;
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<SimpleComputeResourceEntity> computeresources = new ArrayList<SimpleComputeResourceEntity>();

    @Override
    public List<SimpleComputeResourceEntity> getComputeResources()
        {
        return computeresources;
        }
     
    public void addCompute(final SimpleComputeResourceEntity compute)
        {
        computeresources.add(compute);
        compute.setParent(this);
        }
    
    
    }

