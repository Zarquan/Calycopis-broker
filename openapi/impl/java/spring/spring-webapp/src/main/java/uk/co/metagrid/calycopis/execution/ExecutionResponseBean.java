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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaAbstractOption;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponseAllOfSchedule;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetLink;
import net.ivoa.calycopis.openapi.model.IvoaScheduleOfferBlock;
import net.ivoa.calycopis.openapi.model.IvoaScheduleOfferItem;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import uk.co.metagrid.calycopis.executable.AbstractExecutableBeanFactory;
import uk.co.metagrid.calycopis.executable.AbstractExecutableBeanFactoryImpl;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.offerset.OfferSetResponseBean;
import uk.co.metagrid.calycopis.util.ListWrapper;

/**
 * An Execution response Bean.
 *
 */
@Slf4j
public class ExecutionResponseBean
    extends IvoaExecutionSessionResponse
    {
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
        this.entity  = entity;
        }

    @Override
    public UUID getUuid()
        {
        return entity.getUuid();
        }

    @Override
    public String getHref()
        {
        return baseurl + Execution.REQUEST_PATH + entity.getUuid();
        }

    public String getType()
        {
        return Execution.TYPE_DISCRIMINATOR;
        }

    @Override
    public String getName()
        {
        return entity.getName();
        }
    
    @Override
    public OffsetDateTime getCreated()
        {
        return entity.getCreated();
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
    public IvoaOfferSetLink getOfferset()
        {
        return new IvoaOfferSetLink()
            {
            @Override
            public UUID getUuid()
                {
                return entity.getParent().getUuid();
                }
            @Override
            public String getHref()
                {
                return OfferSetResponseBean.makeHref(
                    baseurl,
                    entity.getParent()
                    );
                }
            };
        }

    @Override
    public IvoaExecutionSessionStatus getState()
        {
        return entity.getState() ;
        }

    @Override
    public OffsetDateTime getExpires()
        {
        return entity.getExpires();
        }

    // TODO Inject this in the constructor.
    // https://github.com/ivoa/Calycopis-broker/issues/66
    private AbstractExecutableBeanFactory beanfactory = new AbstractExecutableBeanFactoryImpl();
    
    @Override
    public IvoaAbstractExecutable getExecutable()
        {
        log.debug("getExecutable()");
        log.debug("Executable [{}]", (this.entity.getExecutable() != null) ? this.entity.getExecutable().getUuid() : "null-entity");
        return beanfactory.wrap(
            this.baseurl,
            this.entity.getExecutable()
            );
        }
    
    @Override
    public IvoaExecutionSessionResponseAllOfSchedule getSchedule()
        {
        return new IvoaExecutionSessionResponseAllOfSchedule()
            {
            @Override
            public IvoaScheduleRequestBlock getRequested()
                {
                return null ;
/*
 * In order to display the requested values,
 * we would need to save the row from the original request.
 * Possible, but no real use case for it at the moment.
 * In theory it might be useful for analysis ...
 * but not now.    
 *   
                return new IvoaScheduleRequestBlock()
                    {
                    };
 * 
 */
                };
            
            @Override
            public IvoaScheduleOfferItem getPreparing()
                {
                return new IvoaScheduleOfferItem()
                    {
                    };
                };
            @Override
            public IvoaScheduleOfferItem getExecuting()
                {
                return new IvoaScheduleOfferItem()
                    {
                    public String getStart()
                        {
                        // This gives us start/duration, which is easier to read than start/end.
                        // TODO Create a common formatter class that we use everywhere.
                        // public String format(Instant, Duration)
                        // Only needed if the start time is an Interval.
                        // return entity.getStartInstant().toString() + "/" + entity.getStartDuration().toString();
                        return entity.getStartInstant().toString();
                        }
                    public String getDuration()
                        {
                        return entity.getExeDuration().toString();
                        }
                    };
                };
            @Override
            public IvoaScheduleOfferItem getFinishing()
                {
                return new IvoaScheduleOfferItem()
                    {
                    };
                };
            };
        }

    @Override
    public IvoaExecutionResourceList getResources()
        {
        return null ;
        }

    @Override
    public List<@Valid IvoaAbstractOption> getOptions()
        {
        return Collections.emptyList();
        }
    }
