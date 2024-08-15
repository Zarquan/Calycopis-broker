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
import org.springframework.beans.factory.annotation.Autowired;

import com.github.f4b6a3.uuid.UuidCreator;

import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.OfferSetResponseImpl;
import uk.co.metagrid.ambleck.model.OfferSetResponseFactory;

import uk.co.metagrid.ambleck.model.ExecutionResponseFactory;

@Component
public class OfferSetResponseFactoryImpl
    implements OfferSetResponseFactory
    {

    /*
     * This factory identifier.
     *
     */
    private final UUID uuid ;

    /*
     * Get the factory's identifier.
     *
     */
    @Override
    public UUID getUuid()
        {
        return this.uuid ;
        }

    private final ExecutionResponseFactory factory ;

    @Autowired
    public OfferSetResponseFactoryImpl(final ExecutionResponseFactory factory)
        {
        this.uuid = UuidCreator.getTimeBased();
        this.factory = factory ;
        }

    /**
     * Our HashMap of OfferSetResponses.
     *
     */
    private Map<UUID, OfferSetResponse> hashmap = new HashMap<UUID, OfferSetResponse>();

    /**
     * Insert an OfferSetResponse into our HashMap.
     *
     */
    protected void insert(final OfferSetResponse offerset)
        {
        this.hashmap.put(
            offerset.getUuid(),
            offerset
            );
        }

    /**
     * Select an OfferSetResponse based on its identifier.
     *
     */
    @Override
    public OfferSetResponse select(final UUID uuid)
        {
        return this.hashmap.get(
            uuid
            );
        }

    /**
     * Create a new OfferSetResponse based on an OfferSetRequest.
     *
     */
    @Override
    public OfferSetResponse create(final String baseurl, final OfferSetRequest request)
        {
        OfferSetResponse response = new OfferSetResponseImpl(baseurl);
        this.insert(
            response
            );
        response.setName(
            request.getName()
            );
        factory.create(
            baseurl,
            request,
            response
            );

        return response ;
        }
    }

