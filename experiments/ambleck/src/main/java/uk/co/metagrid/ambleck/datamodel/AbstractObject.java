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
package uk.co.metagrid.ambleck.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractObject {

    public AbstractObject(final String type)
        {
        this(type, null);
        }

    public AbstractObject(final String type, final String name)
        {
        this.type = type;
        this.name = name;
        }

    @JsonIgnore
    private String type;
    public  String getType()
        {
        return this.type;
        }
    public  void setType(final String type)
        {
        this.type = type;
        }

    private String name;
    public  String getName()
        {
        return this.name;
        }
    public  void setName(final String name)
        {
        this.name = name;
        }


    public static class AbstractSpecific {
        }

    public abstract AbstractSpecific getSpec();

    }


