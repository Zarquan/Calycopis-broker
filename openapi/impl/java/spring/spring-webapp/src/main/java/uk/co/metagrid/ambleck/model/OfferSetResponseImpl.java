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
package uk.co.metagrid.ambleck.model;

import java.time.OffsetDateTime;

import com.github.f4b6a3.uuid.UuidCreator;

import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import uk.co.metagrid.ambleck.platform.Execution;

public class OfferSetResponseImpl
    extends IvoaOfferSetResponse
    implements OfferSetAPI
    {


    private Execution<?> execution;
    protected Execution<?> getExecution()
        {
        return this.execution;
        }
    protected void setExecution(final Execution<?> execution)
        {
        this.execution = execution;
        }

    private IvoaExecutionSessionResponse accepted;
    protected IvoaExecutionSessionResponse getAccepted()
        {
        return this.accepted;
        }
    public void setAccepted(final IvoaExecutionSessionResponse accepted)
        {
        this.accepted = accepted ;
        }

    /**
     * Public constructor, automatically generates a new UUID.
     *
     */
    public OfferSetResponseImpl(final OffsetDateTime expires, final String baseurl)
        {
        this.setUuid(
            UuidCreator.getTimeBased()
            );
        this.setHref(
            baseurl + "/offerset/" + this.getUuid()
            );
        this.created(
            OffsetDateTime.now()
            );
        this.expires(
            expires
            );
        this.setResult(
            IvoaOfferSetResponse.ResultEnum.NO
            );
        }

    public void addMessage(final IvoaMessageItem message)
        {
        super.addMessagesItem(message);
        }

    public void addOffer(final ExecutionResponseImpl response)
        {
        super.addOffersItem(response);
        }
    }

