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

package net.ivoa.calycopis.datamodel.resource.data.ivoa;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResourceBlock;

/**
 * An IvoaDataResource entity.
 *
 */
@Entity
@Table(
    name = "ivoadataresources"
    )
public class IvoaDataResourceEntity
    extends AbstractDataResourceEntity
    implements IvoaDataResource
    {

    /**
     * Protected constructor
     *
     */
    protected IvoaDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public IvoaDataResourceEntity(final ExecutionSessionEntity session, final IvoaIvoaDataResource template)
        {
        super(
            session,
            template.getName()
            );

        IvoaIvoaDataResourceBlock ivoa = template.getIvoa();
        if (null != ivoa)
            {
            this.ivoid = ivoa.getIvoid();
            }
        }

    private URI ivoid;
    @Override
    public URI getIvoid()
        {
        return this.ivoid;
        }

    @Override
    public IvoaAbstractDataResource getIvoaBean()
        {
        IvoaIvoaDataResource bean = new IvoaIvoaDataResource(
            IvoaDataResource.TYPE_DISCRIMINATOR
            );
        bean.setUuid(
            this.getUuid()
            );
        bean.setName(
            this.getName()
            );
        bean.setCreated(
            this.getCreated()
            );
        bean.setMessages(
            this.getMessageBeans()
            );

        IvoaIvoaDataResourceBlock block = new IvoaIvoaDataResourceBlock();
        block.setIvoid(
            this.getIvoid()
            );
        bean.setIvoa(
            block
            );
        
        return bean;
        }
    }

