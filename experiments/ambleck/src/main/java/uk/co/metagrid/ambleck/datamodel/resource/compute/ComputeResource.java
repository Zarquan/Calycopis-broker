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

import uk.co.metagrid.ambleck.datamodel.AbstractObject;
import uk.co.metagrid.ambleck.datamodel.resource.AbstractResource;
import uk.co.metagrid.ambleck.datamodel.util.MinMaxFloat;

public class ComputeResource extends AbstractResource {

    public static final String TYPE_URL = "urn:compute-resource";

    public ComputeResource()
        {
        super(TYPE_URL);
        }

    public ComputeResource(final String name)
        {
        super(name, TYPE_URL);
        }

    public static class ComputeSpecific extends AbstractObject.AbstractSpecific {

        public ComputeSpecific()
            {
            super();
            }

        public ComputeSpecific(final MinMaxFloat cores, final MinMaxFloat memory)
            {
            super();
            this.cores = cores ;
            this.memory = memory ;
            }

        @JsonInclude(Include.NON_NULL)
        private MinMaxFloat cores;
        public MinMaxFloat getCores()
            {
            return this.cores ;
            }
        public void setCores(final MinMaxFloat cores)
            {
            this.cores = cores ;
            }

        private MinMaxFloat memory;
        public MinMaxFloat getMemory()
            {
            return this.memory ;
            }
        public void setMemory(final MinMaxFloat memory)
            {
            this.memory = memory ;
            }

        }

    private ComputeSpecific spec ;
    public ComputeSpecific getSpec()
        {
        return this.spec;
        }
    public void setSpec(final ComputeSpecific spec)
        {
        this.spec = spec;
        }


    }


