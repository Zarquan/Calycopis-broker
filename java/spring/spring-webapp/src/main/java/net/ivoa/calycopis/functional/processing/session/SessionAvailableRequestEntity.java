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
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;


/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "sessionavailablerequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class SessionAvailableRequestEntity
extends SessionProcessingRequestEntity
implements SessionAvailableRequest
    {

    protected SessionAvailableRequestEntity()
        {
        super();
        }

    protected SessionAvailableRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionAvailableRequest.KIND,
            session
            );
        log.debug("Created SessionAvailableRequestRequestEntity for session [{}]", session.getUuid());
        }

    @Override
    public ProcessingAction preProcess(final RequestProcessingPlatform platform)
        {
        log.debug("pre-processing session [{}]", session.getUuid());
        //
        // Check our current phase.
        log.debug("Session phase is [{}]", this.session.getPhase());
        switch (this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
                // Invalid phase, fail the session.
                log.error("Unexpected phase [{}] for session [{}][{}]", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                this.session.setPhase(IvoaSimpleExecutionSessionPhase.FAILED);
                // TODO what do we do with the components ?
                // TODO SHould this initiate a FailSessionRequest ?
                return null ;
            
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                // This is why we are here.
                break ;

            default:
                // Already past available, nothing to do.
                log.debug("Session phase [{}] is already done [{}][{}] nothing to do", this.session.getPhase(), this.session.getUuid(), this.session.getClass().getSimpleName());
                return null ;
            }
        
        
        //
        // Check our storage resources.
        for(AbstractStorageResourceEntity storage : session.getStorageResources())
            {
            }

        //
        // Check our data resources.
        for(AbstractDataResourceEntity data : session.getDataResources())
            {
            }
        
        
        //
        // Check our compute resource.
        session.getComputeResource();
        
        return null;
        }

    
    public boolean checkPhase(final LifecycleComponentEntity component)
        {
        log.debug("Checking component [{}][{}] phase [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
        switch (component.getPhase())
            {
            //
            // The component is still being prepared.
            case IvoaLifecyclePhase.INITIALIZING:
            case IvoaLifecyclePhase.WAITING:
                log.error("Component [{}][{}] is not preparing yet [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
                return true ;
            case IvoaLifecyclePhase.PREPARING:
                log.debug("Component [{}][{}] is still being prepared [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
                return true;
            //
            // The component is ready.
            case IvoaLifecyclePhase.AVAILABLE:
                log.debug("Component [{}][{}] is available [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
                return true;
            //
            // The resource preparation has failed.
            case IvoaLifecyclePhase.FAILED:
                log.debug("Component [{}][{}] has failed [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
                this.session.setPhase(IvoaSimpleExecutionSessionPhase.FAILED);
                return false ;
            //
            // The resource has been cancelled.
            case IvoaLifecyclePhase.CANCELLED:
                log.debug("Component [{}][{}] has been cancelled [{}]", component.getUuid(), component.getClass().getSimpleName(), component.getPhase());
                this.session.setPhase(IvoaSimpleExecutionSessionPhase.CANCELLED);
                return false ;
            //
            // Anything else is a failure.
            default:
                log.error("Unexpected phase [{}] for component [{}][{}]", component.getPhase(), component.getUuid(), component.getClass().getSimpleName());
                this.session.setPhase(IvoaSimpleExecutionSessionPhase.FAILED);
                return false ;
            }
        
        }
    }
