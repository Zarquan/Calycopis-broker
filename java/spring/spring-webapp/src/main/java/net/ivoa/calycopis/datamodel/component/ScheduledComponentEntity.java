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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.openapi.model.IvoaObservedScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaObservedScheduleItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleInstant;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleInterval;

/**
 * 
 */
@Slf4j
@Entity
@Table(name = "scheduledcomponents")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ScheduledComponentEntity
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
    public ScheduledComponentEntity(final IvoaComponentSchedule schedule, final String name)
        {
        super(
            name
            );
        if (schedule != null)
            {
            IvoaOfferedScheduleBlock offered = schedule.getOffered();
            if (null != offered)
                {
                IvoaOfferedScheduleInstant preparing = offered.getPreparing();
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
            /*
            IvoaObservedScheduleBlock observed = schedule.getObserved();
            */
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

    public IvoaOfferedScheduleInstant makeOfferedPreparingBean()
        {
        boolean valid = false;
        IvoaOfferedScheduleInstant bean = new IvoaOfferedScheduleInstant(); 
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

    public IvoaOfferedScheduleInterval makeOfferedAvailableBean()
        {
        boolean valid = false;
        IvoaOfferedScheduleInterval bean = new IvoaOfferedScheduleInterval();
        if (getAvailableStartInstantSeconds() > 0)
            {
            StringBuffer buffer = new StringBuffer();
            buffer.append(
                getAvailableStartInstant().toString()
                );
            if (getAvailableStartDurationSeconds() > 0)
                {
                buffer.append(
                    "/"
                    );
                buffer.append(
                    getAvailableStartDuration().toString()
                    );
                }
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

    public IvoaOfferedScheduleInstant makeOfferedReleasingBean()
        {
        boolean valid = false;
        IvoaOfferedScheduleInstant bean = new IvoaOfferedScheduleInstant(); 
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
    
    public IvoaOfferedScheduleBlock makeOfferedScheduleBean()
        {
        boolean valid = false;
        IvoaOfferedScheduleBlock bean = new IvoaOfferedScheduleBlock(); 

        IvoaOfferedScheduleInstant preparing = this.makeOfferedPreparingBean();
        if (null != preparing)
            {
            bean.setPreparing(
                preparing
                );
            valid = true;
            }

        IvoaOfferedScheduleInterval available = this.makeOfferedAvailableBean();
        if (null != available)
            {
            bean.setAvailable(
                available
                );
            valid = true;
            }

        IvoaOfferedScheduleInstant releasing = this.makeOfferedReleasingBean(); 
        if (releasing != null)
            {
            bean.setReleasing(
                this.makeOfferedReleasingBean()
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

    public IvoaObservedScheduleItem makeObservedPreparingBean()
        {
        IvoaObservedScheduleItem bean = new IvoaObservedScheduleItem();
        return bean;
        }

    public IvoaObservedScheduleItem makeObservedAvailableBean()
        {
        IvoaObservedScheduleItem bean = new IvoaObservedScheduleItem(); 
        return bean;
        }

    public IvoaObservedScheduleItem makeObservedReleasingBean()
        {
        IvoaObservedScheduleItem bean = new IvoaObservedScheduleItem(); 
        return bean;
        }
    
    public IvoaObservedScheduleBlock makeObservedScheduleBean()
        {
        IvoaObservedScheduleBlock bean = new IvoaObservedScheduleBlock(); 
        bean.setPreparing(
            this.makeObservedPreparingBean()
            );
        bean.setAvailable(
            this.makeObservedAvailableBean()
            );
        bean.setReleasing(
            this.makeObservedReleasingBean()
            );
        return bean;
        }
    
    public IvoaComponentSchedule makeScheduleBean()
        {
        IvoaComponentSchedule bean = new IvoaComponentSchedule(); 
        bean.setOffered(
            this.makeOfferedScheduleBean()
            );
        /*
         * Don't include this if it is empty.
        bean.setObserved(
            this.makeObservedScheduleBean()
            );
         */
        return bean;
        }
    }
