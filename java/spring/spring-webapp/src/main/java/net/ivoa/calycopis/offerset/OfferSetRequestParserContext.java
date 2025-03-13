/**
 *
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.storage.AbstractStorageResourceValidator;

/**
 *
 */
public interface OfferSetRequestParserContext
    {
    /**
     * Get a reference to the parent parser.
     *  
     */
    public OfferSetRequestParser getParser();
    
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
    public AbstractExecutableValidator.Result getExecutableResult();

    /**
     * Set the validated executable.
     * 
     */
    public void setExecutableResult(final AbstractExecutableValidator.Result result);

    /**
     * List the DataValidatorResults.
     * 
     */
    public List<AbstractDataResourceValidator.Result> getDataResourceValidatorResults();

    /**
     * Generate a DataValidatorResult key.
     *  
     */
    public String makeDataValidatorResultKey(final AbstractDataResourceValidator.Result result);

    /**
     * Generate a DataResource key.
     *  
     */
    public String makeDataValidatorResultKey(final IvoaAbstractDataResource resource);

    /**
     * Add a DataValidatorResult.
     * 
     */
    public void addDataValidatorResult(final AbstractDataResourceValidator.Result result);

    /**
     * Find a DataValidatorResult.
     * 
     */
    public AbstractDataResourceValidator.Result findDataValidatorResult(final AbstractDataResourceValidator.Result result);

    /**
     * Find a DataValidatorResult.
     * 
     */
    public AbstractDataResourceValidator.Result findDataValidatorResult(final IvoaAbstractDataResource resource);
    
    /**
     * Find a DataValidatorResult.
     * 
     */
    public AbstractDataResourceValidator.Result findDataValidatorResult(final String key);

    /**
     * List the ComputeValidatorResults.
     * 
     */
    public List<AbstractComputeResourceValidator.Result> getComputeValidatorResults();

    /**
     * Generate a ComputeValidatorResult key.
     *  
     */
    public String makeComputeValidatorResultKey(final AbstractComputeResourceValidator.Result result);

    /**
     * Generate a ComputeResource key.
     *  
     */
    public String makeComputeValidatorResultKey(final IvoaAbstractComputeResource resource);
    
    /**
     * Add a ComputeValidatorResult.
     * 
     */
    public void addComputeValidatorResult(final AbstractComputeResourceValidator.Result result);

    /**
     * Find a ComputeValidatorResult.
     * 
     */
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final AbstractComputeResourceValidator.Result result);

    /**
     * Find a ComputeValidatorResult.
     * 
     */
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final IvoaAbstractComputeResource resource);
    
    /**
     * Find a ComputeValidatorResult.
     * 
     */
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final String key);

    /**
     * List the StorageValidatorResults.
     * 
     */
    public List<AbstractStorageResourceValidator.Result> getStorageValidatorResults();
    
    /**
     * Generate a StorageValidatorResult key.
     *  
     */
    public String makeStorageValidatorResultKey(final AbstractStorageResourceValidator.Result result);

    /**
     * Generate a StorageResource key.
     *  
     */
    public String makeStorageValidatorResultKey(final IvoaAbstractStorageResource resource);
    
    /**
     * Add a StorageValidatorResult.
     * 
     */
    public void addStorageValidatorResult(final AbstractStorageResourceValidator.Result result);

    /**
     * Find a StorageValidatorResult.
     * 
     */
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final AbstractStorageResourceValidator.Result result);

    /**
     * Find a StorageValidatorResult.
     * 
     */
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final IvoaAbstractStorageResource resource);
    
    /**
     * Find a StorageValidatorResult.
     * 
     */
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final String key);

    /**
     * Add a DataValidatorResult and StorageValidatorResult pair.
     * 
     */
    public void addDataStorageResult(final AbstractDataResourceValidator.Result dataResult, final AbstractStorageResourceValidator.Result storageResult);

    /**
     * Find a StorageValidator result for a DataValidator result.
     * 
     */
    public AbstractStorageResourceValidator.Result findDataStorageResult(final AbstractDataResourceValidator.Result dataResult);

    /**
     * Find a StorageValidator result for a IvoaAbstractDataResource.
     * 
     */
    public AbstractStorageResourceValidator.Result findDataStorageResult(final IvoaAbstractDataResource dataResouce);
    
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
    public Duration getExecutionDuration();

    /**
     * Set the requested start Duration.
     *
     */
    public void setExecutionDuration(final Duration duration);
    

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
