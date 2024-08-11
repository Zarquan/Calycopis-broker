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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.stereotype.Component;

import uk.co.metagrid.ambleck.model.OffersResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponse;

@Component
public class BrokerDatabaseImpl implements BrokerDatabase
    {

    private UUID uuid ;

    public UUID getUuid()
        {
        return this.uuid ;
        }

    public BrokerDatabaseImpl()
        {
        this.uuid = UuidCreator.getTimeBased();
        }

    private Map<UUID, OffersResponse> offers = new HashMap<UUID, OffersResponse>();

    public void addOffer(final OffersResponse offer)
        {
        offers.put(
            offer.getUuid(),
            offer
            );
        for(ExecutionResponse execution : offer.getExecutions())
            {
            this.addExecution(
                execution
                );
            }
        }

    public OffersResponse getOffer(final UUID uuid)
        {
        return offers.get(
            uuid
            );
        }

    public Iterable<OffersResponse> getOffers()
        {
        return offers.values();
        }

    private Map<UUID, ExecutionResponse> executions = new HashMap<UUID, ExecutionResponse>();

    public void addExecution(final ExecutionResponse execution)
        {
        executions.put(
            execution.getUuid(),
            execution
            );
        }

    public ExecutionResponse getExecution(final UUID uuid)
        {
        return executions.get(
            uuid
            );
        }

    public Iterable<ExecutionResponse> getExecutions()
        {
        return executions.values();
        }

    }

