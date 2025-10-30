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
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResource;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;

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
extends LifecycleComponentEntity
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
     * Automatically adds this resource to the parent SessionEntity.
     * 
     */
    protected AbstractDataResourceEntity(final SessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result,
        final String name
        ){
        super(
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

        //
        // Start preparing when the storage becomes available. 
        this.prepareDurationSeconds     = result.getPreparationTime();
        this.prepareStartInstantSeconds = storage.getAvailableStartInstantSeconds();
        
        //
        // Available as soon as the preparation is done.
        this.availableDurationSeconds      = 0L;
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;
        
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SessionEntity session;
    
    @Override
    public SessionEntity getSession()
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
