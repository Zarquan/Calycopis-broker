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

package net.ivoa.calycopis.datamodel.resource.storage.simple;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;

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
    public SimpleStorageResourceEntity(final ExecutionSessionEntity session, final IvoaSimpleStorageResource template)
        {
        super(
            session,
            template.getName()
            );
        // TODO Add the fields ...
        }
    }

