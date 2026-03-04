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
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * A session-level processing request that schedules release requests
 * for all active components in the session.
 */
@Slf4j
@Entity
@Table(
    name = "releasesessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ReleaseSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected ReleaseSessionRequestEntity()
        {
        super();
        }

    protected ReleaseSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing release for session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );

        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.RELEASING
            );

        scheduleReleaseIfActive(
            platform,
            this.session.getExecutable()
            );

        scheduleReleaseIfActive(
            platform,
            this.session.getComputeResource()
            );

        for (AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            scheduleReleaseIfActive(
                platform,
                storageResource
                );
            }

        for (AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            scheduleReleaseIfActive(
                platform,
                dataResource
                );
            }

        return ProcessingAction.NO_ACTION;
        }

    protected void scheduleReleaseIfActive(final Platform platform, final LifecycleComponentEntity component)
        {
        if (component == null)
            {
            return;
            }
        IvoaLifecyclePhase phase = component.getPhase();
        switch (phase)
            {
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
                log.debug(
                    "Scheduling release request for component [{}][{}] in phase [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                platform.getComponentProcessingRequestFactory().createReleaseComponentRequest(
                    component
                    );
                break;

            case COMPLETED:
            case FAILED:
            case CANCELLED:
                log.debug(
                    "Component [{}][{}] already in terminal phase [{}], skipping release",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                break;

            default:
                log.debug(
                    "Component [{}][{}] in phase [{}], skipping release",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                break;
            }
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing release for session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        this.done(
            platform
            );
        }
    }
