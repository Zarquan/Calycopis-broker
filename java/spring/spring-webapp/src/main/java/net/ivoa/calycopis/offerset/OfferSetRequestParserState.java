/**
 *
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;

import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 *
 */
public interface OfferSetRequestParserState
    {
    /**
     * Get the original OfferSet request.
     *  
     */
    public IvoaOfferSetRequest getOriginalOfferSetRequest();

    /**
     * Get the validated OfferSet request.
     *  
     */
    public IvoaOfferSetRequest getValidatedOfferSetRequest();
    
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
     * Get the validated executable.
     * 
     */
    public IvoaAbstractExecutable getExecutable();

    /**
     * Set the validated executable.
     * 
     */
    public void setExecutable(final IvoaAbstractExecutable executable);

    /**
     * Add a validated ComputeResource.
     * 
     */
    public void addComputeResource(final IvoaAbstractComputeResource resource);

    /**
     * Find a validated ComputeResource.
     * 
     */
    public IvoaAbstractComputeResource findComputeResource(final String key);

    /**
     * List the validated ComputeResources.
     * 
     */
    public List<IvoaAbstractComputeResource> getComputeResources();

    /**
     * Add a validated StorageResource.
     * 
     */
    public void addStorageResource(final IvoaAbstractStorageResource resource);

    /**
     * Find a validated StorageResource.
     * 
     */
    public IvoaAbstractStorageResource findStorageResource(final String key);

    /**
     * List the validated StorageResources.
     * 
     */
    public List<IvoaAbstractStorageResource> getStorageResources();
    
    /**
     * Add a validated DataResource.
     * 
     */
    public void addDataResource(final IvoaAbstractDataResource resource);

    /**
     * Find a validated DataResource.
     * 
     */
    public IvoaAbstractDataResource findDataResource(final String key);

    /**
     * List the validated DataResources.
     * 
     */
    public List<IvoaAbstractDataResource> getDataResources();

    /*
     * 
    public interface DataStorageLink
        {
        public IvoaAbstractDataResource getDataResource();
        public IvoaAbstractStorageResource getStorageResource();
        }

    public DataStorageLink findDataStorageLink(final String key);
     * 
     */

    /**
     * Get a List of start intervals.
     *
     */
    public List<Interval> getStartIntervals();

    /**
     * Add a start interval.
     *
     */
    public void addStartInterval(final Interval interval);

    /**
     * Get the requested start Duration.
     *
     */
    public Duration getDuration();

    /**
     * Set the requested start Duration.
     *
     */
    public void setDuration(final Duration duration);
    

    /**
     * Add a core count to the running total.
     * 
     */
    void addMinCores(long delta);

    /**
     * Add a core count to the running total.
     * 
     */
    void addMaxCores(long delta);

    
    /**
     * Get the running total of minimum cores.
     * 
     */
    public long getTotalMinCores();

    /**
     * Get the running total of maximum cores.
     * 
     */
    public long getTotalMaxCores();

    /**
     * Get the running total of minimum memory.
     * 
     */
    public long getTotalMinMemory();

    /**
     * Get the running total of maximum memory.
     * 
     */
    public long getTotalMaxMemory();

    /**
     * Add a memory count to the running total.
     * 
     */
    void addMinMemory(long delta);

    /**
     * Add a memory count to the running total.
     * 
     */
    void addMaxMemory(long delta);


/*
 *


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
