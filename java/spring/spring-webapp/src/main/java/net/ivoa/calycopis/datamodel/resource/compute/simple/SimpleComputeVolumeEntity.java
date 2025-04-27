/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.datamodel.resource.compute.simple;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume.ModeEnum;

/**
 * 
 */
@Entity
@Table(
    name = "simplecomputevolumes"
    )
@DiscriminatorValue(
    value = "uri:simple-compute-volumes"
    )
public class SimpleComputeVolumeEntity
extends ComponentEntity
implements SimpleComputeVolume
    {
    @Id
    @GeneratedValue
    protected UUID uuid;
    public UUID getUuid()
        {
        return this.uuid ;
        }

    /**
     * 
     */
    protected SimpleComputeVolumeEntity()
        {
        super();
        }

    /**
     * 
     */
    protected SimpleComputeVolumeEntity(
        final SimpleComputeResourceEntity parent,
        final IvoaSimpleComputeVolume template
        ){
        super();
        this.parent = parent;
        this.path = template.getPath();
        this.mode = template.getMode();
        }

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleComputeResourceEntity parent ;
    @Override
    public SimpleComputeResourceEntity getParent()
        {
        return this.parent;
        }

    @Override
    public List<AbstractDataResource> getDataResources()
        {
        // TODO Auto-generated method stub
        return null;
        }

    private ModeEnum mode;
    @Override
    public ModeEnum getMode()
        {
        return this.mode;
        }

    private String path;
    @Override
    public String getPath()
        {
        return this.path;
        }
    }
