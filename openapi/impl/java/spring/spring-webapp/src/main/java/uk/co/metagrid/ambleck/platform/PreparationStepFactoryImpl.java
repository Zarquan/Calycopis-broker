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
package uk.co.metagrid.ambleck.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.co.metagrid.ambleck.model.FactoryBase;

public class PreparationStepFactoryImpl
    extends FactoryBase
    implements PreparationStepFactory
    {

    /**
     * Our internal Map of steps.
     *
     */
    private Map<UUID, PreparationStep<?>> hashmap = new HashMap<UUID, PreparationStep<?>>();

    /**
     * Add a PreparationStep to our Map.
     *
     */
    protected void insert(final PreparationStep<?> step)
        {
        hashmap.put(
            step.getUuid(),
            step
            ) ;
        }

    /**
     * Select a PreparationStep based on it's uuid.
     * Used by callbacks to locate theirt target.
     *
     */
    @Override
    public PreparationStep<?> select(final UUID uuid)
        {
        return hashmap.get(uuid) ;
        }
    }


