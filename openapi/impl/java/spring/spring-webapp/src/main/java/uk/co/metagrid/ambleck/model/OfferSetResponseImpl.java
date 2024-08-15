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

public class OfferSetResponseImpl extends OfferSetResponse
    {
    /**
     * Public constructor.
     * Generates a new UUID and sets the expiry time to 5min from now.
     *
     */
    public OfferSetResponseImpl(final String baseurl)
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
            OffsetDateTime.now().plusMinutes(5)
            );
        this.setResult(
            OfferSetResponse.ResultEnum.NO
            );
        }

    /**
     * Add an ExecutionResponse to our list of offers.
     *
    protected void addOffer(final ExecutionResponseImpl execution)
        {
        this.addOffersItem(
             (ExecutionResponse) execution
            );
        this.setResult(
            OfferSetResponse.ResultEnum.YES
            );
        }
     */
    }

