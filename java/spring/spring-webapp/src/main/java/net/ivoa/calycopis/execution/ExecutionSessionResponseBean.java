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

package net.ivoa.calycopis.execution;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceBean;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.executable.AbstractExecutableBeanFactory;
import net.ivoa.calycopis.executable.AbstractExecutableBeanFactoryImpl;
import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.offerset.OfferSetResponseBean;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaAbstractOption;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueOption;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponseAllOfSchedule;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaScheduleOfferItem;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * An Execution response Bean.
 *
 */
@Slf4j
public class ExecutionSessionResponseBean
    extends IvoaExecutionSessionResponse
    {
    /**
     * The type identifier for an execution session response.
     *
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/sessions/execution-session-response-1.0.yaml" ;

    /**
     * The URL path for an execution session.
     *
     */
    public static final String REQUEST_PATH = "/sessions/" ;

    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The Execution entity to wrap.
     *
     */
    private final ExecutionSessionEntity entity;

    /**
     * Protected constructor.
     *
     */
    public ExecutionSessionResponseBean(final String baseurl, final ExecutionSessionEntity entity)
        {
        super();
        super.type(TYPE_DISCRIMINATOR);
        this.baseurl = baseurl;
        this.entity  = entity;
        this.setOptions();
        }

    @Override
    public UUID getUuid()
        {
        return entity.getUuid();
        }

    @Override
    public String getHref()
        {
        return baseurl + REQUEST_PATH + entity.getUuid();
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
                    Duration delay = Duration.ofMinutes(10) ;
                    public String getStart()
                        {
                        return entity.getStartInstant().minus(delay).toString();
                        }
                    public String getDuration()
                        {
                        return delay.toString();
                        }
                    };
                };
            @Override
            public IvoaScheduleOfferItem getExecuting()
                {
                return new IvoaScheduleOfferItem()
                    {
                    public String getStart()
                        {
                        return entity.getStartInstant().toString();
                        }
                    public String getDuration()
                        {
                        return entity.getExeDuration().toString();
                        }
                    };
                };
            @Override
            public IvoaScheduleOfferItem getReleasing()
                {
                return new IvoaScheduleOfferItem()
                    {
                    Duration delay = Duration.ofMinutes(5) ;
                    public String getStart()
                        {
                        return entity.getStartInstant().plus(entity.getExeDuration()).toString();
                        }
                    public String getDuration()
                        {
                        return delay.toString();
                        }
                    };
                };
            };
        }

    @Override
    public IvoaExecutionResourceList getResources()
        {
        return new IvoaExecutionResourceList()
            {
            @Override
            public List<@Valid IvoaAbstractComputeResource> getCompute()
                {
                return new ListWrapper<IvoaAbstractComputeResource, SimpleComputeResourceEntity>(
                    entity.getComputeResources()
                    ){
                    public IvoaAbstractComputeResource wrap(SimpleComputeResourceEntity entity)
                        {
                        return new SimpleComputeResourceBean(
                            baseurl,
                            entity
                            );
                        }
                    };
                }
            @Override
            public List<@Valid IvoaAbstractStorageResource> getStorage()
                {
                return null;
                }
            @Override
            public List<@Valid IvoaAbstractDataResource> getData()
                {
                return null;
                }
            };
        }

    public void setOptions()
        {
        switch(entity.getState())
            {
            case OFFERED:
                {
                this.addOptionsItem(
                    new IvoaEnumValueOption(
                        List.of("ACCEPTED", "REJECTED"),
                        "urn:enum-value-option",
                        "state"
                        )
                    );
                }
                break;
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case READY:
            case RUNNING:
                this.addOptionsItem(
                    new IvoaEnumValueOption(
                        List.of("CANCELLED"),
                        "urn:enum-value-option",
                        "state"
                        )
                    );
                break;

            default:
                break;
            }
        }

    @Override
    @JsonProperty("options")
    @JacksonXmlProperty(localName = "options")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<IvoaAbstractOption> getOptions()
        {
        return super.getOptions();
        }
    }
