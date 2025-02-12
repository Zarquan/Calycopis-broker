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
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeCoresOffered;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeCoresRequested;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeMemoryOffered;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeMemoryRequested;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A SimpleCompute response Bean.
 *
 */
public class SimpleComputeResourceBean
    extends IvoaSimpleComputeResource
    {
    /**
     * The OpenAPI type identifier for simple compute resources.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/resources/compute/simple-compute-resource-1.0.yaml" ;

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
        super(TYPE_DISCRIMINATOR);
        this.baseurl = baseurl;
        this.entity= entity;
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
    public IvoaSimpleComputeCores getCores()
        {
        return new IvoaSimpleComputeCores()
            {
            @Override
            public IvoaSimpleComputeCoresRequested getRequested()
                {
                return new IvoaSimpleComputeCoresRequested() {
                    @Override
                    public Long getMin()
                        {
                        return entity.getMinRequestedCores();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getMaxRequestedCores();
                        }
                    @Override
                    public Boolean getMinimal()
                        {
                        return entity.getMinimalCores();
                        }
                    };
                }
            @Override
            public IvoaSimpleComputeCoresOffered getOffered()
                {
                return new IvoaSimpleComputeCoresOffered()
                    {
                    @Override
                    public Long getMin()
                        {
                        return entity.getMinOfferedCores();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getMaxOfferedCores();
                        }
                    };
                }
            };
        }
    @Override
    public IvoaSimpleComputeMemory getMemory()
        {
        return new IvoaSimpleComputeMemory()
            {
            @Override
            public IvoaSimpleComputeMemoryRequested getRequested()
                {
                return new IvoaSimpleComputeMemoryRequested()
                    {
                    @Override
                    public Long getMin()
                        {
                        return entity.getMinRequestedMemory();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getMaxRequestedMemory();
                        }
                    @Override
                    public Boolean getMinimal()
                        {
                        return entity.getMinimalMemory();
                        }
                    };
                }
            @Override
            public IvoaSimpleComputeMemoryOffered getOffered()
                {
                return new IvoaSimpleComputeMemoryOffered()
                    {
                    @Override
                    public Long getMin()
                        {
                        return entity.getMinOfferedMemory();
                        }
                    @Override
                    public Long getMax()
                        {
                        return entity.getMaxOfferedMemory();
                        }
                    };
                }
            };
        }

    @Override
    public List<@Valid IvoaSimpleComputeVolume> getVolumes()
        {
        return Collections.emptyList();
        }
    }

