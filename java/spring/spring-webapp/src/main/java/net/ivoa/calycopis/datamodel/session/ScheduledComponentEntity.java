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

package net.ivoa.calycopis.datamodel.session;

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaScheduleDurationInstant;
import net.ivoa.calycopis.openapi.model.IvoaScheduleDurationInterval;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionSchedule;


/**
 * 
 */
@Slf4j
@Entity
@Table(name = "scheduledcomponents")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class ScheduledComponentEntity
extends ComponentEntity
implements ScheduledComponent
    {
    /**
     * 
     */
    public ScheduledComponentEntity()
        {
        super();
        }

    /**
     * 
     */
    public ScheduledComponentEntity(final String name)
        {
        this(
            (IvoaExecutionSessionSchedule)null,
            name
            );
        }
    /**
     * 
     */
    public ScheduledComponentEntity(final IvoaExecutionSessionSchedule schedule, final String name)
        {
        super(
            name
            );
        if (schedule != null)
            {
            IvoaScheduleDurationInstant preparing = schedule.getPreparing();
            if (null != preparing)
                {
                String startInstantString = preparing.getStart();
                if (null != startInstantString)
                    {
                    try {
                        this.prepareStartInstantSeconds = Instant.parse(
                            startInstantString
                            ).getEpochSecond();
                        }
                    catch (Exception ouch)
                        {
                        log.warn("Exception parsing prepare start instant [{}][{}]", startInstantString, ouch.getMessage());
                        }
                    }
                String durationString = preparing.getDuration();
                if (null != durationString)
                    {
                    try {
                        this.prepareDurationSeconds = Duration.parse(
                            durationString
                            ).getSeconds();
                        }
                    catch (Exception ouch)
                        {
                        log.warn("Exception parsing prepare duration [{}][{}]", durationString, ouch.getMessage());
                        }
                    }
                }
            }
        }
    
    @Column(name = "prepare_start_instant_seconds")
    protected long prepareStartInstantSeconds;
    @Override
    public long getPrepareStartInstantSeconds()
        {
        return this.prepareStartInstantSeconds;
        }
    @Override
    public Instant getPrepareStartInstant()
        {
        return Instant.ofEpochSecond(
            prepareStartInstantSeconds
            );
        }
    
    @Column(name = "prepare_duration_seconds")
    protected long prepareDurationSeconds;
    @Override
    public long getPrepareDurationSeconds()
        {
        return this.prepareDurationSeconds;
        }
    @Override
    public Duration getPrepareDuration()
        {
        return Duration.ofSeconds(
            prepareDurationSeconds
            );
        }

    @Column(name = "available_start_instant_seconds")
    protected long availableStartInstantSeconds;
    @Override
    public long getAvailableStartInstantSeconds()
        {
        return this.availableStartInstantSeconds;
        }
    @Override
    public Instant getAvailableStartInstant()
        {
        return Instant.ofEpochSecond(
            availableStartInstantSeconds
            );
        }
    @Column(name = "available_start_duration_seconds")
    protected long availableStartDurationSeconds;
    @Override
    public long getAvailableStartDurationSeconds()
        {
        return this.availableStartDurationSeconds;
        }
    @Override
    public Duration getAvailableStartDuration()
        {
        return Duration.ofSeconds(
            availableStartDurationSeconds
            );
        }
    @Override
    public Interval getAvailableStartInterval()
        {
        return Interval.of(
            getAvailableStartInstant(),
            getAvailableStartDuration()
            );
        }
    
    @Column(name = "available_duration_seconds")
    protected long availableDurationSeconds;
    @Override
    public long getAvailableDurationSeconds()
        {
        return this.availableDurationSeconds;
        }
    @Override
    public Duration getAvailableDuration()
        {
        return Duration.ofSeconds(
            availableDurationSeconds
            );
        }
    
    @Column(name = "release_start_instant_seconds")
    protected long releaseStartInstantSeconds;
    @Override
    public long getReleaseStartInstantSeconds()
        {
        return this.releaseStartInstantSeconds;
        }
    @Override
    public Instant getReleaseStartInstant()
        {
        return Instant.ofEpochSecond(
            releaseStartInstantSeconds
            );
        }
    
    @Column(name = "release_duration_seconds")
    protected long releaseDurationSeconds;
    @Override
    public long getReleaseDurationSeconds()
        {
        return this.releaseDurationSeconds;
        }
    @Override
    public Duration getReleaseDuration()
        {
        return Duration.ofSeconds(
            releaseDurationSeconds
            );
        }

    public IvoaScheduleDurationInstant makePreparingBean()
        {
        boolean valid = false;
        IvoaScheduleDurationInstant bean = new IvoaScheduleDurationInstant(); 
        if (getPrepareStartInstantSeconds() > 0)
            {
            bean.setStart(
                getPrepareStartInstant().toString()
                );
            valid = true;
            }
        if (getPrepareDurationSeconds() > 0)
            {
            bean.setDuration(
                getPrepareDuration().toString()
                );
            valid = true;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    public IvoaScheduleDurationInterval makeAvailableBean()
        {
        boolean valid = false;
        IvoaScheduleDurationInterval bean = new IvoaScheduleDurationInterval();
        if (getAvailableStartInstantSeconds() > 0)
            {
            StringBuffer buffer = new StringBuffer();
            buffer.append(
                getAvailableStartInstant().toString()
                );
            buffer.append(
                "/"
                );
            buffer.append(
                getAvailableStartDuration().toString()
                );
            bean.setStart(
                buffer.toString()
                );
            valid = true ;
            }
        if (getAvailableDurationSeconds() > 0)
            {
            bean.setDuration(
                getAvailableDuration().toString()
                );
            valid = true ;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    public IvoaScheduleDurationInstant makeReleasingBean()
        {
        boolean valid = false;
        IvoaScheduleDurationInstant bean = new IvoaScheduleDurationInstant(); 
        if (getReleaseStartInstantSeconds() > 0)
            {
            bean.setStart(
                getReleaseStartInstant().toString()
                );
            valid = true;
            }
        if (getReleaseDurationSeconds() > 0)
            {
            bean.setDuration(
                getReleaseDuration().toString()
                );
            valid = true;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }
    
    public IvoaExecutionSessionSchedule makeScheduleBean()
        {
        boolean valid = false;
        IvoaExecutionSessionSchedule bean = new IvoaExecutionSessionSchedule(); 

        IvoaScheduleDurationInstant preparing = this.makePreparingBean();
        if (null != preparing)
            {
            bean.setPreparing(
                preparing
                );
            valid = true;
            }

        IvoaScheduleDurationInterval available = this.makeAvailableBean();
        if (null != available)
            {
            bean.setAvailable(
                available
                );
            valid = true;
            }

        IvoaScheduleDurationInstant releasing = this.makeReleasingBean(); 
        if (releasing != null)
            {
            bean.setReleasing(
                this.makeReleasingBean()
                );
            valid = true ;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    
    
    }
