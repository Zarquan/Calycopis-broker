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

package net.ivoa.calycopis.datamodel.storage;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleDurationInstant;

/**
 * 
 */
@Slf4j
public abstract class AbstractStorageResourceValidatorImpl
implements AbstractStorageResourceValidator
    {

    public AbstractStorageResourceValidatorImpl()
        {
        super();
        }

    /**
     * 
     *
     */
    public boolean setPrepareDuration(
        final IvoaAbstractStorageResource validated,
        final Long seconds
        ){
        if (null != seconds)
            {
            IvoaComponentSchedule schedule = validated.getSchedule();
            if (null == schedule)
                {
                schedule = new IvoaComponentSchedule(); 
                validated.setSchedule(
                    schedule
                    );
                }
            
            IvoaOfferedScheduleBlock offered = schedule.getOffered();
            if (null == offered)
                {
                offered = new IvoaOfferedScheduleBlock ();
                schedule.setOffered(
                    offered
                    );   
                }
    
            IvoaOfferedScheduleDurationInstant preparing = offered.getPreparing();
            if (null == preparing)
                {
                preparing = new IvoaOfferedScheduleDurationInstant();
                offered.setPreparing(
                    preparing
                    );
                }
    
            String start = preparing.getStart();
            if (null != start)
                {
                log.error("Existing preparing start [{}]", start);
                return false ;
                }
    
            String duration = preparing.getDuration();
            if (null != duration)
                {
                log.error("Existing preparing duration [{}]", duration);
                return false ;
                }
    
            // Saving this as a String sucks a bit, but we are using the generated bean class.
            // TODO If we create a new class for the validated object that wraps or extends the generated bean

    // TODO Use the extended bean
            
            preparing.setDuration(
                Duration.ofSeconds(
                    seconds
                    ).toString()
                );
            
            return true ;
            }
        else {
            log.error("Null prepare duration");
            return false;
            }
        }
    }
