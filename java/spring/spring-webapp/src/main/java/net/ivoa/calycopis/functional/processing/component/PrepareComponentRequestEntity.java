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

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
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
            "Pre-processing component [{}][{}][{}]",
            this.component.getUuid(),
            this.component.getPhase(),
            this.component.getClass().getSimpleName()
            );
        switch(this.component.getPhase())
            {
            case INITIALIZING:
            case WAITING:
                // If the start time is in the future.
                if ((this.component.getPrepareStartInstant() != null) && (this.component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    log.debug(
                        "Component [{}][{}] prepare start time is in the future [{}]",
                        this.component.getUuid(),
                        this.component.getClass().getSimpleName(),
                        this.component.getPrepareStartInstant()
                        );
                    // Set the phase to WAITING.
                    this.component.setPhase(
                        IvoaLifecyclePhase.WAITING
                        );
                    // No further action required.
                    return ProcessingAction.NO_ACTION;
                    }
                // Start the prepare process.
                else {
                    // Set the phase to PREPARING.
                    this.component.setPhase(
                        IvoaLifecyclePhase.PREPARING
                        );
                    // Start the prepare action.
                    return this.component.getPrepareAction(
                        this
                        );            
                    }

            case PREPARING:
                // Continue the prepare action.
                return this.component.getPrepareAction(
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
                    this.getComponent().getPhase(),
                    this.getComponent().getUuid(),
                    this.getComponent().getClass().getSimpleName()
                    );
            
            }
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing component [{}][{}][{}]",
            this.getComponent().getUuid(),
            this.getComponent().getPhase(),
            this.getComponent().getClass().getSimpleName()
            );

        switch(this.component.getPhase())
            {
            // Shouldn't get here.
            case INITIALIZING:
                log.error(
                    "Unexpected phase [{}] for component [{}][{}]",
                    this.getComponent().getPhase(),
                    this.getComponent().getUuid(),
                    this.getComponent().getClass().getSimpleName()
                    );
                break;
                
            // Phase is waiting, reschedule this request.
            case WAITING:
                Duration delay = Duration.ofSeconds(30);
                if ((this.component.getPrepareStartInstant() != null) && (this.component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        this.component.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                log.debug(
                    "Re-scheduling request [{}][{}] for component [{}][{}] in [{}]s",
                    this.getUuid(),
                    this.getClass().getSimpleName(),
                    this.component.getUuid(),
                    this.component.getClass().getSimpleName(),
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
                    action.postProcess(
                        this.component
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
                            this.component.setPhase(
                                IvoaLifecyclePhase.AVAILABLE
                                );
                            this.done(platform);
                            break;
    
                        // If the preparation failed, fail this component.
                        case FAILED:
                            this.fail(platform);
                            break;
                            
                        // Anything else doesn't make sense.
                        default:
                            log.error(
                                "Unexpected next phase [{}] result from action for request [{}][{}] for component [{}][{}]",
                                action.getNextPhase(),
                                this.getUuid(),
                                this.getClass().getSimpleName(),
                                this.getComponent().getUuid(),
                                this.getComponent().getClass().getSimpleName()
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
                    this.getComponent().getPhase(),
                    this.getComponent().getUuid(),
                    this.getComponent().getClass().getSimpleName()
                    );
                this.fail(platform);
                break;
            }
        }
    }
