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
public class ExecutionBlockImpl implements ExecutionBlock
    {

    public ExecutionBlockImpl(
        final Instant  instant,
        final Duration duration,
        final Integer minCores,
        final Integer maxCores,
        final Integer minMemory,
        final Integer maxMemory
        ) {
        this.instant  = instant ;
        this.duration = duration ;
        this.blockStart  = instant.getEpochSecond() / ExecutionBlock.BLOCK_STEP_SIZE ;
        this.blockLength = duration.getSeconds() / ExecutionBlock.BLOCK_STEP_SIZE ;
        this.minCores = minCores ;
        this.maxCores = maxCores ;
        this.minMemory = minMemory ;
        this.maxMemory = maxMemory ;
        }

    public ExecutionBlockImpl(
        final Long blockStart,
        final Long blockLength,
        final Integer minCores,
        final Integer maxCores,
        final Integer minMemory,
        final Integer maxMemory
        ) {
        this.blockStart  = blockStart  ;
        this.blockLength = blockLength ;
        this.instant  = Instant.ofEpochSecond(blockStart * ExecutionBlock.BLOCK_STEP_SIZE) ;
        this.duration = Duration.ofSeconds(blockLength * ExecutionBlock.BLOCK_STEP_SIZE) ;
        this.minCores = minCores ;
        this.maxCores = maxCores ;
        this.minMemory = minMemory ;
        this.maxMemory = maxMemory ;
        }

    private Instant instant;
    public Instant getInstant()
        {
        return this.instant;
        }

    private Duration duration;
    public Duration getDuration()
        {
        return this.duration;
        }

    private Long blockStart;
    public Long getBlockStart()
        {
        return this.blockStart;
        }

    private Long blockLength;
    public Long getBlockLength()
        {
        return this.blockLength;
        }

    private Integer minCores;
    public Integer getMinCores()
        {
        return this.minCores;
        }
    public void setMinCores(final Integer value)
        {
        this.minCores = value ;
        }

    private Integer maxCores;
    public Integer getMaxCores()
        {
        return this.maxCores;
        }
    public void setMaxCores(final Integer value)
        {
        this.maxCores = value ;
        }

    private Integer minMemory;
    public Integer getMinMemory()
        {
        return this.minMemory;
        }
    public void setMinMemory(final Integer value)
        {
        this.minMemory = value ;
        }

    private Integer maxMemory;
    public Integer getMaxMemory()
        {
        return this.maxMemory;
        }
    public void setMaxMemory(final Integer value)
        {
        this.maxMemory = value ;
        }
    }

