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

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "cancelsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class CancelSessionRequestEntity
extends SessionProcessingRequestEntity
implements CancelSessionRequest
    {

    protected CancelSessionRequestEntity()
        {
        super();
        }

    protected CancelSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final RequestProcessingPlatform platform)
        {
        log.debug("pre-processing session [{}]", session.getUuid());
        //
        // Check the current phase.
        log.debug("Session [{}][{}] phase is [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
        switch (this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
                break;

            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
            case IvoaSimpleExecutionSessionPhase.PREPARING:
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
                break;
            
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
                break;
            
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                log.debug("Skipping CANCEL for session [{}][{}], phase is already [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
                // Nothing more required.
                return ProcessingAction.NO_ACTION ;
                
            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                // Nothing more required.
                return ProcessingAction.NO_ACTION ;
            }
        //
        // Set the session phase to CANCELLED.
        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.CANCELLED
            );
        
        //
        // Release all of the components ...
        platform.getComputeProcessingRequestFactory().createReleaseComputeResourceRequest(
            this.session.getComputeResource()
            );
        // TODO Release the executable.
        // TODO Release all the data resources.
        // TODO Release all the storage resources.
        
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION ;
        }

    @Override
    public void postProcess(final RequestProcessingPlatform platform, final ProcessingAction action)
        {
        log.debug("post-processing Session [{}][{}] with phase [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
        this.done(
            platform
            );
        }
    }
