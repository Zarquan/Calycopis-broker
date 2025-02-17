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

package net.ivoa.calycopis.storage.simple;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.storage.AbstractStorageResourceEntity;

/**
 * A SimpleStorageResource Entity.
 *
 */
@Entity
@Table(
    name = "simplestorageresources"
    )
@DiscriminatorValue(
    value="uri:simple-storage-resource"
    )
public class SimpleStorageResourceEntity
    extends AbstractStorageResourceEntity
    implements SimpleStorageResource
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
    protected SimpleStorageResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public SimpleStorageResourceEntity(final ExecutionSessionEntity parent)
        {
        super();
        this.parent = parent;
        }
    }

