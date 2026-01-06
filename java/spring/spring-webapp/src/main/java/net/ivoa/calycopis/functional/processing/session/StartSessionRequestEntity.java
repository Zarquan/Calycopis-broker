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
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "startsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class StartSessionRequestEntity
extends SessionProcessingRequestEntity
implements StartSessionRequest
    {

    protected StartSessionRequestEntity()
        {
        super();
        }

    protected StartSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug("pre-processing session [{}]", session.getUuid());
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION ;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug("post-processing Session [{}][{}] with phase [{}]", this.session.getUuid(), this.session.getClass().getSimpleName(), this.session.getPhase());
        this.done(
            platform
            );
        }
    }
