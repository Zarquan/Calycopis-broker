/**
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.resource.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 *
 */
@Slf4j
public class OfferSetRequestParserContextImpl
extends AbstractValidatorImpl
    implements OfferSetRequestParserContext
    {

    /**
     * Public constructor.
     * 
     */
    public OfferSetRequestParserContextImpl(
        final OfferSetRequestParser parser,
        final IvoaOfferSetRequest offersetRequest,
        final OfferSetEntity offersetEntity
        ){
        this.parser = parser;
        this.originalRequest  = offersetRequest;
        this.validatedRequest = new IvoaOfferSetRequest();
        this.offersetEntity   = offersetEntity;
        }

    /**
     * Get a reference to the parent parser.
     *  
     */
    private final OfferSetRequestParser parser;
    public OfferSetRequestParser getParser()
        {
        return this.parser;
        }

    private final IvoaOfferSetRequest originalRequest;
    @Override
    public IvoaOfferSetRequest getOriginalOfferSetRequest()
        {
        return this.originalRequest;
        }

    private IvoaOfferSetRequest validatedRequest;
    @Override
    public IvoaOfferSetRequest getValidatedOfferSetRequest()
        {
        return this.validatedRequest;
        }

    private final OfferSetEntity offersetEntity;
    @Override
    public OfferSetEntity getOfferSetEntity()
        {
        return this.offersetEntity;
        }

    private boolean valid = true ;
    @Override
    public boolean valid()
        {
        return this.valid;
        }
    @Override
    public void valid(boolean value)
        {
        this.valid = value;
        }
    public void fail()
        {
        this.valid = false;
        }

    private AbstractExecutableValidator.Result executable;
    @Override
    public AbstractExecutableValidator.Result getExecutableResult()
        {
        return this.executable;
        }
    public void setExecutableResult(final AbstractExecutableValidator.Result executable)
        {
        this.executable = executable;
        }
    
    /**
     * Our List of DataValidator results.
     * 
     */
    private List<AbstractDataResourceValidator.Result> dataValidatorResultList = new ArrayList<AbstractDataResourceValidator.Result> ();

    @Override
    public List<AbstractDataResourceValidator.Result> getDataResourceValidatorResults()
        {
        return dataValidatorResultList;
        }

    /**
     * Our Map of DataValidator results.
     * 
     */
    private Map<String, AbstractDataResourceValidator.Result> dataValidatorResultMap = new HashMap<String, AbstractDataResourceValidator.Result>();

    @Override
    public String makeDataValidatorResultKey(final AbstractDataResourceValidator.Result result)
        {
        log.debug("makeDataValidatorResultKey(DataResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeDataValidatorResultKey(
            result.getObject()
            );
        }
    
    @Override
    public String makeDataValidatorResultKey(final IvoaAbstractDataResource resource)
        {
        log.debug("makeDataValidatorResultKey(IvoaAbstractDataResource)");
        log.debug("Resource [[{}][{}]]", resource.getUuid(), resource.getName());
        String key = null ;
        if (resource != null)
            {
            UUID uuid = resource.getUuid();
            if (uuid != null)
                {
                key = uuid.toString();
                }
            else {
                key = resource.getName();
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addDataValidatorResult(final AbstractDataResourceValidator.Result result)
        {
        log.debug("addDataValidatorResult(DataResourceValidator.Result)");
        log.debug("Result [{}]", result);
        dataValidatorResultList.add(
            result
            );
        dataValidatorResultMap.put(
            makeDataValidatorResultKey(
                result
                ),
            result
            );
        }
    
    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final AbstractDataResourceValidator.Result result)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final IvoaAbstractDataResource resource)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final String key)
        {
        log.debug("findDataValidatorResult(String)");
        log.debug("Key [{}]", key);
        return dataValidatorResultMap.get(key);
        }

    /**
     * Our List of ComputeValidator results.
     * 
     */
    private List<AbstractComputeResourceValidator.Result> compValidatorResultList = new ArrayList<AbstractComputeResourceValidator.Result>();

    @Override
    public List<AbstractComputeResourceValidator.Result> getComputeValidatorResults()
        {
        return compValidatorResultList;
        }

    /**
     * Our Map of ComputeValidator results.
     * 
     */
    private Map<String, AbstractComputeResourceValidator.Result> compValidatorResultMap = new HashMap<String, AbstractComputeResourceValidator.Result>();

    @Override
    public String makeComputeValidatorResultKey(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("makeComputeValidatorResultKey(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeComputeValidatorResultKey(
            result.getObject()
            );
        }
    
    @Override
    public String makeComputeValidatorResultKey(final IvoaAbstractComputeResource resource)
        {
        log.debug("makeComputeValidatorResultKey(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            UUID uuid = resource .getUuid();
            if (uuid != null)
                {
                key = uuid.toString();
                }
            else {
                key = resource .getName();
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addComputeValidatorResult(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("addComputeValidatorResult(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        compValidatorResultList.add(
            result
            );
        compValidatorResultMap.put(
            makeComputeValidatorResultKey(
                result
                ),
            result
            );
        }
    
    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("findComputeValidatorResult(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return findComputeValidatorResult(
            makeComputeValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final IvoaAbstractComputeResource resource)
        {
        log.debug("findComputeValidatorResult(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", resource);
        return findComputeValidatorResult(
            makeComputeValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(String key)
        {
        log.debug("findComputeValidatorResult(String)");
        log.debug("Key [{}]", key);
        return compValidatorResultMap.get(key);
        }

    /**
     * Our List of StorageValidator results.
     * 
     */
    private List<AbstractStorageResourceValidator.Result> storageValidatorResultList = new ArrayList<AbstractStorageResourceValidator.Result>();

    @Override
    public List<AbstractStorageResourceValidator.Result> getStorageValidatorResults()
        {
        return storageValidatorResultList;
        }
    
    /**
     * Our Map of StorageValidator results.
     * 
     */
    private Map<String, AbstractStorageResourceValidator.Result> storageValidatorResultMap = new HashMap<String, AbstractStorageResourceValidator.Result>();

    @Override
    public String makeStorageValidatorResultKey(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("makeStorageValidatorResultKey(StorageResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeStorageValidatorResultKey(
            result.getObject()
            );
        }
    
    @Override
    public String makeStorageValidatorResultKey(final IvoaAbstractStorageResource resource)
        {
        log.debug("makeStorageValidatorResultKey(IvoaAbstractStorageResource)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            UUID uuid = resource.getUuid();
            if (uuid != null)
                {
                key = uuid.toString();
                }
            else {
                key = resource.getName();
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }
    
    @Override
    public void addStorageValidatorResult(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("addStorageValidatorResult(String)");
        log.debug("Result [{}]", result);
        storageValidatorResultList.add(
            result
            );
        storageValidatorResultMap.put(
            makeStorageValidatorResultKey(
                result
                ),
            result
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("findStorageValidatorResult(StorageResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return findStorageValidatorResult(
            makeStorageValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final IvoaAbstractStorageResource resource)
        {
        log.debug("findStorageValidatorResult(IvoaAbstractStorageResource)");
        log.debug("Resource [{}]", resource);
        return findStorageValidatorResult(
            makeStorageValidatorResultKey(
                resource
                )
            );
        }
    
    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final String key)
        {
        log.debug("findStorageValidatorResult(String)");
        log.debug("Key [{}]", key);
        return storageValidatorResultMap.get(key);
        }

    /**
     * Our List of VolumeMountValidator results.
     * 
     */
    private List<AbstractVolumeMountValidator.Result> volumeValidatorResultList = new ArrayList<AbstractVolumeMountValidator.Result>();

    @Override
    public List<AbstractVolumeMountValidator.Result> getVolumeValidatorResults()
        {
        return volumeValidatorResultList;
        }

    /**
     * Our Map of VolumeMountValidator results.
     * 
     */
    private Map<String, AbstractVolumeMountValidator.Result> volumeValidatorResultMap = new HashMap<String, AbstractVolumeMountValidator.Result>();

    @Override
    public String makeVolumeValidatorResultKey(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("makeVolumeValidatorResultKey(VolumeMountValidator.Result)");
        log.debug("Result [{}]", result);
        return makeVolumeValidatorResultKey(
            result.getObject()
            );
        }
    
    @Override
    public String makeVolumeValidatorResultKey(final IvoaAbstractVolumeMount resource)
        {
        log.debug("makeVolumeValidatorResultKey(IvoaAbstractVolumeMount)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            UUID uuid = resource.getUuid();
            if (uuid != null)
                {
                key = uuid.toString();
                }
            else {
                key = resource.getName();
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }
    
    @Override
    public void addVolumeValidatorResult(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("addVolumeValidatorResult(String)");
        log.debug("Result [{}]", result);
        volumeValidatorResultList.add(
            result
            );
        volumeValidatorResultMap.put(
            makeVolumeValidatorResultKey(
                result
                ),
            result
            );
        }

    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("findVolumeValidatorResult(VolumeMountValidator.Result)");
        log.debug("Result [{}]", result);
        return findVolumeValidatorResult(
            makeVolumeValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final IvoaAbstractVolumeMount resource)
        {
        log.debug("findVolumeValidatorResult(IvoaAbstractVolumeMount)");
        log.debug("Resource [{}]", resource);
        return findVolumeValidatorResult(
            makeVolumeValidatorResultKey(
                resource
                )
            );
        }
    
    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final String key)
        {
        log.debug("findVolumeValidatorResult(String)");
        log.debug("Key [{}]", key);
        return volumeValidatorResultMap.get(key);
        }
    
    /**
     * A Map linking DataValidator results to StorageValidator results.
     * 
     */
    private Map<String, AbstractStorageResourceValidator.Result> dataStorageMap = new HashMap<String, AbstractStorageResourceValidator.Result>();

    @Override
    public void addDataStorageResult(
        final IvoaAbstractDataResource dataResource,
        final AbstractStorageResourceValidator.Result storageResult
        ){
        log.debug("addDataStorageResult(IvoaAbstractDataResource, StorageResourceValidator.Result)");
        log.debug("IvoaAbstractDataResource [{}]", dataResource);
        log.debug("StorageResult [{}]", storageResult);
        // TODO
        // Check for a duplicate already in the map ?
        dataStorageMap.put(
            makeDataValidatorResultKey(
                dataResource
                ),
            storageResult
            );
        }

    @Override
    public void addDataStorageResult(
        final AbstractDataResourceValidator.Result dataResult,
        final AbstractStorageResourceValidator.Result storageResult
        ){
        log.debug("addDataStorageResult(DataResourceValidator.Result, StorageResourceValidator.Result)");
        log.debug("DataResult [{}]", dataResult);
        log.debug("StorageResult [{}]", storageResult);
        // TODO
        // Check for a duplicate already in the map ?
        dataStorageMap.put(
            makeDataValidatorResultKey(
                dataResult
                ),
            storageResult
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findDataStorageResult(final AbstractDataResourceValidator.Result dataResult)
        {
        log.debug("findDataStorageResult(DataResourceValidator.Result)");
        log.debug("Result [{}]", dataResult);
        return dataStorageMap.get(
            makeDataValidatorResultKey(
                dataResult
                )
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findDataStorageResult(final IvoaAbstractDataResource dataResouce)
        {
        log.debug("findDataStorageResult(IvoaAbstractDataResource)");
        log.debug("Resouce [{}]", dataResouce);
        return dataStorageMap.get(
            makeDataValidatorResultKey(
                dataResouce
                )
            );
        }
    
    /**
     * Our List of requested start Intervals.
     *
     */
    private List<Interval> startIntervals = new ArrayList<Interval>();
    public List<Interval> getStartIntervals()
        {
        return this.startIntervals;
        }
    @Override
    public void addStartInterval(Interval interval)
        {
        this.startIntervals.add(interval);
        }
    
    /**
     * The requested duration.
     *
     */
    private Duration executionDuration = null;
    @Override
    public Duration getExecutionDuration()
        {
        return this.executionDuration;
        }
    @Override
    public void setExecutionDuration(Duration duration)
        {
        this.executionDuration = duration;
        }

    private long totalMinCores;
    @Override
    public long getTotalMinCores()
        {
        return this.totalMinCores;
        }
    @Override
    public void addMinCores(long delta)
        {
        this.totalMinCores += delta;
        }

    private long totalMaxCores;
    @Override
    public long getTotalMaxCores()
        {
        return this.totalMaxCores;
        }
    @Override
    public void addMaxCores(long delta)
        {
        this.totalMaxCores += delta;
        }

    private long totalMinMemory;
    @Override
    public long getTotalMinMemory()
        {
        return this.totalMinMemory;
        }
    @Override
    public void addMinMemory(long delta)
        {
        this.totalMinMemory += delta;
        }

    private long totalMaxMemory;
    @Override
    public long getTotalMaxMemory()
        {
        return this.totalMaxMemory;
        }
    @Override
    public void addMaxMemory(long delta)
        {
        this.totalMaxMemory += delta;
        }

    private Duration maxPreparationDuration = Duration.ZERO;
    
    @Override
    public void addPreparationDuration(final Duration duration)
        {
        log.debug("Adding prep duration [{}]", duration);
        if (null != duration)
            {
            if (duration.compareTo(this.maxPreparationDuration) > 0)
                {
                this.maxPreparationDuration = duration;
                }
            }
        }

    @Override
    public Duration getMaxPreparationDuration()
        {
        return maxPreparationDuration ;
        }
    }
