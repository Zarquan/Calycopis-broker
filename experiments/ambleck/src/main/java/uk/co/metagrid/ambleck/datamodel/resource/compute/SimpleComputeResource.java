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
package uk.co.metagrid.ambleck.datamodel.resource.compute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.metagrid.ambleck.datamodel.util.MinMaxInteger;

public class SimpleComputeResource extends AbstractComputeResource {

    public static final String TYPE_URL = "urn:simple-compute-resource";

    public SimpleComputeResource()
        {
        super(TYPE_URL);
        }

    public SimpleComputeResource(final String name)
        {
        super(TYPE_URL, name);
        }

    public static class SimpleComputeSpecific extends AbstractComputeSpecific {

        public SimpleComputeSpecific()
            {
            super();
            }

        public SimpleComputeSpecific(final MinMaxInteger cores, final MinMaxInteger memory)
            {
            super();
            this.cores = cores ;
            this.memory = memory ;
            }

        @JsonInclude(Include.NON_NULL)
        private MinMaxInteger cores;
        public MinMaxInteger getCores()
            {
            return this.cores ;
            }
        public void setCores(final MinMaxInteger cores)
            {
            this.cores = cores ;
            }

        private MinMaxInteger memory;
        public MinMaxInteger getMemory()
            {
            return this.memory ;
            }
        public void setMemory(final MinMaxInteger memory)
            {
            this.memory = memory ;
            }

        }

    private SimpleComputeSpecific spec ;
    public SimpleComputeSpecific getSpec()
        {
        return this.spec;
        }
    public void setSpec(final SimpleComputeSpecific spec)
        {
        this.spec = spec;
        }

    }


