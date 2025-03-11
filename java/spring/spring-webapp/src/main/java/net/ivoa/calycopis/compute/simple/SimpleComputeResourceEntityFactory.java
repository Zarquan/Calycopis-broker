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

package net.ivoa.calycopis.compute.simple;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

/**
 * A SimpleComputeResource Factory.
 *
 */
public interface SimpleComputeResourceEntityFactory
    extends FactoryBase
    {

    /**
     * Select a SimpleComputeResource based on its identifier.
     *
     */
    public Optional<SimpleComputeResourceEntity> select(final UUID uuid);

    /**
     * Create and save a new SimpleComputeResource entity based on a template.
     *
     */
    public SimpleComputeResourceEntity create(
        final ExecutionSessionEntity parent,
        final IvoaSimpleComputeResource template
        );

    /**
     * Create and save a new SimpleComputeResource entity based on a template.
     *
     */
    public SimpleComputeResourceEntity create(
        final ExecutionSessionEntity parent,
        final IvoaSimpleComputeResource template,
        final OfferBlock offerBlock
        );

    /**
     * Create a new SimpleComputeResource entity.
     *
     */
    public SimpleComputeResourceEntity create(
        final ExecutionSessionEntity parent,
        final String name,
        final Long minrequestedcores,
        final Long maxrequestedcores,
        final Long minofferedcores,
        final Long maxofferedcores,
        final Long minrequestedmemory,
        final Long maxrequestedmemory,
        final Long minofferedmemory,
        final Long maxofferedmemory,
        final Boolean minimalcores,
        final Boolean minimalmemory,
        boolean save
        );
    }

