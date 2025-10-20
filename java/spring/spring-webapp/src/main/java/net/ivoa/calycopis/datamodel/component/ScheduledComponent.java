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

package net.ivoa.calycopis.datamodel.component;

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

/**
 *
 */
public interface ScheduledComponent
extends Component
    {

    /**
     *
     */
    public Instant getPrepareStartInstant();

    /**
     *
     */
    public long getPrepareStartInstantSeconds();

    /**
     *
     */
    public Duration getPrepareDuration();

    /**
     *
     */
    public long getPrepareDurationSeconds();

    /**
     *
     */
    public Interval getAvailableStartInterval();

    /**
     *
     */
    public Instant getAvailableStartInstant();

    /**
     *
     */
    public long getAvailableStartInstantSeconds();

    /**
     *
     */
    public Duration getAvailableStartDuration();

    /**
     *
     */
    public long getAvailableStartDurationSeconds();

    /**
     *
     */
    public Duration getAvailableDuration();

    /**
     *
     */
    public long getAvailableDurationSeconds();

    /**
     *
     */
    public Instant getReleaseStartInstant();

    /**
     *
     */
    public long getReleaseStartInstantSeconds();

    /**
     *
     */
    public Duration getReleaseDuration();

    /**
     *
     */
    public long getReleaseDurationSeconds();
    
    }

