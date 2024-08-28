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

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

//import com.github.f4b6a3.uuid.UuidCreator;

import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.OfferSetResponseImpl;
import uk.co.metagrid.ambleck.model.OfferSetResponseFactory;

import uk.co.metagrid.ambleck.model.ExecutionResponseFactory;
import uk.co.metagrid.ambleck.platform.CanfarProcessingContextFactory;
import uk.co.metagrid.ambleck.platform.Execution;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OfferSetResponseFactoryImpl
    extends FactoryBase
    implements OfferSetResponseFactory
    {
    /*
     * The default expiry time for offers, in minutes.
     *
     */
    public static final int DEFAULT_EXPIRY_TIME = 5 ;

    /**
     * Our ExecutionResponseFactory instance.
     *
     */
    private final ExecutionResponseFactory responder ;

    /**
     * Our CanfarExecutionFactory instance.
     *
     */
    private final CanfarProcessingContextFactory canfarder ;

    @Autowired
    public OfferSetResponseFactoryImpl(final ExecutionResponseFactory responder, final CanfarProcessingContextFactory canfarder)
        {
        super();
        this.responder = responder ;
        this.canfarder = canfarder ;
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
        log.debug(
            "Creating OfferSetResponse"
            );
        OfferSetResponseImpl offerset = new OfferSetResponseImpl(
            OffsetDateTime.now().plusMinutes(
                DEFAULT_EXPIRY_TIME
                ),
            baseurl
            );

        ProcessingContext context = canfarder.create(
            baseurl,
            request,
            offerset
            );
        offerset.setExecution(
            context.getExecution()
            );
        offerset.setName(
            request.getName()
            );
        this.insert(
            offerset
            );
        responder.create(
            baseurl,
            request,
            offerset,
            context
            );
        return offerset ;
        }
    }

