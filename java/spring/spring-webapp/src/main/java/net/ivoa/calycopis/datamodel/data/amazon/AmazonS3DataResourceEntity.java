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

package net.ivoa.calycopis.datamodel.data.amazon;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaS3DataResource;

/**
 * An Amazon S3 data resource.
 *
 */
@Entity
@Table(
    name = "s3dataresources"
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
     * Protected constructor with parent, storage, and validation Result.
     *
     */
    public AmazonS3DataResourceEntity(
        final ExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        this(
            session,
            storage,
            result,
            (IvoaS3DataResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with parent and template.
     *
     */
    public AmazonS3DataResourceEntity(
        final ExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result,
        final IvoaS3DataResource validated
        ){
        super(
            session,
            storage,
            result,
            validated.getName()
            );
        this.endpoint = validated.getEndpoint();
        this.template = validated.getTemplate();
        this.bucket   = validated.getBucket();
        this.object   = validated.getObject();

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
        return fillBean(
            new IvoaS3DataResource (
                AmazonS3DataResource.TYPE_DISCRIMINATOR
                )
            );
        }

    protected IvoaS3DataResource fillBean(final IvoaS3DataResource bean)
        {
        super.fillBean(
            bean
            );

        // TODO fill in the Amazon fields

        return bean;
        }
    }

