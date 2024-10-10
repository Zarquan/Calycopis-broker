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

package uk.co.metagrid.calycopis.execution;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;
import uk.co.metagrid.calycopis.util.FactoryBase;

/**
 * An Execution Factory.
 *
 */
public interface ExecutionFactory
    extends FactoryBase
    {
    /**
     * Select an Execution based on its identifier.
     *
     */
    public Optional<ExecutionEntity> select(final UUID uuid);

    /**
     * Create a new Execution based on an OfferSetRequest.
     *
     */
    public ExecutionEntity create(final IvoaOfferSetRequest request, final OfferSetEntity parent);

    /**
     * Create a new Execution based on an OfferSetRequest.
     *
     */
    public ExecutionEntity create(final IvoaOfferSetRequest request, final OfferSetEntity parent, boolean save);

    }

