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

package net.ivoa.calycopis.datamodel.session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * An Execution Factory.
 *
 */
public interface SessionEntityFactory
    extends FactoryBase
    {
    /**
     * Select an SessionEntity based on UUID.
     *
     */
    public Optional<SessionEntity> select(final UUID uuid);

    /**
     * Select ExecutionSessionEntities based on phase.
     *
     */
    public List<SessionEntity> select(final IvoaExecutionSessionPhase phase);
    
    /**
     * Create a new SessionEntity from a parser context and compute resource offer. 
     *
     */
    public SessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer);

    /**
     * Apply an Update request to an Execution.
     *
     */
    public Optional<SessionEntity> update(final UUID uuid, final IvoaAbstractUpdate request);

    /**
     * Save a SessionEntity.
     *
     */
    public SessionEntity save(final SessionEntity entity);
    
    }

