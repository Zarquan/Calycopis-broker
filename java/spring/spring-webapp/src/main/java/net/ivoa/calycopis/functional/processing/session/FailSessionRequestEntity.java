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
    name = "failsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class FailSessionRequestEntity
extends SessionProcessingRequestEntity
implements FailSessionRequest
    {

    protected FailSessionRequestEntity()
        {
        super();
        }

    protected FailSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            FailSessionRequest.KIND,
            session
            );
        log.debug("Created FailSessionRequestEntity for session [{}]", session.getUuid());
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
                log.debug("Unable to cancel, session [{}][{}] is already [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
                // Nothing more required.
                return null;
                
            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                // Nothing more required.
                return null ;
            }
        //
        // Set the session phase to FAILED.
        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.FAILED
            );
        
        //
        // Cancel all the components ...
        platform.getComputeProcessingRequestFactory().createCancelComputeResourceRequest(
            this.session.getComputeResource()
            );

        //
        // Nothing more required.
        return null;
        }
    }
