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

import java.time.Instant;
import java.time.Duration;

/*
 * Resources data for an Execution block in the database.
 *
 */
public interface ExecutionBlock
    {

    /**
     * The block step size in seconds.
     * This controls the granularity of time values in the database.
     * A production system would use one hour steps.
     * The development system uses five minute steps.
     *
     */
    public static final Long BLOCK_STEP_SIZE = 60L * 5L ;

    public Instant getInstant();
    public Duration getDuration();

    public Long getBlockStart();
    public Long getBlockLength();
    public Integer getMinCores();
    public Integer getMaxCores();
    public Integer getMinMemory();
    public Integer getMaxMemory();

    }


