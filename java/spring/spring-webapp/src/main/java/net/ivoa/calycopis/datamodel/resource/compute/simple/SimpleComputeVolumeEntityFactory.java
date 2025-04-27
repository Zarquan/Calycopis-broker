/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.datamodel.resource.compute.simple;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume;

/**
 * 
 */
public interface SimpleComputeVolumeEntityFactory
extends FactoryBase
    {
    /**
     * Select a SimpleComputeVolumeEntity based on UUID.
     *
     */
    public Optional<SimpleComputeVolumeEntity> select(final UUID uuid);

    /**
     * Create and save a new SimpleComputeVolumeEntity based on a template.
     *
     */
    public SimpleComputeVolumeEntity create(
        final SimpleComputeResourceEntity parent,
        final IvoaSimpleComputeVolume template
        );

    }
