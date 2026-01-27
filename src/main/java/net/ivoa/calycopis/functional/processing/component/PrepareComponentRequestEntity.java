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

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.openapi.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "preparecomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PrepareComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {
    
    protected PrepareComponentRequestEntity()
        {
        super();
        }

    protected PrepareComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );
        
        log.debug("Resolving component using platform factories");
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
            case INITIALIZING:
            case WAITING:
                // If the start time is in the future.
                if ((component.getPrepareStartInstant() != null) && (component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    log.debug(
                        "Component [{}][{}] prepare start time is in the future [{}]",
                        component.getUuid(),
                        component.getClass().getSimpleName(),
                        component.getPrepareStartInstant()
                        );
                    // Set the phase to WAITING.
                    component.setPhase(
                        IvoaLifecyclePhase.WAITING
                        );
                    // No further action required.
                    return ProcessingAction.NO_ACTION;
                    }
                // Start the prepare process.
                else {
                    // Set the phase to PREPARING.
                    component.setPhase(
                        IvoaLifecyclePhase.PREPARING
                        );
                    // Start the prepare action.
                    return component.getPrepareAction(
                        this
                        );            
                    }

            case PREPARING:
                // Continue the prepare action.
                return component.getPrepareAction(
                    this
                    );

            case AVAILABLE:
            case RUNNING:
            case RELEASING:
            case COMPLETED:
            case CANCELLED:
            case FAILED:
                // Phase is past PREPARING, no action needed.
                return ProcessingAction.NO_ACTION;

            default:
                log.error(
                    "Unexpected phase [{}] for component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
            
            }
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing component [{}][{}]",
            this.componentUuid,
            this.componentKind
            );
            
        log.debug("Resolving component using platform factories");
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
        
        log.debug(
            "Post-processing component [{}][{}][{}]",
            component.getUuid(),
            component.getPhase(),
            component.getClass().getSimpleName()
            );

        switch(component.getPhase())
            {
            // Shouldn't get here.
            case INITIALIZING:
                log.error(
                    "Unexpected phase [{}] for component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                break;
                
            // Phase is waiting, reschedule this request.
            case WAITING:
                Duration delay = Duration.ofSeconds(30);
                if ((component.getPrepareStartInstant() != null) && (component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        component.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                log.debug(
                    "Re-scheduling request [{}][{}] for component [{}][{}] in [{}]s",
                    this.getUuid(),
                    this.getClass().getSimpleName(),
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    delay.getSeconds()
                    );
                this.activate(delay);
                break;
            //
            // If the component is PREPARING, check the action for the next phase.
            case PREPARING:
                // If we have an action.
                if (action != null)
                    {
                    //
                    // Update the component with the results of the action.
                    log.debug(
                        "** Post-processing action for request [{}][{}] for component [{}][{}]",
                        this.getUuid(),
                        this.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    action.postProcess(
                        component
                        );
                    //
                    // Check the next phase from the action.
                    switch (action.getNextPhase())
                        {
                        // If the preparation is still ongoing, update the activation time and wait.
                        case PREPARING:
                            this.activate();  
                            break;
    
                        // If the preparation has finished, the next phase is AVAILABLE.
                        case AVAILABLE:
                            component.setPhase(
                                IvoaLifecyclePhase.AVAILABLE
                                );
                            // Schedule an update for our session.
                            platform.getSessionProcessingRequestFactory().createPrepareSessionRequest(
                                component.getSession()
                                );
                            // Done.
                            this.done(platform);
                            break;
    
                        // If the preparation failed, fail this component.
                        case FAILED:
                            this.fail(platform, component);
                            break;
                            
                        // Anything else doesn't make sense.
                        default:
                            log.error(
                                "Unexpected next phase [{}] result from action for request [{}][{}] for component [{}][{}]",
                                action.getNextPhase(),
                                this.getUuid(),
                                this.getClass().getSimpleName(),
                                component.getUuid(),
                                component.getClass().getSimpleName()
                                );
                            this.fail(platform);
                            break;
                        }
                    }
                // If we don't have an action, assume we are done
                else {
                    this.done(platform);
                    }
                break;
                
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
            case COMPLETED:
            case CANCELLED:
            case FAILED:
                // Phase is past PREPARING, we are done.
                this.done(platform);
                break;

            default:
                log.error(
                    "Unexpected phase [{}] for component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                this.fail(platform);
                break;
            }
        }
    }
