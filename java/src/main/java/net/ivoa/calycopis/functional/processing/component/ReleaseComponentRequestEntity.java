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

package net.ivoa.calycopis.functional.processing.component;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

/**
 * A processing request that releases a component by setting its phase
 * to RELEASING, waiting for a fixed delay, and then setting it to COMPLETED.
 */
@Slf4j
@Entity
@Table(
    name = "releasecomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ReleaseComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {

    public static final long DEFAULT_RELEASE_DELAY_SECONDS = 20;

    protected ReleaseComponentRequestEntity()
        {
        super();
        }

    protected ReleaseComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing release for component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );

        LifecycleComponentEntity component = this.getComponent(
            platform
            );

        if (component == null)
            {
            log.error(
                "Unable to find component in factory [{}][{}]",
                this.componentUuid,
                this.componentKind
                );
            return ProcessingAction.NO_ACTION;
            }

        IvoaLifecyclePhase phase = component.getPhase();
        if (phase == IvoaLifecyclePhase.COMPLETED
            || phase == IvoaLifecyclePhase.FAILED
            || phase == IvoaLifecyclePhase.CANCELLED)
            {
            log.debug(
                "Component [{}][{}] already in terminal phase [{}], nothing to release",
                component.getUuid(),
                component.getClass().getSimpleName(),
                phase
                );
            return ProcessingAction.NO_ACTION;
            }

        component.setPhase(
            IvoaLifecyclePhase.RELEASING
            );

        return component.getReleaseAction(
            platform,
            this
            );
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing release for component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );

        LifecycleComponentEntity component = this.getComponent(
            platform
            );

        if (component == null)
            {
            log.error(
                "Unable to find component in factory [{}][{}]",
                this.componentUuid,
                this.componentKind
                );
            this.done(platform);
            return;
            }

        if (action != null)
            {
            action.postProcess(
                component
                );
            component.setPhase(
                action.getNextPhase()
                );
            }
        else {
            component.setPhase(
                IvoaLifecyclePhase.COMPLETED
                );
            }

        log.debug(
            "Component [{}][{}] release complete, phase now [{}]",
            component.getUuid(),
            component.getClass().getSimpleName(),
            component.getPhase()
            );

        platform.getSessionProcessingRequestFactory().createMonitorSessionRequest(
            component.getSession()
            );

        this.done(platform);
        }
    }
