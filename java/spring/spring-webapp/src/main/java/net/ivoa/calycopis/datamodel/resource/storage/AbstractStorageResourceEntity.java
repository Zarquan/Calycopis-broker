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

package net.ivoa.calycopis.datamodel.resource.storage;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ScheduledComponentEntity;
import net.ivoa.calycopis.datamodel.executable.docker.DockerNetworkPortEntity;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainer.NetworkPort;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * 
 */
@Entity
@Table(
    name = "abstractstorageresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractStorageResourceEntity
extends ScheduledComponentEntity
implements AbstractStorageResource
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractStorageResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * Automatically adds this resource to the parent ExecutionSessionEntity.
     * 
     */
    protected AbstractStorageResourceEntity(final ExecutionSessionEntity session, final IvoaComponentSchedule schedule, final String name)
        {
        super(
            schedule,
            name
            );
        this.session = session;
        session.addStorageResource(
            this
            );
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionSessionEntity session;

    @Override
    public ExecutionSessionEntity getSession()
        {
        return this.session;
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractDataResourceEntity> dataresources = new ArrayList<AbstractDataResourceEntity>();

    @Override
    public List<AbstractDataResourceEntity> getDataResources()
        {
        return dataresources;
        }

    public void addDataResource(final AbstractDataResourceEntity resource)
        {
        dataresources.add(
            resource
            );
        }
    
    protected IvoaAbstractStorageResource fillBean(final IvoaAbstractStorageResource bean)
        {
        bean.setUuid(
            this.getUuid()
            );
        bean.setName(
            this.getName()
            );
        bean.setCreated(
            this.getCreated()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        bean.setData(
            new ListWrapper<String, AbstractDataResourceEntity>(
                dataresources
                ){
                public String wrap(final AbstractDataResourceEntity inner)
                    {
                    return inner.getUuid().toString();
                    }
                }
            );
        
        return bean;
        }
    }
