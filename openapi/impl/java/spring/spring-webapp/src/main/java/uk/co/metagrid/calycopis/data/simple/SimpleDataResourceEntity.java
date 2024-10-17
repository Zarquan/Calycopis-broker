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

package uk.co.metagrid.calycopis.data.simple;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;

/**
 * A SimpleDataResource Entity.
 *
 */
@Entity
@Table(
    name = "simpledata"
    )
@DiscriminatorValue(
    value="urn:simple-data"
    )
public class SimpleDataResourceEntity
    extends ComponentEntity
    implements SimpleDataResource
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
    protected SimpleDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public SimpleDataResourceEntity(final ExecutionEntity parent, final String name, final String location)
        {
        super(name);
        this.parent = parent;
        this.location = location;
        }
    
    private String location;
    @Override
    public String getLocation()
        {
        return this.location;
        }
    
    }
