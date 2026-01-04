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
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.scheduled.ScheduledExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;
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
implements PrepareSessionRequest
    {

    protected PrepareSessionRequestEntity()
        {
        super();
        }

    protected PrepareSessionRequestEntity(final ScheduledExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final RequestProcessingPlatform platform)
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
                return failSession(platform);

            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
                return prepareSession(platform);
                
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                // Phase is already PREPARING, no further Action required.
                return ProcessingAction.NO_ACTION ;
                
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                // Phase is past PREPARING, no further Action required.
                return ProcessingAction.NO_ACTION ;
            
            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                return ProcessingAction.NO_ACTION ;
            }
        }

    protected ProcessingAction prepareSession(final RequestProcessingPlatform platform)
        {
        // If we don't need to start preparing yet.
        if ((session.getPrepareStartInstant() != null) && (session.getPrepareStartInstant().isAfter(Instant.now())))
            {
            log.debug(
                "Session [{}] PrepareStart is in the future [{}]",
                this.session.getUuid(),
                session.getPrepareStartInstant()
                );
            // Set the session phase to WAITING.
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.WAITING
                );
            }
        // If we should start preparing now.
        else {
            //
            // Set the session phase to PREPARING.
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.PREPARING
                );
            //
            // Start preparing the executable.
            platform.getExecutableProcessingRequestFactory().createPrepareExecutableRequest(
                this.session.getExecutable()
                );
            //
            // Start preparing the storage resources.
            for(AbstractStorageResourceEntity storageResource : session.getStorageResources())
                {
                platform.getStorageProcessingRequestFactory().createPrepareStorageResourceRequest(
                    storageResource
                    );
                }
            //
            // Start preparing the data resources.
            for(AbstractDataResourceEntity dataResource : session.getDataResources())
                {
                platform.getDataProcessingRequestFactory().createPrepareDataResourceRequest(
                    dataResource
                    );
                }
            //
            // Start preparing the compute resources.
            platform.getComputeProcessingRequestFactory().createPrepareComputeResourceRequest(
                session.getComputeResource()
                );
        
            }
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final RequestProcessingPlatform platform, final ProcessingAction action)
        {
        log.debug("post-processing Session [{}][{}] with phase [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
        switch(this.session.getPhase())
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
                this.done(platform);
                break;

            case IvoaSimpleExecutionSessionPhase.WAITING:
                // Phase is waiting, reschedule this request for half the time between now and then.
                Duration delay = Duration.ofSeconds(30);
                if ((session.getPrepareStartInstant() != null) && (session.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        session.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                log.debug(
                    "Re-scheduling PrepareSessionRequest for session [{}][{}] in [{}]s",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    delay.getSeconds()
                    );
                this.activate(  
                    delay
                    );
                break;

            case IvoaSimpleExecutionSessionPhase.PREPARING:
                // Phase has been set to PREPARING, schedule a start.
                platform.getSessionProcessingRequestFactory().createStartSessionRequest(
                    this.session
                    );
                this.done(platform);
                break;

            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                this.done(platform);
                break;
            }
        }
    }
