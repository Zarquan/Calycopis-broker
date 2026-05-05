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

package net.ivoa.calycopis.broker.engine.entities.storage.simple;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator;

/**
 * A SimpleStorageResource Factory.
 *
 */
public interface SimpleStorageResourceEntityFactory
extends AbstractStorageResourceEntityFactory
    {

    /**
     * Select a SimpleStorageResource based UUID.
     *
     */
    public Optional<AbstractStorageResourceEntityImpl> select(final UUID uuid);

    /**
     * Create a new SimpleStorageResourceEntity based on a template.
     *
     */
    public SimpleStorageResourceEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractStorageResourceValidator.Result result
        );

    }

