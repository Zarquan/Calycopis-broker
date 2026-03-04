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
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * A session-level processing request that checks whether all components
 * have completed (or failed) and transitions the session phase accordingly.
 */
@Slf4j
@Entity
@Table(
    name = "monitorsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class MonitorSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected MonitorSessionRequestEntity()
        {
        super();
        }

    protected MonitorSessionRequestEntity(final SimpleExecutionSessionEntity session)
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
            "Pre-processing monitor for session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );

        switch(this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
                checkComponents(platform);
                break;

            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
                break;

            default:
                log.debug(
                    "Session [{}] phase [{}] not relevant for monitoring",
                    this.session.getUuid(),
                    this.session.getPhase()
                    );
                break;
            }
        return ProcessingAction.NO_ACTION;
        }

    protected void checkComponents(final Platform platform)
        {
        boolean allCompleted = true;
        boolean anyFailed = false;

        allCompleted &= checkComponent(this.session.getExecutable());
        anyFailed |= isComponentFailed(this.session.getExecutable());

        allCompleted &= checkComponent(this.session.getComputeResource());
        anyFailed |= isComponentFailed(this.session.getComputeResource());

        if (anyFailed)
            {
            log.debug(
                "One or more components FAILED for session [{}], setting session to FAILED",
                this.session.getUuid()
                );
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.FAILED
                );
            }
        else if (allCompleted)
            {
            log.debug(
                "All components COMPLETED for session [{}], setting session to COMPLETED",
                this.session.getUuid()
                );
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.COMPLETED
                );
            }
        else if (this.session.getPhase() == IvoaSimpleExecutionSessionPhase.RELEASING)
            {
            log.debug(
                "Components still releasing for session [{}], keeping session in RELEASING",
                this.session.getUuid()
                );
            }
        else {
            log.debug(
                "Components still running for session [{}], setting session to RUNNING",
                this.session.getUuid()
                );
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.RUNNING
                );
            }
        }

    private boolean checkComponent(final LifecycleComponentEntity component)
        {
        if (component == null)
            {
            return true;
            }
        IvoaLifecyclePhase phase = component.getPhase();
        log.debug(
            "Monitor checking component [{}][{}] phase [{}]",
            component.getUuid(),
            component.getClass().getSimpleName(),
            phase
            );
        return phase == IvoaLifecyclePhase.COMPLETED;
        }

    private boolean isComponentFailed(final LifecycleComponentEntity component)
        {
        if (component == null)
            {
            return false;
            }
        return component.getPhase() == IvoaLifecyclePhase.FAILED;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing monitor for session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        this.done(platform);
        }
    }
