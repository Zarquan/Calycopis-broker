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

package net.ivoa.calycopis.broker.engine.entities.data.simple;

import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;

/**
 * A SimpleDataResource Factory.
 *
 */
public interface SimpleDataResourceEntityFactory
extends AbstractDataResourceEntityFactory
    {

    /**
     * Create a new SimpleDataResource based on a template.
     *
     */
    public SimpleDataResourceEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractStorageResourceEntityImpl storage,
        final AbstractDataResourceValidator.Result result
        );

    }

