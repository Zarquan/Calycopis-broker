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

import java.util.UUID;
import java.util.List;
import java.time.OffsetDateTime;
import com.github.f4b6a3.uuid.UuidCreator;

import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponse;

import uk.co.metagrid.ambleck.message.WarnMessage;

import uk.co.metagrid.ambleck.platform.Execution;

public class OfferSetResponseImpl
    extends OfferSetResponse
    implements OfferSetAPI
    {


    private Execution execution;
    protected Execution getExecution()
        {
        return this.execution;
        }
    protected void setExecution(final Execution execution)
        {
        this.execution = execution;
        }

    private ExecutionResponse accepted;
    protected ExecutionResponse getAccepted()
        {
        return this.accepted;
        }
    public void setAccepted(final ExecutionResponse accepted)
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
            OfferSetResponse.ResultEnum.NO
            );
        }

    public void addMessage(final MessageItem message)
        {
        super.addMessagesItem(message);
        }

    public void addOffer(final ExecutionResponseImpl response)
        {
        super.addOffersItem(response);
        }
    }

