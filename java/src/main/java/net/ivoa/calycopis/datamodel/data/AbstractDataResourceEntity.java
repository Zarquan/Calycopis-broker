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

import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResource;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
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
     * TODO meta can be replaced by Result.getObject().getMeta()
     * 
     */
    protected AbstractDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );

        this.session = session;
        this.session.addDataResource(
            this
            );

        this.storage = storage;
        this.storage.addDataResource(
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
        
        //
        // Hard coded 10s release duration.
        // Start releasing as soon as availability ends.
        this.releaseDurationSeconds = 10L ; 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds ;         
        
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
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

    public abstract IvoaAbstractDataResource makeBean(final URIBuilder builder);

    protected IvoaAbstractDataResource fillBean(final IvoaAbstractDataResource bean)
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
        bean.setStorage(
            this.storage.getUuid().toString()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractDataResource.WEBAPP_PATH;
        }

    // TODO Move this to a test specific class.
    @Column(name="preparecounter")
    private int preparecounter;
    public int getPrepareCounter()
        {
        return this.preparecounter;
        }

    // Generic prepare action - move to the real Entities later.
    @Override
    public ProcessingAction getPrepareAction(final ComponentProcessingRequest request)
        {
        return new ProcessingAction()
            {

            int count = AbstractDataResourceEntity.this.preparecounter ;
            
            @Override
            public boolean process()
                {
                log.debug(
                    "Preparing [{}][{}] count [{}]",
                    AbstractDataResourceEntity.this.getUuid(),
                    AbstractDataResourceEntity.this.getClass().getSimpleName(),
                    count
                    );
    
                count++;
                try {
                    Thread.sleep(1000);
                    }
                catch (InterruptedException e)
                    {
                    log.error(
                        "Interrupted while preparing [{}][{}]",
                        AbstractDataResourceEntity.this.getUuid(),
                        AbstractDataResourceEntity.this.getClass().getSimpleName()
                        );
                    }
    
                return true ;
                }
    
            @Override
            public UUID getRequestUuid()
                {
                return request.getUuid();
                }
    
            @Override
            public IvoaLifecyclePhase getNextPhase()
                {
                if (count < 4)
                    {
                    return IvoaLifecyclePhase.PREPARING ;
                    }
                else {
                    return IvoaLifecyclePhase.AVAILABLE ;
                    }
                }
            
            @Override
            public boolean postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof AbstractDataResourceEntity)
                    {
                    return postProcess(
                        (AbstractDataResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected component type [{}] post processing [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    return false ;
                    }
                }
                
            public boolean postProcess(final AbstractDataResourceEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                component.preparecounter = this.count ;
                return true ;
                }
            };
        }
    }
