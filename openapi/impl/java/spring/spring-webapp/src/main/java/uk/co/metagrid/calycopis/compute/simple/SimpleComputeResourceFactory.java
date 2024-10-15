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

package uk.co.metagrid.calycopis.compute.simple;

import java.util.Optional;
import java.util.UUID;

import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.factory.FactoryBase;

/**
 * A SimpleComputeResource Factory.
 *
 */
public interface SimpleComputeResourceFactory
    extends FactoryBase
    {

    /**
     * Select a SimpleComputeResource based on its identifier.
     *
     */
    public Optional<SimpleComputeResourceEntity> select(final UUID uuid);

    /**
     * Create and save a new SimpleComputeResource entity.
     *
     */
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final String name, Long requestedcores, Long offeredcores, Long requestedmemory, Long offeredmemory);

    /**
     * Create a new SimpleComputeResource entity.
     *
     */
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final String name, Long requestedcores, Long offeredcores, Long requestedmemory, Long offeredmemory, boolean save);

    /**
     * Create and save a new SimpleComputeResource entity based on a template.
     *
     */
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final SimpleComputeResourceEntity template);

    /**
     * Create and save a new SimpleComputeResource entity based on a template.
     *
     */
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final SimpleComputeResourceEntity template, long offercores, long offermemory);

    }

