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

package net.ivoa.calycopis.functional.processing.session;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "preparesessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PrepareSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected PrepareSessionRequestEntity()
        {
        super();
        }

    protected PrepareSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug("pre-processing Session [{}][{}] with phase [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());

        switch (this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
                log.error(
                    "[${}] shouldn't get called if phase is stll [${}]",
                    this.getClass().getSimpleName(),
                    session.getPhase()
                    );
                return failSession(
                    platform
                    );
            //
            // Start to prepare the session
            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
                return beginPreparing(
                    platform
                    );
            //
            // Phase is already PREPARING, no Action required.
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                return ProcessingAction.NO_ACTION ;
            //
            // Phase is past PREPARING, no further Action required.
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                return ProcessingAction.NO_ACTION ;
            
            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                return ProcessingAction.NO_ACTION ;
            }
        }

    protected ProcessingAction beginPreparing(final Platform platform)
        {
        log.debug(
            "Begin preparing Session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        // If we don't need to start preparing yet.
        if (session.getPrepareStartInstant() != null)
            {
            log.debug(
                "Session [{}][{}] prepare start time is set to [{}]",
                this.session.getUuid(),
                this.session.getClass().getSimpleName(),
                this.session.getPrepareStartInstant()
                );
    
            if (session.getPrepareStartInstant().isAfter(Instant.now()))
                {
                log.debug(
                    "Session [{}][{}] prepare start time is in the future [{}], setting phase to WAITING",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPrepareStartInstant()
                    );
                // Set the session phase to WAITING.
                this.session.setPhase(
                    IvoaSimpleExecutionSessionPhase.WAITING
                    );
                //
                // Done for now.
                return ProcessingAction.NO_ACTION ;
                }
            }
        //
        // Set the session phase to PREPARING and prepare the session components.
        log.debug(
            "Setting session [{}][{}] phase to PREPARING and scheduling prepare requests for the components",
            this.session.getUuid(),
            this.session.getClass().getSimpleName()
            );
        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.PREPARING
            );

        log.debug(
            "Scheduling prepare request for session [{}][{}] executable [{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getExecutable().getUuid(),
            this.session.getExecutable().getClass().getSimpleName()
            );
        platform.getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getExecutable()
            );

        log.debug(
            "Scheduling prepare request for session [{}][{}] compute resource [{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getComputeResource().getUuid(),
            this.session.getComputeResource().getClass().getSimpleName()
            );
        platform.getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getComputeResource()
            );

        for(AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            log.debug(
                "Scheduling prepare request for session [{}][{}] storage resource [{}][{}]",
                this.session.getUuid(),
                this.session.getClass().getSimpleName(),
                storageResource.getUuid(),
                storageResource.getClass().getSimpleName()
                );
            platform.getComponentProcessingRequestFactory().createPrepareComponentRequest(
                storageResource
                );
            }

        for(AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            log.debug(
                "Scheduling prepare request for session [{}][{}] data resource [{}][{}]",
                this.session.getUuid(),
                this.session.getClass().getSimpleName(),
                dataResource.getUuid(),
                dataResource.getClass().getSimpleName()
                );
            platform.getComponentProcessingRequestFactory().createPrepareComponentRequest(
                dataResource
                );
            }
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION;
        }

    protected ProcessingAction finishPreparing(final Platform platform)
        {
        log.debug(
            "Finish preparing Session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        //
        // Check if all the components are ready.
        boolean ready = true ;
        
        ready &= checkComponent(
            platform,
            this.session.getExecutable()
            );

        ready &= checkComponent(
            platform,
            this.session.getComputeResource()
            );
        
        for (AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            ready &= checkComponent(
                platform,
                storageResource
                );
            }

        for (AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            ready &= checkComponent(
                platform,
                dataResource
                );
            }

        //
        // If the phase is still PREPARING.
        if (this.session.getPhase() == IvoaSimpleExecutionSessionPhase.PREPARING)
            {
            // 
            // If all the components are ready, set the session phase to AVAILABLE.
            if (ready)
                {
                log.debug(
                    "All the session components are ready, setting phase to [AVAILABLE]"
                    );
                this.session.setPhase( 
                    IvoaSimpleExecutionSessionPhase.AVAILABLE
                    );
                }
            else {
                log.debug(
                    "Some components are not [AVAILABLE] yet, leaving phase as [PREPARING]"
                    );
                }
            return ProcessingAction.NO_ACTION ;
            }
        else {
            log.debug(
                "Session [{}][{}] phase is no longer PREPARING [{}], done finishing prepare",
                this.session.getUuid(),
                this.session.getClass().getSimpleName(),
                this.session.getPhase()
                );
            return ProcessingAction.NO_ACTION ;
            }
        }
    
    protected boolean checkComponent(final Platform platform, final LifecycleComponentEntity component)
        {
        log.debug(
            "Checking component [{}][{}] phase [{}]",
            component.getUuid(),
            component.getClass().getSimpleName(),
            component.getPhase()
            );
        switch(component.getPhase())
            {
            //
            // If the component isn't ready yet.
            case IvoaLifecyclePhase.INITIALIZING:
            case IvoaLifecyclePhase.WAITING:
            case IvoaLifecyclePhase.PREPARING:
                return false ;

            //
            // If the component is ready.
            case IvoaLifecyclePhase.AVAILABLE:
            case IvoaLifecyclePhase.RUNNING:
                return true ;

            //
            // Anything else is an error.
            case IvoaLifecyclePhase.RELEASING:
            case IvoaLifecyclePhase.COMPLETED:
            case IvoaLifecyclePhase.CANCELLED:
            case IvoaLifecyclePhase.FAILED:
            default:
                log.error(
                    "Unexpected component phase [{}][{}][{}] during prepare session [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.failSession(
                    platform
                    );
                return false ;
            }
        }
    
    public static final Duration DEFAULT_WAITING_DELAY = Duration.ofSeconds(30);
    
    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug("post-processing Session [{}][{}] with phase [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
        switch(this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
                log.error(
                    "Request [{}][{}] shouldn't get called if phase is stll [{}]",
                    this.getUuid(),
                    this.getClass().getSimpleName(),
                    this.session.getPhase()
                    );
                this.done(
                    platform
                    );
                break;
            //
            // Phase is WAITING, reschedule this request for half the time difference.
            case IvoaSimpleExecutionSessionPhase.WAITING:
                Duration delay = DEFAULT_WAITING_DELAY;
                if ((this.session.getPrepareStartInstant() != null) && (this.session.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        this.session.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                log.debug(
                    "Session [{}][{}] phase is [{}], re-scheduling request for [{}]s",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPhase(),
                    delay.getSeconds()
                    );
                this.activate(  
                    delay
                    );
                break;
            //
            // Phase is PREPARING, wait until the components are ready.
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                log.debug(
                    "Session [{}][{}] phase is [{}], waiting for components to become [AVAILABLE], no further action required",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPhase()
                    );
                /*
                 * Marking this request as done assumes we have at least one component
                 * (the executable or the compute resource) still in PREPARING phase
                 * that will trigger a new PrepareSessionRequest when the component
                 * becomes AVAILABLE.
                 * If not, then we should re-schedule this request to check again later.
                 * 
                 */
                this.done(
                    platform
                    );  
                break;
            //
            // The phase has moved beyond PREPARING.
            case IvoaSimpleExecutionSessionPhase.RELEASING:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                log.debug(
                    "Session [{}][{}] phase is [{}], no further action required",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.done(
                    platform
                    );  
                break;
                
            default:
                log.error(
                    "Unexpected phase [{}] for session [{}][{}]",
                    this.session.getPhase(),
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.done(
                    platform
                    );
                break;
            }
        }
    }
