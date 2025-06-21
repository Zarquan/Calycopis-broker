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

package net.ivoa.calycopis.datamodel.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ScheduledComponentEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResource;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;

/**
 * 
 */
@Entity
@Table(
    name = "abstractdataresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractDataResourceEntity
extends ScheduledComponentEntity
implements AbstractDataResource
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * Automatically adds this resource to the parent ExecutionSessionEntity.
     * 
     */
    protected AbstractDataResourceEntity(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaComponentSchedule schedule, final String name)
        {
        super(
            schedule,
            name
            );
        this.session = session;
        session.addDataResource(
            this
            );
        this.storage = storage;
        storage.addDataResource(
            this
            );
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionSessionEntity session;
    
    @Override
    public ExecutionSessionEntity getSession()
        {
        return this.session ;
        }
    
    @JoinColumn(name = "storage", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AbstractStorageResourceEntity storage;
    @Override
    public AbstractStorageResource getStorage()
        {
        return this.storage;
        }
    public void setStorage(final AbstractStorageResourceEntity storage)
        {
        this.storage = storage;
        }
    
    protected IvoaAbstractDataResource fillBean(final IvoaAbstractDataResource bean)
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
        bean.setStorage(
            this.storage.getUuid().toString()
            );
        return bean;
        }
    }
