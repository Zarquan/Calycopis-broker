/*
 *
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

package uk.co.metagrid.ambleck.webapp;

import java.util.UUID;
import java.util.Map;
import java.util.List;

import uk.co.metagrid.ambleck.model.OffersResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponse;

public interface BrokerDatabase
    {

    public UUID getUuid();

    public void addOffer(final OffersResponse offer);

    public OffersResponse getOffer(final UUID uuid);

    public Iterable<OffersResponse> getOffers();



    public void addExecution(final ExecutionResponse execution);

    public ExecutionResponse getExecution(final UUID uuid);

    public Iterable<ExecutionResponse> getExecutions();

    }

