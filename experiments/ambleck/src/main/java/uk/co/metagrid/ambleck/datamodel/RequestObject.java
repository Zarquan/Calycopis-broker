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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import uk.co.metagrid.ambleck.datamodel.executable.AbstractExecutable;
import uk.co.metagrid.ambleck.datamodel.resource.AbstractResource;

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
        property = "type",
        defaultImpl = AbstractExecutable.class
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

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
//      include = JsonTypeInfo.As.PROPERTY,
//      include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
//      visible = true,
        defaultImpl = AbstractResource.class
        )
    @JacksonXmlProperty(localName = "resource")
    @JacksonXmlElementWrapper(localName = "resources")
    private List<AbstractResource> resources = new ArrayList<AbstractResource>();
    public List<AbstractResource> getResources()
        {
        return this.resources;
        }
    public void setResources(final List<AbstractResource> resources)
        {
        this.resources = resources;
        }
    public void addResource(final AbstractResource resource)
        {
        this.resources.add(
            resource
            );
        }
    }

