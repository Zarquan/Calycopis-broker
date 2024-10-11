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
import java.util.List;
import java.util.UUID;

import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse.ResultEnum;

public interface OfferSetAPI
    {

    public void setAccepted(IvoaExecutionSessionResponse accepted);

    public List<IvoaExecutionSessionResponse> getOffers();

    public UUID getUuid();
    public OffsetDateTime getExpires();
    public String getHref();
    public void setResult(ResultEnum result);

    public void addMessage(final IvoaMessageItem message);
    public void addOffer(final ExecutionResponseImpl response);

    }

