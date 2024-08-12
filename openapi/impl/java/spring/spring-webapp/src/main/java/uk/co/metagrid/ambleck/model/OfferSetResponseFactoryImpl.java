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
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.github.f4b6a3.uuid.UuidCreator;

import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.OfferSetResponseImpl;

@Component
public class OfferSetResponseFactoryImpl
    implements OfferSetResponseFactory
    {
    private UUID uuid ;

    @Override
    public UUID getUuid()
        {
        return this.uuid ;
        }

    public OfferSetResponseFactoryImpl()
        {
        this.uuid = UuidCreator.getTimeBased();
        }

    /**
     * Our Map of OfferSetResponses.
     *
     */
    private Map<UUID, OfferSetResponse> offersets = new HashMap<UUID, OfferSetResponse>();

    /**
     * Create a new OfferSetResponse based on an OfferSetRequest.
     *
     */
    @Override
    public OfferSetResponse create(final OfferSetRequest request)
        {
        OfferSetResponse offerset = new OfferSetResponseImpl();
        offersets.put(
            offerset.getUuid(),
            offerset
            );
        return offerset ;
        }

    /**
     * Get an Iterable of all the OfferSetResponses.
     *
     */
    @Override
    public Iterable<OfferSetResponse> select()
        {
        return offersets.values();
        }

    /**
     * Locate a specific OfferSetResponse based on the identifier.
     *
     */
    @Override
    public OfferSetResponse select(final UUID uuid)
        {
        return offersets.get(
            uuid
            );
        }
    }

