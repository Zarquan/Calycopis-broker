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

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A SimpleStorage response Bean.
 *
 */
public class SimpleStorageResourceBean
    extends IvoaSimpleStorageResource
    {
    /**
     * The OpenAPI type identifier for simple storage resources.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/resources/storage/simple-storage-resource-1.0.yaml" ;

    /**
     * The URL path for the executions endpoint.
     *
     */
    private static final String REQUEST_PATH = "/resources/storage/" ;


    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The SimpleStorageResource entity to wrap.
     *
     */
    private final SimpleStorageResourceEntity entity;

    /**
     * Protected constructor.
     *
     */
    public SimpleStorageResourceBean(final String baseurl, final SimpleStorageResourceEntity entity)
        {
        super(TYPE_DISCRIMINATOR);
        this.baseurl = baseurl;
        this.entity= entity;
        }

    /**
     * Generate the href URL based on our baseurl and UUID.
     *
     */
    @Override
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

