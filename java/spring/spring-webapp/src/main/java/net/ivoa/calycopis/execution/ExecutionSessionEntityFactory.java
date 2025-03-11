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
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * An Execution Factory.
 *
 */
public interface ExecutionSessionEntityFactory
    extends FactoryBase
    {
    /**
     * Select an Execution based on its identifier.
     *
     */
    public Optional<ExecutionSessionEntity> select(final UUID uuid);

    /**
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context);

    /**
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context, final IvoaExecutionSessionPhase phase);

    /**
     * Create a new Execution based on the contents of an OfferSetRequestParser.
     *
     */
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context, final IvoaExecutionSessionPhase phase, boolean save);

    /**
     * Apply an Update request to an Execution.
     *
     */
    public Optional<ExecutionSessionEntity> update(final UUID uuid, IvoaAbstractUpdate request);
    
    }

