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

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceFactory;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceFactory;
import net.ivoa.calycopis.data.simple.SimpleDataResource;
import net.ivoa.calycopis.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.data.simple.SimpleDataResourceFactory;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookEntity;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookFactory;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.execution.ExecutionSessionFactory;
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

/**
 *
 */
@Slf4j
public class OfferSetRequestParserStateImpl
extends ValidatorTools
    implements OfferSetRequestParserState
    {

    private OfferBlockFactory            offerBlockFactory;
    private ExecutionSessionFactory      executionFactory;
    private SimpleComputeResourceFactory simpleComputeFactory;
    private JupyterNotebookFactory       jupyterNotebookFactory;

    /**
     * Public constructor.
     * 
     */
    public OfferSetRequestParserStateImpl(
        final IvoaOfferSetRequest offersetRequest,
        final OfferSetEntity offersetEntity
        ){
        this.originalRequest  = offersetRequest;
        this.validatedRequest = new IvoaOfferSetRequest();
        this.offersetEntity   = offersetEntity;
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

    private Validator.Result<IvoaAbstractExecutable, AbstractExecutableEntity> executable;
    @Override
    public Validator.Result<IvoaAbstractExecutable, AbstractExecutableEntity> getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final Validator.Result<IvoaAbstractExecutable, AbstractExecutableEntity> executable)
        {
        this.executable = executable;
        }
    
    private List<Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> > dataResourceList = new ArrayList<Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity>> ();
    private Map<String, Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity>> dataResourceMap = new HashMap<String, Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> >();

    @Override
    public Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> findDataResource(String key)
        {
        log.debug("findDataResource(String)");
        log.debug("Key [{}]", key);
        return dataResourceMap.get(key);
        }

    @Override
    public void addDataResource(final Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> resource)
        {
        log.debug("addDataResource(String)");
        log.debug("Resource [{}][{}]", resource.getObject().getUuid(), resource.getObject().getName());
        dataResourceList.add(
            resource
            );
        if (resource.getObject().getUuid() != null)
            {
            dataResourceMap.put(
                resource.getObject().getUuid().toString(),
                resource
                );
            }
        if (resource.getObject().getName() != null)
            {
            dataResourceMap.put(
                resource.getObject().getName(),
                resource
                );
            }
        }

    private List<Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity>> compResourceList = new ArrayList<Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity>>();
    private Map<String, Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity>> compResourceMap = new HashMap<String, Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity>>();

    @Override
    public Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity> findComputeResource(String key)
        {
        log.debug("findComputeResource(String)");
        log.debug("Key [{}]", key);
        return compResourceMap.get(key);
        }

    @Override
    public void addComputeResource(final Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity> resource)
        {
        log.debug("addComputeResource(String)");
        log.debug("Resource [{}][{}]", resource.getObject().getUuid(), resource.getObject().getName());
        compResourceList.add(
            resource
            );
        if (resource.getObject().getUuid() != null)
            {
            compResourceMap.put(
                resource.getObject().getUuid().toString(),
                resource
                );
            }
        if (resource.getObject().getName() != null)
            {
            compResourceMap.put(
                resource.getObject().getName(),
                resource
                );
            }
        }

    private List<Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity>> storageResourceList = new ArrayList<Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity>>();
    private Map<String, Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity>> storageResourceMap = new HashMap<String, Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity>>();

    @Override
    public Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity> findStorageResource(String key)
        {
        log.debug("findStorageResource(String)");
        log.debug("Key [{}]", key);
        return storageResourceMap.get(key);
        }

    @Override
    public void addStorageResource(final Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity> resource)
        {
        log.debug("addStorageResource(String)");
        log.debug("Resource [{}][{}]", resource.getObject().getUuid(), resource.getObject().getName());
        storageResourceList.add(
            resource
            );
        if (resource.getObject().getUuid() != null)
            {
            storageResourceMap.put(
                resource.getObject().getUuid().toString(),
                resource
                );
            }
        if (resource.getObject().getName() != null)
            {
            storageResourceMap.put(
                resource.getObject().getName(),
                resource
                );
            }
        }
    
    /**
     * A Map of DataResources to StorageResources.
     * 
     */
    private Map<String, IvoaAbstractStorageResource> dataStorageMap = new HashMap<String, IvoaAbstractStorageResource>();
    
    /**
     * Link a StorageResource to a DataResource.
     * 
     */
    public void addDataStorageResource(final IvoaAbstractDataResource data, final IvoaAbstractStorageResource stor)
        {
        log.debug("addDataStorageResource(IvoaAbstractDataResource, IvoaAbstractStorageResource )");
        String dataUuid = ((data.getUuid() != null) ? data.getUuid().toString() : null);
        String storUuid = ((stor.getUuid() != null) ? stor.getUuid().toString() : null);
        log.debug("DataResource [{}][{}]", data.getName(), dataUuid);
        log.debug("StorResource [{}][{}]", stor.getName(), storUuid);

        if (dataStorageMap.get(dataUuid) != null)
            {
            log.error("Duplicate DataResource entry [{}][{}]", data.getName(), dataUuid);
            }
        else {
            dataStorageMap.put(
                dataUuid,
                stor
                );
            }
        }
    
    /**
     * Get the StorageResource for a DataResource.
     * 
     */
    public IvoaAbstractStorageResource findDataStorageResource(final IvoaAbstractDataResource data)
        {
        log.debug("findDataStorageResource(IvoaAbstractDataResource)");
        String dataUuid = ((data.getUuid() != null) ? data.getUuid().toString() : null);
        log.debug("DataResource [{}][{}]", data.getName(), dataUuid);
        return dataStorageMap.get(
            data.getUuid().toString()
            );
        }

    /**
     * A list of requested start intervals.
     *
     */
    private List<Interval> startintervals = new ArrayList<Interval>();
    public List<Interval> getStartIntervals()
        {
        return this.startintervals;
        }

    /**
     * The requested duration.
     *
     */
    private Duration exeduration = null;
    public Duration getDuration()
        {
        return this.exeduration;
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

    
    
    
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        this.valid = true;

        //
        // Start with NO, and set to YES when we have at least one offer.
        IvoaOfferSetResponse.ResultEnum result = IvoaOfferSetResponse.ResultEnum.NO;

        //
        // If everything is OK.
        if (this.valid)
            {
            //
            // Generate some offers ..
log.debug("---- ---- ---- ----");
log.debug("Generating offers ....");
log.debug("Start intervals [{}]", startintervals);
log.debug("Execution duration [{}]", exeduration);

log.debug("Min cores [{}]", totalMinCores);
log.debug("Max cores [{}]", totalMaxCores);
log.debug("Min memory [{}]", totalMinMemory);
log.debug("Max memory [{}]", totalMaxMemory);

log.debug("Executable [{}][{}]", executable.getName(), executable.getClass().getName());

for (SimpleDataResource resource : dataResourceList)
    {
    log.debug("Data resource [{}][{}]", resource.getName(), resource.getClass().getName());
    }

for (SimpleComputeResource resource : compResourceList)
    {
    log.debug("Computing resource [{}][{}]", resource.getName(), resource.getClass().getName());
    }
log.debug("---- ---- ---- ----");

            //
            // Populate our OfferSet ..
            for (Interval startinterval : startintervals)
                {
                //
                // Generate a list of available blocks.
                List<OfferBlock> offerblocks = offerBlockFactory.generate(
                    startinterval,
                    exeduration,
                    totalMinCores,
                    totalMinMemory
                    );

                for (OfferBlock offerblock : offerblocks)
                    {
                    log.debug("OfferBlock [{}]", offerblock.getStartTime());
                    ExecutionSessionEntity execution = executionFactory.create(
                        offerblock,
                        offerset,
                        this
                        );
                    log.debug("ExecutionEntity [{}]", execution.getUuid());
                    offerset.addExecution(
                        execution
                        );

                    // TODO Add an AbstractExecutableFactory.
                    execution.setExecutable(
                        jupyterNotebookFactory.create(
                            execution,
                            ((JupyterNotebookEntity) this.executable)
                            )
                        );

                    //
                    // TODO Add the data resources.
                    //

                    //
                    // TODO simple scaling factor means we need to handle fractions ?
                    // TODO Both CANFAR and Openstack only support fixed sizes.
                    // We want
                    //     foreach node
                    //     the upper limit is the individual node multiplied by the scaling factor
                    //     find an allowed config that is between the requested minimum and the scaled limit
                    //     this depends on a call to the platform to check the available sizes for this user
                    // this should be managed by the canfar classes
                    // so we call out to platform with the block size and request size
                    // platform responds with a list of compute entities that fit the limits
                    long corescale   = offerblock.getCores()/this.getTotalMinCores();
                    long memoryscale = offerblock.getMemory()/this.getTotalMinMemory();
                    log.debug("----");
                    log.debug("OfferBlock [{}][{}]", offerblock.getCores(), offerblock.getMemory());
                    log.debug("Cores  [{}][{}][{}]", this.getTotalMinCores(), offerblock.getCores(), corescale);
                    log.debug("Memory [{}][{}][{}]", this.getTotalMinMemory(), offerblock.getMemory(), memoryscale);
                    for (SimpleComputeResourceEntity compresource : compResourceList)
                        {
                        long offercores  = compresource.getMinRequestedCores()  * corescale;
                        long offermemory = compresource.getMinRequestedMemory() * memoryscale;
                        log.debug("----");
                        log.debug("Computing resource [{}][{}]", compresource.getName(), compresource.getClass().getName());
                        log.debug("Cores  [{}][{}][{}]", compresource.getMinRequestedCores(),  offercores,  corescale);
                        log.debug("Memory [{}][{}][{}]", compresource.getMinRequestedMemory(), offermemory, memoryscale);
                        execution.addComputeResource(
                            simpleComputeFactory.create(
                                execution,
                                compresource,
                                offercores,
                                offercores,
                                offermemory,
                                offermemory
                                )
                            );
                        }
                    log.debug("----");
                    //
                    // Confirm we have at least one result.
                    result = IvoaOfferSetResponse.ResultEnum.YES;
                    }
                }
            }
        //
        // Set the OfferSet result.
        offerset.setResult(
            result
            );
        }


    }
