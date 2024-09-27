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

import java.util.UUID;

import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse;

import java.time.Instant;
import java.time.Duration;

/*
 * Resources data for an Execution block in the database.
 *
 */
public class ExecutionBlockImpl implements ExecutionBlock
    {

    public ExecutionBlockImpl(
        final IvoaExecutionResponse.StateEnum blockState,
        final UUID offeruuid,
        final UUID parentuuid,
        final Instant expirytime,
        final Instant instant,
        final Duration duration,
        final Integer minCores,
        final Integer maxCores,
        final Integer minMemory,
        final Integer maxMemory
        ) {
        this.blockState  = blockState ;
        this.offeruuid   = offeruuid ;
        this.parentuuid  = parentuuid ;
        this.expirytime  = expirytime ;
        this.instant     = instant ;
        this.duration    = duration ;
        this.blockStart  = instant.getEpochSecond() / ExecutionBlock.BLOCK_STEP_SECONDS ;
        this.blockLength = duration.getSeconds() / ExecutionBlock.BLOCK_STEP_SECONDS ;
        this.minCores    = minCores ;
        this.maxCores    = maxCores ;
        this.minMemory   = minMemory ;
        this.maxMemory   = maxMemory ;
        }

    public ExecutionBlockImpl(
        final String blockState,
        final UUID offeruuid,
        final UUID parentuuid,
        final Instant expirytime,
        final Long blockStart,
        final Long blockLength,
        final Integer minCores,
        final Integer maxCores,
        final Integer minMemory,
        final Integer maxMemory
        ) {
        this(
            IvoaExecutionResponse.StateEnum.fromValue(
                blockState
                ),
            offeruuid,
            parentuuid,
            expirytime,
            blockStart,
            blockLength,
            minCores,
            maxCores,
            minMemory,
            maxMemory
            );
        }

    public ExecutionBlockImpl(
        final IvoaExecutionResponse.StateEnum blockState,
        final UUID offeruuid,
        final UUID parentuuid,
        final Instant expirytime,
        final Long blockStart,
        final Long blockLength,
        final Integer minCores,
        final Integer maxCores,
        final Integer minMemory,
        final Integer maxMemory
        ) {
        this.blockState  = blockState ;
        this.offeruuid   = offeruuid ;
        this.parentuuid  = parentuuid ;
        this.expirytime  = expirytime ;
        this.blockStart  = blockStart ;
        this.blockLength = blockLength ;
        this.instant     = Instant.ofEpochSecond(blockStart * ExecutionBlock.BLOCK_STEP_SECONDS) ;
        this.duration    = Duration.ofSeconds(blockLength * ExecutionBlock.BLOCK_STEP_SECONDS) ;
        this.minCores    = minCores ;
        this.maxCores    = maxCores ;
        this.minMemory   = minMemory ;
        this.maxMemory   = maxMemory ;
        }

    private IvoaExecutionResponse.StateEnum blockState;
    public IvoaExecutionResponse.StateEnum getState()
        {
        return this.blockState;
        }
    public void setState(IvoaExecutionResponse.StateEnum state)
        {
        this.blockState = state ;
        }

    private UUID offeruuid;
    public UUID getOfferUuid()
        {
        return this.offeruuid;
        }
    public void setOfferUuid(final UUID offeruuid)
        {
        this.offeruuid = offeruuid;
        }

    private UUID parentuuid;
    public UUID getParentUuid()
        {
        return this.parentuuid;
        }
    public void setParentUuid(final UUID parentuuid)
        {
        this.parentuuid = parentuuid;
        }

    private Instant expirytime;
    public Instant getExpiryTime()
        {
        return this.expirytime;
        }
    public void setExpiryTime(final Instant expirytime)
        {
        this.expirytime= expirytime ;
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

