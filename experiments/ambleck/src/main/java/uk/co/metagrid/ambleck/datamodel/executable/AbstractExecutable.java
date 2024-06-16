/*
 *
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
 * Using Jackson subtypes
 * https://stacktobasics.com/jackson-sub-types
 *
 */
package uk.co.metagrid.ambleck.datamodel.executable;

import uk.co.metagrid.ambleck.datamodel.AbstractObject;

// https://stackoverflow.com/a/23743058
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes(
        {
        @JsonSubTypes.Type(
            value = PingExecutable.class,
            name  = PingExecutable.TYPE_URL
            ),
        @JsonSubTypes.Type(
            value = DelayExecutable.class,
            name  = DelayExecutable.TYPE_URL
            )
        }
    )
public abstract class AbstractExecutable extends AbstractObject {

    public AbstractExecutable(final String type)
        {
        this(type, null);
        }

    public AbstractExecutable(final String type, final String name)
        {
        super(type, name);
        }

    }


