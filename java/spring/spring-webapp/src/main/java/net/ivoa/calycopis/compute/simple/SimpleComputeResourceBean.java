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

package net.ivoa.calycopis.compute.simple;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceCoresBlock;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceCoresRequested;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceCoresOffered;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceMemoryBlock;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceMemoryRequested;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceMemoryOffered;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceVolume;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

import net.ivoa.calycopis.util.ListWrapper;
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
    public IvoaComputeResourceCoresBlock getCores()
        {
        return new IvoaComputeResourceCoresBlock()
            {
            @Override
            public IvoaComputeResourceCoresRequested getRequested()
                {
                return new IvoaComputeResourceCoresRequested() {
                    @Override
                    public Long getMin()
                        {
                        return entity.getRequestedCores();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getRequestedCores();
                        }
                    };
                }
            @Override
            public IvoaComputeResourceCoresOffered getOffered()
                {
                return new IvoaComputeResourceCoresOffered()
                    {
                    @Override
                    public Long getValue()
                        {
                        return entity.getOfferedCores();
                        }
                    };
                }
            };
        }
    @Override
    public IvoaComputeResourceMemoryBlock getMemory()
        {
        return new IvoaComputeResourceMemoryBlock()
            {
            @Override
            public IvoaComputeResourceMemoryRequested getRequested()
                {
                return new IvoaComputeResourceMemoryRequested()
                    {
                    @Override
                    public Long getMin()
                        {
                        return entity.getRequestedMemory();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getRequestedMemory();
                        }
                    // TODO restore to units enum ? 
                    @Override
                    public String getUnits()
                        {
                        // return IvoaMinMaxComputeLong.UnitsEnum.GIB;
                        return "GiB";
                        }
                    };
                }
            @Override
            public IvoaComputeResourceMemoryOffered getOffered()
                {
                return new IvoaComputeResourceMemoryOffered()
                    {
                    @Override
                    public Long getValue()
                        {
                        return entity.getRequestedMemory();
                        }
                    // TODO restore to units enum ? 
                    @Override
                    public String getUnits()
                        {
                        //return IvoaMinMaxComputeLong1.UnitsEnum.GIB;
                        return "GiB";
                        }
                    };
                }
            };
        }

    @Override
    public List<@Valid IvoaComputeResourceVolume> getVolumes()
        {
        return Collections.emptyList();
        }
    }

