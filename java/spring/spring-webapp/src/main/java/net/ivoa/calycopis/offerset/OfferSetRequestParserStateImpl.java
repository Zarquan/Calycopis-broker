/**
 *
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.data.simple.SimpleDataResource;
import net.ivoa.calycopis.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.data.simple.SimpleDataResourceEntityFactory;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookEntity;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntityFactory;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offers.OfferBlockFactory;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequestSchedule;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaS3DataResource;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeVolume;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;
import net.ivoa.calycopis.validator.compute.ComputeResourceValidator;
import net.ivoa.calycopis.validator.data.DataResourceValidator;
import net.ivoa.calycopis.validator.data.DataResourceValidator.Result;
import net.ivoa.calycopis.validator.executable.ExecutableValidator;
import net.ivoa.calycopis.validator.storage.StorageResourceValidator;

/**
 *
 */
@Slf4j
public class OfferSetRequestParserStateImpl
extends ValidatorTools
    implements OfferSetRequestParserState
    {

    /**
     * Public constructor.
     * 
     */
    public OfferSetRequestParserStateImpl(
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

    private ExecutableValidator.Result executable;
    @Override
    public ExecutableValidator.Result getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final ExecutableValidator.Result executable)
        {
        this.executable = executable;
        }
    
    /**
     * Our List of DataValidator results.
     * 
     */
    private List<DataResourceValidator.Result> dataValidatorResultList = new ArrayList<DataResourceValidator.Result> ();

    @Override
    public List<DataResourceValidator.Result> getDataResourceValidatorResults()
        {
        return dataValidatorResultList;
        }

    /**
     * Our Map of DataValidator results.
     * 
     */
    private Map<String, DataResourceValidator.Result> dataValidatorResultMap = new HashMap<String, DataResourceValidator.Result>();

    @Override
    public String makeDataValidatorResultKey(final DataResourceValidator.Result result)
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
    public void addDataValidatorResult(final DataResourceValidator.Result result)
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
    public DataResourceValidator.Result findDataValidatorResult(final DataResourceValidator.Result result)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                result
                )
            );
        }

    @Override
    public DataResourceValidator.Result findDataValidatorResult(final IvoaAbstractDataResource resource)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public DataResourceValidator.Result findDataValidatorResult(final String key)
        {
        log.debug("findDataValidatorResult(String)");
        log.debug("Key [{}]", key);
        return dataValidatorResultMap.get(key);
        }

    /**
     * Our List of ComputeValidator results.
     * 
     */
    private List<ComputeResourceValidator.Result> compValidatorResultList = new ArrayList<ComputeResourceValidator.Result>();

    @Override
    public List<ComputeResourceValidator.Result> getComputeValidatorResults()
        {
        return compValidatorResultList;
        }

    /**
     * Our Map of ComputeValidator results.
     * 
     */
    private Map<String, ComputeResourceValidator.Result> compValidatorResultMap = new HashMap<String, ComputeResourceValidator.Result>();

    @Override
    public String makeComputeValidatorResultKey(final ComputeResourceValidator.Result result)
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
    public void addComputeValidatorResult(final ComputeResourceValidator.Result result)
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
    public ComputeResourceValidator.Result findComputeValidatorResult(final ComputeResourceValidator.Result result)
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
    public ComputeResourceValidator.Result findComputeValidatorResult(final IvoaAbstractComputeResource resource)
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
    public ComputeResourceValidator.Result findComputeValidatorResult(String key)
        {
        log.debug("findComputeValidatorResult(String)");
        log.debug("Key [{}]", key);
        return compValidatorResultMap.get(key);
        }

    /**
     * Our List of StorageValidator results.
     * 
     */
    private List<StorageResourceValidator.Result> storageValidatorResultList = new ArrayList<StorageResourceValidator.Result>();

    @Override
    public List<StorageResourceValidator.Result> getStorageValidatorResults()
        {
        return storageValidatorResultList;
        }
    
    /**
     * Our Map of StorageValidator results.
     * 
     */
    private Map<String, StorageResourceValidator.Result> storageValidatorResultMap = new HashMap<String, StorageResourceValidator.Result>();

    @Override
    public String makeStorageValidatorResultKey(final StorageResourceValidator.Result result)
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
    public void addStorageValidatorResult(final StorageResourceValidator.Result result)
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
    public StorageResourceValidator.Result findStorageValidatorResult(final StorageResourceValidator.Result result)
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
    public StorageResourceValidator.Result findStorageValidatorResult(final IvoaAbstractStorageResource resource)
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
    public StorageResourceValidator.Result findStorageValidatorResult(final String key)
        {
        log.debug("findStorageValidatorResult(String)");
        log.debug("Key [{}]", key);
        return storageValidatorResultMap.get(key);
        }
    
    /**
     * A Map linking DataValidator results to StorageValidator results.
     * 
     */
    private Map<String, StorageResourceValidator.Result> dataStorageMap = new HashMap<String, StorageResourceValidator.Result>();
    
    @Override
    public void addDataStorageResult(
        final DataResourceValidator.Result dataResult,
        final StorageResourceValidator.Result storageResult
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
    public StorageResourceValidator.Result findDataStorageResult(final DataResourceValidator.Result dataResult)
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
    public StorageResourceValidator.Result findDataStorageResult(final IvoaAbstractDataResource dataResouce)
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

    }
