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

public interface ExecutionBlockDatabase
    {

    /**
     * Insert an ExecutionBlock into our database.
     *
     */
    public int insert(final ExecutionBlock block);

    /**
     * Select an ExecutionBlock from our database.
     *
     */
    public ExecutionBlock select(final UUID offeruuid);

    /**
     * Update an ExecutionBlock in our database.
     *
     */
    public int update(final UUID offeruuid, final ExecutionResponse.StateEnum newstate);

    /**
     * Generate a list of ExecutionBlock offers based on a ProcessingContext.
     *
     */
    public List<ExecutionBlock> generate(final ProcessingContext context);

    /**
     * Sweep the database for expired offers.
     *
     */
    public int sweep(final Integer limit);

    }

