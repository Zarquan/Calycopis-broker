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

package net.ivoa.calycopis.datamodel.storage;

import java.net.URI;
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
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.scheduled.ScheduledExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaComponentMetadata;
import net.ivoa.calycopis.util.ListWrapper;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractstorageresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractStorageResourceEntity
extends LifecycleComponentEntity
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
     * Automatically adds this resource to the parent SessionEntity.
     * 
     */
    protected AbstractStorageResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );

        this.session = session;
        this.session.addStorageResource(
            this
            );

        
        if (session instanceof ScheduledExecutionSessionEntity)
            {
            this.init(
                ((ScheduledExecutionSessionEntity) session),
                result
                );
            }
        }
    
    protected void init(
        final ScheduledExecutionSessionEntity session,
        AbstractStorageResourceValidator.Result result
        ){
        //
        // Start preparing when the session starts preparing.
        this.prepareDurationSeconds     = result.getPreparationTime();
        this.prepareStartInstantSeconds = session.getPrepareStartInstantSeconds();

        //
        // Available as soon as the preparation is done.
        this.availableDurationSeconds      = 0L;
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;

        //
        // Hard coded 10s release duration.
        // Start releasing 10s after availability ends.
        this.releaseDurationSeconds = 10L ; 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds + 10L ;         

        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;

    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    @OneToMany(
        mappedBy = "storage",
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
    
    public abstract IvoaAbstractStorageResource makeBean(final URIBuilder builder);
    
    protected IvoaAbstractStorageResource fillBean(final IvoaAbstractStorageResource bean)
        {
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            this.getPhase()
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

    @Override
    protected URI getWebappPath()
        {
        return AbstractStorageResource.WEBAPP_PATH;
        }
    }
