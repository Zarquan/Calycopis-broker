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

    protected PrepareSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            PrepareSessionRequest.KIND,
            session
            );
        log.debug("Created PrepareSessionRequestEntity for session [{}]", session.getUuid());
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
            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
                break ;

            default:
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                // Already gone past available, nothing to do.
                return null ;
            }
        //
        // Set the session phase to PREPARING.
        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.PREPARING
            );
        
        //
        // Start processing the executable.
        platform.getExecutableProcessingRequestFactory().createPrepareExecutableRequest(
            this.session.getExecutable()
            );

        //
        // Start processing the storage.
        for(AbstractStorageResourceEntity storage : session.getStorageResources())
            {
            platform.getStorageProcessingRequestFactory().createPrepareStorageResourceRequest(
                storage
                );
            }

        //
        // Start processing the compute.
        platform.getComputeProcessingRequestFactory().createPrepareComputeResourceRequest(
            session.getComputeResource()
            );
        
        return null;
        }
    }
