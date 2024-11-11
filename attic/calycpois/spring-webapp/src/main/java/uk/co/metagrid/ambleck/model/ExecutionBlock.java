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
package uk.co.metagrid.ambleck.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;

/*
 * Resources data for an Execution block in the database.
 *
 */
public interface ExecutionBlock
    {

    /**
     * The block step size.
     * This controls the granularity of time values in the database.
     * A production system would use one hour steps.
     * The development system uses five minute steps.
     *
     */
    public static final Long BLOCK_STEP_MINUTES = 5L ;
    public static final Long BLOCK_STEP_SECONDS = 60L * BLOCK_STEP_MINUTES ;

    public IvoaExecutionSessionStatus getState();
    public void setState(IvoaExecutionSessionStatus state);

    public UUID getOfferUuid();
    public void setOfferUuid(final UUID offeruuid);

    public UUID getParentUuid();
    public void setParentUuid(final UUID parentuuid);

    public Instant getExpiryTime();
    public void setExpiryTime(final Instant expirytime);

    public Instant  getInstant();
    public Duration getDuration();

    public Long getBlockStart();
    public Long getBlockLength();

    public Integer getMinCores();
    public void setMinCores(final Integer value);

    public Integer getMaxCores();
    public void setMaxCores(final Integer value);

    public Integer getMinMemory();
    public void setMinMemory(final Integer value);

    public Integer getMaxMemory();
    public void setMaxMemory(final Integer value);

    }


