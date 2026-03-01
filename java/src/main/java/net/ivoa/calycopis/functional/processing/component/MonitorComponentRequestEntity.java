/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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

import java.time.Duration;

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
 * A processing request that monitors a component by polling its status
 * via getMonitorAction() and updating the component and session phases
 * accordingly.
 */
@Slf4j
@Entity
@Table(
    name = "monitorcomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class MonitorComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {

    public static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(10);

    protected MonitorComponentRequestEntity()
        {
        super();
        }

    protected MonitorComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing monitor for component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );

        LifecycleComponentEntity component = this.getComponent(
            platform
            );

        if (component != null)
            {
            log.debug(
                "Component found [{}][{}][{}]",
                component.getUuid(),
                component.getPhase(),
                component.getClass().getSimpleName()
                );
            }
        else {
            log.error(
                "Unable to find component in factory [{}][{}]",
                this.componentUuid,
                this.componentKind
                );
            return ProcessingAction.NO_ACTION;
            }

        switch(component.getPhase())
            {
            case AVAILABLE:
            case RUNNING:
                return component.getMonitorAction(
                    this
                    );

            case COMPLETED:
            case FAILED:
            case CANCELLED:
            case RELEASING:
                return ProcessingAction.NO_ACTION;

            default:
                log.error(
                    "Unexpected phase [{}] for monitored component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                return ProcessingAction.NO_ACTION;
            }
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing monitor for component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );

        LifecycleComponentEntity component = this.getComponent(
            platform
            );

        if (component != null)
            {
            log.debug(
                "Component found [{}][{}][{}]",
                component.getUuid(),
                component.getPhase(),
                component.getClass().getSimpleName()
                );
            }
        else {
            log.error(
                "Unable to find component in factory [{}][{}]",
                this.componentUuid,
                this.componentKind
                );
            return ;
            }

        switch(component.getPhase())
            {
            case AVAILABLE:
            case RUNNING:
                if (action != null)
                    {
                    action.postProcess(
                        component
                        );

                    switch (action.getNextPhase())
                        {
                        case RUNNING:
                            component.setPhase(
                                IvoaLifecyclePhase.RUNNING
                                );
                            this.activate(DEFAULT_POLL_INTERVAL);
                            break;

                        case COMPLETED:
                            component.setPhase(
                                IvoaLifecyclePhase.COMPLETED
                                );
                            platform.getSessionProcessingRequestFactory().createMonitorSessionRequest(
                                component.getSession()
                                );
                            this.done(platform);
                            break;

                        case FAILED:
                            this.fail(platform, component);
                            break;

                        default:
                            log.error(
                                "Unexpected next phase [{}] from monitor action for component [{}][{}]",
                                action.getNextPhase(),
                                component.getUuid(),
                                component.getClass().getSimpleName()
                                );
                            this.fail(platform);
                            break;
                        }
                    }
                else {
                    log.debug(
                        "No monitor action for component [{}][{}], marking as COMPLETED",
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    component.setPhase(
                        IvoaLifecyclePhase.COMPLETED
                        );
                    platform.getSessionProcessingRequestFactory().createMonitorSessionRequest(
                        component.getSession()
                        );
                    this.done(platform);
                    }
                break;

            case COMPLETED:
            case FAILED:
            case CANCELLED:
            case RELEASING:
                this.done(platform);
                break;

            default:
                log.error(
                    "Unexpected phase [{}] for monitored component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                this.fail(platform);
                break;
            }
        }
    }
