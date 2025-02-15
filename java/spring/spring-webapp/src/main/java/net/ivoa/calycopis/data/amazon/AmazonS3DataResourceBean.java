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

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A SimpleDataResource response Bean.
 *
 */
public class AmazonS3DataResourceBean
    extends IvoaSimpleDataResource
    {
    /**
     * The OpenAPI type identifier for S3 data resources.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/resources/data/S3-data-resource-1.0" ;

    /**
     * The URL path for S3 data resources.
     *
     */
    private static final String REQUEST_PATH = "/resources/data/" ;

    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The SimpleDataResource entity to wrap.
     *
     */
    private final AmazonS3DataResourceEntity entity;

    /**
     * Protected constructor.
     *
     */
    public AmazonS3DataResourceBean(final String baseurl, final AmazonS3DataResourceEntity entity)
        {
        super();
        this.baseurl = baseurl;
        this.entity= entity;
        }

    /**
     * Generate the href URL based on our baseurl and UUID.
     *
     */
//  @Override
    public String getHref()
        {
        return this.baseurl + REQUEST_PATH + entity.getUuid();
        }

    @Override
    public UUID getUuid()
        {
        return entity.getUuid();
        }

    @Override
    public String getName()
        {
        return entity.getName();
        }

    @Override
    public List<@Valid IvoaMessageItem> getMessages()
        {
        return new ListWrapper<IvoaMessageItem, MessageEntity>(
            entity.getMessages()
            ){
            public IvoaMessageItem wrap(final MessageEntity inner)
                {
                return new MessageItemBean(
                    inner
                    );
                }
            };
        }
    }

