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

package net.ivoa.calycopis.datamodel.session.scheduled;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSession;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaScheduleStartDurationInstant;
import net.ivoa.calycopis.openapi.model.IvoaScheduleStartDurationInterval;
import net.ivoa.calycopis.openapi.model.IvoaScheduledExecutionSchedule;
import net.ivoa.calycopis.openapi.model.IvoaScheduledExecutionSession;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSession;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
@Entity
@Table(name = "scheduledexecutionsession")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ScheduledExecutionSessionEntity
extends SimpleExecutionSessionEntity
implements ScheduledExecutionSession
    {

    @Override
    public URI getKind()
        {
        return ScheduledExecutionSession.TYPE_DISCRIMINATOR;
        }

    /**
     * 
     */
    public ScheduledExecutionSessionEntity()
        {
        super();
        }

    /**
     * 
     */
    public ScheduledExecutionSessionEntity(
        final OfferSetEntity offerset,
        final OfferSetRequestParserContext context,
        final ResourceOffer offerblock
        ){
        super(offerset, context, offerblock);

        // TODO factor in the compute prepare time.
        // OfferBlock needs to have separate prepare and available times.
        // Actually - the offerblock relates to the compute resource.
        this.availableStartInstantSeconds = offerblock.getStartTime().getEpochSecond();
        this.availableDurationSeconds     = offerblock.getDuration().toSeconds();

        this.prepareDurationSeconds       = context.getTotalPrepareTime();
        this.prepareStartInstantSeconds   = this.availableStartInstantSeconds - this.prepareDurationSeconds;

        //
        // Hard coded 10s release duration.
        // Start releasing as soon as availability ends.
        // Release duration should depends on the components.
        this.releaseDurationSeconds = 10L ; 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds + 5L ;         

        }
    
    public void init(final IvoaScheduledExecutionSchedule schedule)
        {
        if (schedule != null)
            {
            IvoaScheduleStartDurationInstant preparing = schedule.getPreparing();
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

    public IvoaScheduleStartDurationInstant makePreparingBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInstant bean = new IvoaScheduleStartDurationInstant(); 
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

    public IvoaScheduleStartDurationInterval makeAvailableBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInterval bean = new IvoaScheduleStartDurationInterval();
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

    public IvoaScheduleStartDurationInstant makeReleasingBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInstant bean = new IvoaScheduleStartDurationInstant(); 
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
    
    public IvoaScheduledExecutionSchedule makeScheduleBean()
        {
        boolean valid = false;
        IvoaScheduledExecutionSchedule bean = new IvoaScheduledExecutionSchedule(); 

        IvoaScheduleStartDurationInstant preparing = this.makePreparingBean();
        if (null != preparing)
            {
            bean.setPreparing(
                preparing
                );
            valid = true;
            }

        IvoaScheduleStartDurationInterval available = this.makeAvailableBean();
        if (null != available)
            {
            bean.setAvailable(
                available
                );
            valid = true;
            }

        IvoaScheduleStartDurationInstant releasing = this.makeReleasingBean(); 
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

    public IvoaScheduledExecutionSession makeBean(final URIBuilder uribuilder)
        {
        return this.fillBean(
            uribuilder,
            new IvoaScheduledExecutionSession().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }

    public IvoaScheduledExecutionSession fillBean(
        final URIBuilder uribuilder,
        final IvoaScheduledExecutionSession bean
        ){
        super.fillBean(
            uribuilder,
            bean
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }
    }
