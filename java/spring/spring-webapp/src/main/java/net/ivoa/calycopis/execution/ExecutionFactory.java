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

package net.ivoa.calycopis.execution;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offerset.OfferSetEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import net.ivoa.calycopis.openapi.model.IvoaUpdateRequest;

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
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context);

    /**
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state);

    /**
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state, boolean save);

    /**
     * Apply an Update request to an Execution.
     *
     */
    public Optional<ExecutionEntity> update(final UUID uuid, IvoaAbstractUpdate request);
    
    }
