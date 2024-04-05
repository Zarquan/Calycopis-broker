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
 */
package uk.co.metagrid.ambleck.datamodel.resource.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.metagrid.ambleck.datamodel.util.MinMaxInteger;

public class SimpleStorageResource extends AbstractStorageResource {

    public static final String TYPE_URL = "urn:simple-storage-resource";

    public SimpleStorageResource()
        {
        super(TYPE_URL);
        }

    public SimpleStorageResource(final String name)
        {
        super(TYPE_URL, name);
        }

    public static class SimpleStorageSpecific extends AbstractStorageSpecific {

        public SimpleStorageSpecific()
            {
            super();
            }

        public SimpleStorageSpecific(final MinMaxInteger size)
            {
            super();
            this.size = size ;
            }

        @JsonInclude(Include.NON_NULL)
        private MinMaxInteger size;
        public MinMaxInteger getSize()
            {
            return this.size ;
            }
        public void setCores(final MinMaxInteger size)
            {
            this.size = size ;
            }
        }

    private SimpleStorageSpecific spec ;
    public SimpleStorageSpecific getSpec()
        {
        return this.spec;
        }
    public void setSpec(final SimpleStorageSpecific spec)
        {
        this.spec = spec;
        }
    }


