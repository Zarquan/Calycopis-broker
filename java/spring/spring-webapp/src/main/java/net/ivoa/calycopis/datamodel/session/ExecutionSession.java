/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.datamodel.session;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.datamodel.component.ScheduledComponent;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.resource.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;

/**
 * Public interface for an execution session.
 *
 */
public interface ExecutionSession
    extends ScheduledComponent
    {
    /**
     * The type identifier for an execution session response.
     *
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/sessions/execution-session-response-1.0" ;

    /**
     * The URL path for an execution session.
     *
     */
    public static final String REQUEST_PATH = "/sessions/" ;

    /**
     * Get the Execution phase.
     *
     */
    public IvoaExecutionSessionPhase getPhase();

    /**
     * Set the Execution phase.
     *
     */
    void setPhase(final IvoaExecutionSessionPhase phase);

    /**
     * Get the expiry date for an OFFERED Execution.
     *
     */
    public OffsetDateTime getExpires();

    /**
     * Get the parent OfferSet.
     *
     */
    public OfferSetEntity getOfferSet();

    /**
     * Get the start of the preparation step in seconds.
     *
    public long getPrepareStartInstantSeconds();
     */

    /**
     * Get the start of the preparation step as an Instant.
     *
    public Instant getPrepareStartInstant();
     */

    /**
     * Get the length of the preparation step in seconds.
     *
    public long getPrepareDurationSeconds();
     */

    /**
     * Get the length of the preparation step as a Duration.
     *
    public Duration getPrepareDuration();
     */

    /**
     * Get the end of the preparation step in seconds.
     *
    public long getPrepareDoneInstantSeconds();
     */

    /**
     * Get the end of the preparation step as an Instant.
     *
    public Instant getPrepareDoneInstant();
     */

    /**
     * Get the start of the execution start interval, in seconds.
     *
    public long getExecutionStartInstantSeconds();
     */

    /**
     * Get the start of the execution start interval, as an Instant.
     *
    public Instant getExecutionStartInstant();
     */

    /**
     * Get the length of the execution start interval in seconds.
     *
    public long getExecutionStartDurationSeconds();
     */

    /**
     * Get the length of the execution start interval as a Duration.
     *
    public Duration getExecutionStartDuration();
     */

    /**
     * Get the execution start interval (instant + duration) as an Interval.
     *
    public Interval getExecutionStartInterval();
     */

    /**
     * Get the execution duration in seconds.
     *
    public long getExecutionDurationSeconds();
     */

    /**
     * Get the execution duration as a Duration.
     *
    public Duration getExecutionDuration();
     */

    /**
     * Get the execution interval as an Interval.
     *
    public Interval getExecutionInterval();
     */

    /**
     * Get the start of the release step in seconds. 
     *
    public long getReleaseStartInstantSeconds();
     */

    /**
     * Get the start of the release step as an Instant. 
     *
    public Instant getReleaseStartInstant();
     */

    /**
     * Get the length of the release step in seconds. 
     *
    public long getReleaseDurationSeconds();
     */

    /**
     * Get the length of the release step as a Duration. 
     *
    public Duration getReleaseDuration();
     */

    /**
     * Get the end of the release step in seconds. 
     *
    public long getReleaseDoneInstantSeconds();
     */

    /**
     * Get the end of the release step as an Instant. 
     *
    public Instant getReleaseDoneInstant();
     */
    
    /**
     * Get the Executable entity.
     *
     */
    public AbstractExecutableEntity getExecutable();

    /**
     * Get a list of the ComputeResources.
     *
     */
    public List<AbstractComputeResourceEntity> getComputeResources();

    /**
     * Get a list of the DataResources.
     *
     */
    public List<AbstractDataResourceEntity> getDataResources();

    /**
     * Get a list of the StorageResources.
     *
     */
    public List<AbstractStorageResourceEntity> getStorageResources();

    /**
     * Get a list of the VolumeMounts.
     *
     */
    public List<AbstractVolumeMountEntity> getVolumeMounts();
    
    /**
     * Get an Ivoa bean representation.
     *  
     */
    public IvoaExecutionSessionResponse getIvoaBean(final String baseurl);

    }

