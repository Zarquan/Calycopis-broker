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

package net.ivoa.calycopis.datamodel.offerset;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.message.MessageEntity;
import net.ivoa.calycopis.datamodel.message.MessageItemBean;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaComponentMetadata;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A serialization bean for OfferSets.
 * 
 */
@Slf4j
public class OfferSetResponseBean
    extends IvoaOfferSetResponse {

    /**
     * The URL path for OfferSets.
     * 
     */
    public static final String REQUEST_PATH = "/offersets/" ;

    /**
     * The base URL for the current request.
     * 
     */
    private final String baseurl;

    /**
     * The OfferSet entity to wrap.
     * 
     */
    private final OfferSetEntity entity;

	/**
	 * Protected constructor.
	 * 
	 */
	public OfferSetResponseBean(final String baseurl, final OfferSetEntity entity)
	    {
	    super();
        this.baseurl = baseurl;
        this.entity = entity;
	    }
	
	public IvoaComponentMetadata getMeta()
	    {
	    return new IvoaComponentMetadata() {

        @Override
        public String getKind()
            {
            return OfferSet.TYPE_DISCRIMINATOR;
            }
	    
        @Override
        public UUID getUuid()
            {
            return entity.getUuid();
            }

        @Override
        public String getUrl()
            {
            return makeUrl(baseurl, entity);
            }

        @Override
        public String getName()
            {
            return entity.getName();
            }

        @Override
        public String getDescription()
            {
            return entity.getDescription();
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
        };
    }

    static String makeUrl(final String baseurl, final OfferSetEntity entity)
        {
        return baseurl + REQUEST_PATH + entity.getUuid();
        }

    @Override
    public ResultEnum getResult()
        {
        return entity.getResult();
        }

    @Override
    public List<@Valid IvoaExecutionSessionResponse> getOffers()
        {
        return new ListWrapper<IvoaExecutionSessionResponse, SessionEntity>(
            entity.getOffers()
            ){
            public IvoaExecutionSessionResponse wrap(final SessionEntity inner)
                {
                return inner.getIvoaBean(
                    baseurl
                    );
                }
            };
        }
    }
