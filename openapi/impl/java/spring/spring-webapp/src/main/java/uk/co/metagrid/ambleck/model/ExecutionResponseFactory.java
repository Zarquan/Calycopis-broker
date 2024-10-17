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

import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

public interface ExecutionResponseFactory
    {

    /*
     * Get the factory's identifier.
     *
     */
    public UUID getUuid();

    /**
     * Select an Execution based on its identifier.
     *
     */
    public ExecutionResponseImpl select(final UUID uuid);

    /**
     * Process an OfferSetRequest and populate an OfferSetResponse with Execution offers.
     *
     */
    public void create(final String baseurl, final IvoaOfferSetRequest request, final OfferSetAPI response, final ProcessingContext<?> context);

    /**
     * Update an Execution.
     *
     */
    public IvoaExecutionSessionResponse update(final UUID uuid, final IvoaAbstractUpdate update);

    }
