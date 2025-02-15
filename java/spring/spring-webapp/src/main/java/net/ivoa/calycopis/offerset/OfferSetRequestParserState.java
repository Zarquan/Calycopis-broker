/**
 *
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.message.MessageSubject;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 *
 */
public interface OfferSetRequestParserState
    {
    /**
     * Get the OfferSet request.
     *  
     */
    public IvoaOfferSetRequest getOfferSetRequest();

    /**
     * Get the OfferSet entity we are creating.
     *  
     */
    public OfferSetEntity getOfferSetEntity();

    /**
     * Flag to indicate that the parser hasn't encountered any errors.
     *  
     */
    public boolean valid();

    /**
     * Set the valid flag.
     *  
     */
    public void valid(boolean value);

    /**
     * Get the requested executable.
     * 
     */
    public IvoaAbstractExecutable getRequesedtExecutable();

    /**
     * Set the requested executable.
     * 
     */
    public void setRequestedExecutable(final IvoaAbstractExecutable executable);
    
    /**
     * Get a List of start intervals.
     *
    public List<Interval> getStartIntervals();
     */

    /**
     * Get the requested start Duration.
     *
    public Duration getDuration();
     */

    public long getMinCores();
    public long getMaxCores();

    public long getMinMemory();
    public long getMaxMemory();


/*
 *

    public List<SimpleDataResourceEntity> getDataResourceList();
    public SimpleDataResourceEntity findDataResource(final String key);

    public List<SimpleComputeResourceEntity> getComputeResourceList();
    public SimpleComputeResourceEntity findComputeResource(final String key);

    public AbstractExecutable getExecutable();

    // This is a total over all the compute resources.
    public long getMinCores();
    //public int getMaxCores();
    public void addMinCores(long delta);
    //public void addMaxCores(int delta);

    // This is a total over all the compute resources.
    public long getMinMemory();
    //public int getMaxMemory();
    public void addMinMemory(long delta);
    //public void addMaxMemory(int delta);

    public interface ScheduleItem
        {
        public Interval getStartTime();
        public Duration getDuration();
        }

    public ScheduleItem getPreparationTime();
    public void setPreparationTime(final Interval starttime, final Duration duration);

    public ScheduleItem getExecutionTime();
    public void setExecutionTime(final Interval starttime, final Duration duration);

 *
 */

    }
