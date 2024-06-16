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
 *
 */
package uk.co.metagrid.ambleck.datamodel;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import uk.co.metagrid.ambleck.datamodel.executable.AbstractExecutable;

import uk.co.metagrid.ambleck.datamodel.resource.compute.AbstractComputeResource;
import uk.co.metagrid.ambleck.datamodel.resource.compute.SimpleComputeResource;
import uk.co.metagrid.ambleck.datamodel.resource.storage.AbstractStorageResource;
import uk.co.metagrid.ambleck.datamodel.resource.storage.SimpleStorageResource;


@JsonRootName("request")
public class RequestObject {

    public RequestObject()
        {
        }

    public RequestObject(final AbstractExecutable executable)
        {
        this.executable = executable;
        }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
        )
    private AbstractExecutable executable;
    public AbstractExecutable getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final AbstractExecutable executable)
        {
        this.executable = executable;
        }

    public class Resources {

        public Resources()
            {
            }

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type"
            )
        @JacksonXmlProperty(localName = "compute-resource")
        @JacksonXmlElementWrapper(localName = "compute")
        @JsonInclude(Include.NON_EMPTY)
        private List<AbstractComputeResource> compute = new ArrayList<AbstractComputeResource>();
        public List<AbstractComputeResource> getCompute()
            {
            return this.compute;
            }
        public void setCompute(final List<AbstractComputeResource> compute)
            {
            this.compute = compute;
            }
        public void addCompute(final AbstractComputeResource resource)
            {
            this.compute.add(
                resource
                );
            }

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type"
            )
        @JacksonXmlProperty(localName = "storage-resource")
        @JacksonXmlElementWrapper(localName = "storage")
        @JsonInclude(Include.NON_EMPTY)
        private List<AbstractStorageResource> storage = new ArrayList<AbstractStorageResource>();
        public List<AbstractStorageResource> getStorage()
            {
            return this.storage;
            }
        public void setStorage(final List<AbstractStorageResource> storage)
            {
            this.storage = storage;
            }
        public void addStorage(final AbstractStorageResource resource)
            {
            this.storage.add(
                resource
                );
            }
        }

    @JacksonXmlProperty(localName = "resources")
    private Resources resources = new Resources() ;
    public Resources getResources()
        {
        return this.resources;
        }
    }

