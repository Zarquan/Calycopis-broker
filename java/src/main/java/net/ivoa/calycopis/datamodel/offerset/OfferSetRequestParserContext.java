/**
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.message.MessageSubject;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaOfferSetRequest;

/**
 *
 */
public interface OfferSetRequestParserContext
extends MessageSubject
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
     * Register all the resources in the request.
     * This assigns UUIDs to all resources and
     * pre-registers storage resources in the
     * context lookup maps, so that cross-references
     * between resources can be resolved regardless
     * of validation order.
     *
     */
    public void registerResources();

    /**
     * Flag to indicate that the last validator call was dispatched.
     * A dispatched validator is one that recognised the object type
     * and either accepted or rejected the request.
     *  
     */
    public boolean dispatched();

    /**
     * Set the dispatched flag.
     *  
     */
    public void dispatched(boolean value);

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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final AbstractComputeResourceValidator.Result result);

    /**
     * Find a ComputeValidatorResult.
     * 
     */
    @Deprecated
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final IvoaAbstractComputeResource resource);
    
    /**
     * Find a ComputeValidatorResult.
     * 
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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

    /*
     * Add a DataValidatorResult and StorageValidatorResult pair.
    public void addDataStorageResult(final IvoaAbstractDataResource dataResource, final AbstractStorageResourceValidator.Result storageResult);
     */
    
    /*
     * Add a DataValidatorResult and StorageValidatorResult pair.
    @Deprecated
    public void addDataStorageResult(final AbstractDataResourceValidator.Result dataResult, final AbstractStorageResourceValidator.Result storageResult);
     */

    /*
     * Find a StorageValidator result for a DataValidator result.
    @Deprecated
    public AbstractStorageResourceValidator.Result findDataStorageResult(final AbstractDataResourceValidator.Result dataResult);
     */

    /*
     * Find a StorageValidator result for a IvoaAbstractDataResource.
    @Deprecated
    public AbstractStorageResourceValidator.Result findDataStorageResult(final IvoaAbstractDataResource dataResouce);
     */

    /**
     * List the VolumeValidatorResults.
     * 
     */
    public List<AbstractVolumeMountValidator.Result> getVolumeValidatorResults();
    
    /**
     * Generate a VolumeValidatorResult key.
     *  
     */
    @Deprecated
    public String makeVolumeValidatorResultKey(final AbstractVolumeMountValidator.Result result);

    /**
     * Generate a VolumeMount key.
     *  
     */
    @Deprecated
    public String makeVolumeValidatorResultKey(final IvoaAbstractVolumeMount resource);
    
    /**
     * Add a VolumeValidatorResult.
     * 
     */
    public void addVolumeValidatorResult(final AbstractVolumeMountValidator.Result result);

    /**
     * Find a VolumeValidatorResult.
     * 
     */
    @Deprecated
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final AbstractVolumeMountValidator.Result result);

    /**
     * Find a VolumeValidatorResult.
     * 
     */
    @Deprecated
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final IvoaAbstractVolumeMount resource);
    
    /**
     * Find a VolumeValidatorResult.
     * 
     */
    @Deprecated
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final String key);
    
    /**
     * Get the start interval.
     *
     */
    public Interval getStartInterval();

    /**
     * Set the start interval.
     *
     */
    public void setStartInterval(final Interval interval);

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
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    void addMinCores(long delta);

    /**
     * Add a core count to the running total.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    void addMaxCores(long delta);
    
    /**
     * Get the running total of minimum cores.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public long getTotalMinCores();

    /**
     * Get the running total of maximum cores.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public long getTotalMaxCores();

    /**
     * Get the running total of minimum memory.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public long getTotalMinMemory();

    /**
     * Get the running total of maximum memory.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public long getTotalMaxMemory();

    /**
     * Add a memory count to the running total.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public void addMinMemory(long delta);

    /**
     * Add a memory count to the running total.
     * TODO Do we need this if we only have one compute resource ?
     * 
     */
    public void addMaxMemory(long delta);

    /**
     * Calculate the staging and preparing totals.
     */
    public Long calculateTotalPrepareTime();

    /**
     * Get the total staging time.
     * 
     */
    public Long getTotalStagingTime();

    /**
     * Get the total preparation time.
     * 
     */
    public Long getTotalPrepareTime();
    
    }
