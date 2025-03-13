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

package net.ivoa.calycopis.storage;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.component.ComponentEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;

/**
 * 
 */
@Entity
@Table(
    name = "storageresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class AbstractStorageResourceEntity
extends ComponentEntity
implements AbstractStorageResource
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractStorageResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * 
     */
    protected AbstractStorageResourceEntity(final ExecutionSessionEntity parent, final String name)
        {
        super(name);
        this.parent = parent;
        parent.addStorageResource(
            this
            );
        }

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
    
    @Override
    public IvoaAbstractStorageResource getIvoaBean(String baseurl)
        {
        // TODO Auto-generated method stub
        return null;
        }
    }
