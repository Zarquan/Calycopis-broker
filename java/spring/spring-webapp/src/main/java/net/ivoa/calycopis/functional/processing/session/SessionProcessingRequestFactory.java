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

import net.ivoa.calycopis.datamodel.session.scheduled.ScheduledExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;

/**
 * 
 */
public interface SessionProcessingRequestFactory
extends FactoryBase
    {

    public PrepareSessionRequest  createPrepareSessionRequest(final ScheduledExecutionSessionEntity session);

    public StartSessionRequest    createStartSessionRequest(final ScheduledExecutionSessionEntity session);

    public MonitorSessionRequest  createMonitorSessionRequest(final ScheduledExecutionSessionEntity session);

    public ReleaseSessionRequest  createReleaseSessionRequest(final ScheduledExecutionSessionEntity session);

    public CancelSessionRequest  createCancelSessionRequest(final ScheduledExecutionSessionEntity session);

    public FailSessionRequest    createFailSessionRequest(final ScheduledExecutionSessionEntity session);

    }
