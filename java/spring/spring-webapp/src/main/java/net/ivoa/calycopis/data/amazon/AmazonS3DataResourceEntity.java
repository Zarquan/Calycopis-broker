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

package net.ivoa.calycopis.data.amazon;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAmazonS3DataResource;

/**
 * An Amazon S3 data resource.
 *
 */
@Entity
@Table(
    name = "s3dataresources"
    )
@DiscriminatorValue(
    value = "uri:S3-data-resource"
    )
public class AmazonS3DataResourceEntity
    extends AbstractDataResourceEntity
    implements AmazonS3DataResource
    {

    /**
     * Protected constructor
     *
     */
    protected AmazonS3DataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent and template.
     *
     */
    public AmazonS3DataResourceEntity(final ExecutionSessionEntity parent, final IvoaAmazonS3DataResource template)
        {
        super(
            parent,
            template.getName()
            );
        this.endpoint = template.getEndpoint();
        this.template = template.getTemplate();
        this.bucket   = template.getBucket();
        this.object   = template.getObject();
        }
    
    private String endpoint;
    @Override
    public String getEndpoint()
        {
        return this.endpoint;
        }

    private String template;
    @Override
    public String getTemplate()
        {
        return this.template;
        }

    private String bucket;
    @Override
    public String getBucket()
        {
        return this.bucket;
        }

    private String object;
    @Override
    public String getObject()
        {
        return this.object;
        }

    @Override
    public IvoaAbstractDataResource getIvoaBean()
        {
        IvoaAmazonS3DataResource bean = new IvoaAmazonS3DataResource(
            AmazonS3DataResource.TYPE_DISCRIMINATOR
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

