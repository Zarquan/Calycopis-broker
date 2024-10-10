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

package uk.co.metagrid.calycopis.execution;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.util.ListWrapper;

/**
 * An Execution response Bean.
 *
 */
public class ExecutionResponseBean
    extends IvoaExecutionResponse
    {

    /**
     * The URL path for the executions endpoint.
     *
     */
    private static final String REQUEST_PATH = "/execution/" ;


    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The Execution entity to wrap.
     *
     */
    private final ExecutionEntity entity;

    /**
     * Protected constructor.
     *
     */
    public ExecutionResponseBean(final String baseurl, final ExecutionEntity entity)
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
    }

