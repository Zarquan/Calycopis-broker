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

package uk.co.metagrid.calycopis.compute.simple;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.openapi.model.IvoaComputeResourceCores;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceMemory;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceVolume;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.util.ListWrapper;
import wtf.metio.storageunits.model.StorageUnits;

/**
 * A SimpleCompute response Bean.
 *
 */
public class SimpleComputeResourceBean
    extends IvoaSimpleComputeResource
    {

    /**
     * The URL path for the executions endpoint.
     *
     */
    private static final String REQUEST_PATH = "/simple-compute/" ;


    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The SimpleComputeResource entity to wrap.
     *
     */
    private final SimpleComputeResourceEntity entity;

    /**
     * Protected constructor.
     *
     */
    public SimpleComputeResourceBean(final String baseurl, final SimpleComputeResourceEntity entity)
        {
        super();
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

    @Override
    public IvoaComputeResourceCores getCores()
        {
        return new IvoaComputeResourceCores()
            {
            @Override
            public Long getRequested()
                {
                return entity.getRequestedCores();
                }
            @Override
            public Long getOffered()
                {
                return entity.getOfferedCores();
                }
            };
        }
        
    @Override
    public IvoaComputeResourceMemory getMemory()
        {
        return new IvoaComputeResourceMemory()
            {
            @Override
            public String getRequested()
                {
                return StorageUnits.binaryValueOf(
                    entity.getRequestedMemory()
                    ).toString();
                }
            @Override
            public String getOffered()
                {
                return StorageUnits.binaryValueOf(
                    entity.getOfferedMemory()
                    ).toString();
                }
            };
        }

    @Override
    public List<@Valid IvoaComputeResourceVolume> getVolumes()
        {
        return Collections.emptyList();
        }
    }

