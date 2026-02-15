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

package net.ivoa.calycopis.datamodel.session.simple;

import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;

/**
 * A Factory for execution sessions.
 *
 */
public interface SimpleExecutionSessionEntityFactory
    extends AbstractExecutionSessionEntityFactory<SimpleExecutionSessionEntity>
    {
    
    /**
     * Create a new ExecutionSessionEntity from a parser context and compute resource offer. 
     *
     */
    public SimpleExecutionSessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer);

    /**
     * Save an ExecutionSessionEntity.
     *
     */
    public SimpleExecutionSessionEntity save(final SimpleExecutionSessionEntity entity);
    
    }

