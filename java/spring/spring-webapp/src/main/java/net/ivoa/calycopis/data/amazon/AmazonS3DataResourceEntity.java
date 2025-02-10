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
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.component.ComponentEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;

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
    extends ComponentEntity
    implements AmazonS3DataResource
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
    protected AmazonS3DataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public AmazonS3DataResourceEntity(final ExecutionSessionEntity parent, final String name, final String endpoint, final String template, final String bucket, final String object)
        {
        super(name);
        this.parent   = parent;
        this.endpoint = endpoint;
        this.template = template;
        this.bucket   = bucket;
        this.object   = object;
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
    }

