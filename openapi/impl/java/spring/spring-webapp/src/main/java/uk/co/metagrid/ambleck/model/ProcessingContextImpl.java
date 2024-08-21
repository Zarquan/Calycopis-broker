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
 */
package uk.co.metagrid.ambleck.model;

import java.util.UUID;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import org.threeten.extra.Interval;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * A class to hold context during processing.
 *
 */
public class ProcessingContextImpl implements ProcessingContext
    {

    public ProcessingContextImpl(final String baseurl, final OfferSetRequest request, final OfferSetResponse response)
        {
        this.baseurl  = baseurl ;
        this.request  = request ;
        this.response = response ;
        }

    private boolean valid = true ;
    public boolean valid()
        {
        return this.valid;
        }
    public void valid(boolean value)
        {
        this.valid = value ;
        }
    public void fail()
        {
        this.valid = false ;
        }

    private final OfferSetRequest request;
    public OfferSetRequest request()
        {
        return this.request;
        }

    private final OfferSetResponse response;
    public OfferSetResponse response()
        {
        return this.response;
        }

    private final String baseurl;
    public String baseurl()
        {
        return this.baseurl;
        }

    // Messages
    public void addMessage(final MessageItem message)
        {
        this.response.addMessagesItem(
            message
            );
        }

    // Data resources
    private Map<String, AbstractDataResource> datamap = new HashMap<String, AbstractDataResource>();
    private List<AbstractDataResource> datalist = new ArrayList<AbstractDataResource>();

    public void addDataResource(final AbstractDataResource data)
        {
        UUID   uuid = data.getUuid() ;
        String name = data.getName() ;
        String type = data.getType() ;

        // Check for anonymous resource and set the UUID.
        if ((uuid == null) && (name == null))
            {
            uuid = UuidCreator.getTimeBased();
            data.setUuid(
                uuid
                );
            }
        // Add the resource UUID to our Map.
        if (uuid != null)
            {
            datamap.put(
                uuid.toString(),
                data
                );
            }
        // Add the resource name to our Map.
        if (name != null)
            {
            datamap.put(
                name,
                data
                );
            }
        // Add the resource to our List.
        datalist.add(
            data
            );
        }

    public List<AbstractDataResource> getDataResourceList()
        {
        return this.datalist;
        }

    public AbstractDataResource findDataResource(final String key)
        {
        return this.datamap.get(key);
        }

    // Compute resources.
    private Map<String, AbstractComputeResource> compmap = new HashMap<String, AbstractComputeResource>();
    private List<AbstractComputeResource> complist = new ArrayList<AbstractComputeResource>();

    public void addComputeResource(final AbstractComputeResource comp)
        {
        UUID   uuid = comp.getUuid() ;
        String name = comp.getName() ;
        String type = comp.getType() ;

        // Check for anonymous resource and set the UUID.
        if ((uuid == null) && (name == null))
            {
            uuid = UuidCreator.getTimeBased();
            comp.setUuid(
                uuid
                );
            }
        // Add the resource UUID to our Map.
        if (uuid != null)
            {
            compmap.put(
                uuid.toString(),
                comp
                );
            }
        // Add the resource name to our Map.
        if (name != null)
            {
            compmap.put(
                name,
                comp
                );
            }
        // Add the resource to our List.
        complist.add(
            comp
            );
        }

    public List<AbstractComputeResource> getComputeResourceList()
        {
        return this.complist;
        }

    public AbstractComputeResource findComputeResource(final String key)
        {
        return this.compmap.get(key);
        }

    // Executable
    private AbstractExecutable executable ;
    public AbstractExecutable getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final AbstractExecutable executable)
        {
        this.executable = executable;
        }

    public int DEFAULT_CORES = 1 ;
    private int mincores = 0 ;
    public int getMinCores()
        {
        if (this.mincores != 0)
            {
            return this.mincores ;
            }
        else {
            return DEFAULT_CORES ;
            }
        }
    public void addMinCores(int delta)
        {
        this.mincores += delta ;
        }

    private int maxcores = 0 ;
    public int getMaxCores()
        {
        if (this.maxcores != 0)
            {
            return this.maxcores ;
            }
        else {
            return this.getMinCores();
            }
        }
    public void addMaxCores(int delta)
        {
        this.maxcores += delta ;
        }

    public int DEFAULT_MEMORY = 1 ;
    private int minmemory = 0 ;
    public int getMinMemory()
        {
        if (this.minmemory != 0)
            {
            return this.minmemory ;
            }
        else {
            return DEFAULT_MEMORY ;
            }
        }
    public void addMinMemory(int delta)
        {
        this.minmemory += delta ;
        }

    private int maxmemory = 0 ;
    public int getMaxMemory()
        {
        if (this.maxmemory != 0)
            {
            return this.maxmemory ;
            }
        else {
            return this.getMinMemory();
            }
        }
    public void addMaxMemory(int delta)
        {
        this.maxmemory += delta ;
        }
/*
 *
    public long DEFAULT_DURATION = Duration.ofMinutes(5).getSeconds();
    private long duration = 0 ;
    public long getDuration()
        {
        if (this.duration != 0)
            {
            return this.duration ;
            }
        else {
            return DEFAULT_DURATION ;
            }
        }
    public void setDuration(long value)
        {
        this.duration = value ;
        }
 *
 */

    public class ScheduleItemImpl implements ScheduleItem
        {
        public ScheduleItemImpl(final Interval starttime, final Duration minduration, final Duration maxduration)
            {
            this.starttime = starttime ;
            this.minduration = minduration;
            this.maxduration = maxduration;
            }
        private Interval starttime ;
        public Interval getStartTime()
            {
            return this.starttime;
            }
        private Duration minduration;
        public Duration getMinDuration()
            {
            return this.minduration;
            }
        private Duration maxduration;
        public Duration getMaxDuration()
            {
            return this.maxduration;
            }
        }

    private List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();
    public List<ScheduleItem> getScheduleItems()
        {
        return this.scheduleItems ;
        }
    public void addScheduleItem(final Interval starttime, final Duration minduration, final Duration maxduration)
        {
        scheduleItems.add(
            new ScheduleItemImpl(
                starttime,
                minduration,
                maxduration
                )
            );
        }
    }


