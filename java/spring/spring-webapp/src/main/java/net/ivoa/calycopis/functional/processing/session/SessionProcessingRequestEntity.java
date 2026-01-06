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

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.ProcessingRequestEntity;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "sessionprocessingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class SessionProcessingRequestEntity
extends ProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected SessionProcessingRequestEntity()
        {
        super();
        }

    protected SessionProcessingRequestEntity(final URI kind, final SimpleExecutionSessionEntity session)
        {
        super(kind);
        log.debug("Created SessionProcessingRequestEntity kind [{}] for session [{}]", kind, session.getUuid());
        this.session = session;
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    protected SimpleExecutionSessionEntity session;

    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    protected ProcessingAction failSession(final Platform platform)
        {
        if (this.session != null)
            {
            log.debug("Failing session [{}]", this.session.getUuid());
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.FAILED
                );
            platform.getSessionProcessingRequestFactory().createFailSessionRequest(
                this.session
                );            
            }
        else {
            log.debug("No session to fail");
            }
        // FailSessionRequest issued, no further Action required.
        return ProcessingAction.NO_ACTION ;
        }
    }
