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

package net.ivoa.calycopis.functional.processing.executable;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "prepareexecutablerequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PrepareExecutableRequestEntity
extends ExecutableProcessingRequestEntity
implements PrepareExecutableRequest
    {

    protected PrepareExecutableRequestEntity()
        {
        super();
        }

    protected PrepareExecutableRequestEntity(final AbstractExecutableEntity executable)
        {
        super(
            PrepareExecutableRequest.KIND,
            executable
            );
        }

    @Override
    public ProcessingAction preProcess(final RequestProcessingPlatform platform)
        {
        log.debug("Pre-processing request [{}][{}] for [{}][{}]", this.getUuid(), this.getClass().getSimpleName(), this.getExecutable().getUuid(), getExecutable().getClass().getSimpleName());
        //
        // Mark our resource as PREPARING.
        this.getExecutable().setPhase(IvoaLifecyclePhase.PREPARING);
        //
        // Create an Action to do the work outside the database Transaction.
        return new ProcessingAction()
            {
            private UUID requestUuid = PrepareExecutableRequestEntity.this.getUuid();
            @Override
            public UUID getRequestUuid()
                {
                return this.requestUuid;
                }

            private UUID   resourceUuid  = PrepareExecutableRequestEntity.this.getExecutable().getUuid();
            private String resourceClass = PrepareExecutableRequestEntity.this.getExecutable().getClass().getSimpleName();

            @Override
            public boolean process()
                {
                //
                // Prepare our resource ....
                log.debug("Preparing resource [{}][{}]", resourceUuid, resourceClass);
                try {
                    Thread.sleep(22000);
                    }
                catch (final InterruptedException ouch)
                    {
                    log.debug("InterruptedException [{}][{}] while preparing data resource [{}][{}]", ouch.getClass().getSimpleName(), ouch.getMessage(), resourceUuid, resourceClass);
                    }
                return true;
                }
            };
        }

    @Override
    public void postProcess(final RequestProcessingPlatform platform)
        {
        log.debug("Post-processing request [{}][{}] for [{}][{}]", this.getUuid(), this.getClass().getSimpleName(), this.getExecutable().getUuid(), getExecutable().getClass().getSimpleName());
        //
        // TODO Check that the executable is prepared ....
        //
        // Mark our resource as AVAILABLE.
        this.getExecutable().setPhase(IvoaLifecyclePhase.AVAILABLE);
        //
        // Activate our parent Session.
        platform.getSessionProcessingRequestFactory().createSessionAvailableRequest(
            (SimpleExecutionSessionEntity) this.getExecutable().getSession()
            );
        //
        // Close this request.
        super.postProcess(platform);
        }
    }
