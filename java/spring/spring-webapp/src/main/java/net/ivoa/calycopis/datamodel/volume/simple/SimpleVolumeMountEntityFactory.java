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

package net.ivoa.calycopis.datamodel.volume.simple;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.functional.factory.FactoryBase;

/**
 * A SimpleVolumeMount Factory.
 *
 */
public interface SimpleVolumeMountEntityFactory
    extends FactoryBase
    {

    /**
     * Select a SimpleVolumeMount based UUID.
     *
     */
    public Optional<SimpleVolumeMountEntity> select(final UUID uuid);

    /**
     * Create a new SimpleVolumeMountEntity based on a template.
     *
     */
    public SimpleVolumeMountEntity create(
        final AbstractExecutionSessionEntity session,
        final AbstractVolumeMountValidator.Result result
        );

    }

