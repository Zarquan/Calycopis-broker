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

package net.ivoa.calycopis.data.simple;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;

/**
 * A Simple data resource.
 *
 */
@Entity
@Table(
    name = "simpledataresources"
    )
@DiscriminatorValue(
    value="uri:simple-data-resource"
    )
public class SimpleDataResourceEntity
    extends AbstractDataResourceEntity
    implements SimpleDataResource
    {

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
    public SimpleDataResourceEntity(final ExecutionSessionEntity parent, final IvoaSimpleDataResource template)
        {
        super(
            parent,
            template.getName()
            );
        this.location = template.getLocation();
        }
    
    private String location;
    @Override
    public String getLocation()
        {
        return this.location;
        }

    @Override
    public IvoaAbstractDataResource getIvoaBean()
        {
        IvoaSimpleDataResource bean = new IvoaSimpleDataResource(
            SimpleDataResource.TYPE_DISCRIMINATOR
            );
        bean.setUuid(
            this.getUuid()
            );
        bean.setMessages(
            this.getMessageBeans()
            );

        // TODO fill in the fields 
                
        return bean;
        }
    }

