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

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceFactory;
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
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequestSchedule;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaS3DataResource;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import wtf.metio.storageunits.model.StorageUnit;
import wtf.metio.storageunits.model.StorageUnits;

/**
 *
 */
@Slf4j
public class OfferSetRequestParserImpl
    implements OfferSetRequestParser
    {

    private final OfferBlockFactory            offerBlockFactory;
    private final ExecutionSessionFactory             executionFactory;
    private final SimpleComputeResourceFactory simpleComputeFactory;
    private final SimpleDataResourceFactory    simpleDataFactory;
    private final AmazonS3DataResourceFactory  amazonDataFactory;
    private final JupyterNotebookFactory       jupyterNotebookFactory;

    public OfferSetRequestParserImpl(
        final OfferBlockFactory            offerBlockFactory,
        final ExecutionSessionFactory      executionFactory,
        final SimpleComputeResourceFactory simpleComputeFactory,
        final SimpleDataResourceFactory    simpleDataFactory,
        final AmazonS3DataResourceFactory  amazonDataFactory,
        final JupyterNotebookFactory       jupyterNotebookFactory
        ){
        this.offerBlockFactory      = offerBlockFactory;
        this.executionFactory       = executionFactory;
        this.simpleComputeFactory   = simpleComputeFactory;
        this.simpleDataFactory      = simpleDataFactory;
        this.amazonDataFactory      = amazonDataFactory;
        this.jupyterNotebookFactory = jupyterNotebookFactory;
        }

    private boolean valid;
    @Override
    public boolean valid()
        {
        return this.valid;
        }
    public void valid(boolean value)
        {
        this.valid = value;
        }
    public void fail()
        {
        this.valid = false;
        }

    private IvoaOfferSetRequest request;
    public IvoaOfferSetRequest getOfferSetRequest()
        {
        return this.request;
        }

    private OfferSetEntity offerset;
    public OfferSetEntity getOfferSetEntity()
        {
        return this.offerset;
        }

    private List<SimpleDataResourceEntity> dataresourcelist = new ArrayList<SimpleDataResourceEntity>();
    public List<SimpleDataResourceEntity> getDataResourceList()
        {
        return this.dataresourcelist;
        }

    private Map<String, SimpleDataResourceEntity> dataresourcemap = new HashMap<String, SimpleDataResourceEntity>();
    public SimpleDataResourceEntity findDataResource(String key)
        {
        return dataresourcemap.get(key);
        }

    protected void addDataResource(final SimpleDataResourceEntity data)
        {
        dataresourcelist.add(
            data
            );
        if (data.getUuid() != null)
            {
            dataresourcemap.put(
                data.getUuid().toString(),
                data
                );
            }
        if (data.getName() != null)
            {
            dataresourcemap.put(
                data.getName(),
                data
                );
            }
        }

    private List<SimpleComputeResourceEntity> compresourcelist = new ArrayList<SimpleComputeResourceEntity>();
    public List<SimpleComputeResourceEntity> getComputeResourceList()
        {
        return this.compresourcelist;
        }

    private Map<String, SimpleComputeResourceEntity> compresourcemap = new HashMap<String, SimpleComputeResourceEntity>();
    public SimpleComputeResourceEntity findComputeResource(final String key)
        {
        return compresourcemap.get(key);
        }

    protected void addComputeResource(final SimpleComputeResourceEntity comp)
        {
        compresourcelist.add(
            comp
            );
        if (comp.getUuid() != null)
            {
            compresourcemap.put(
                comp.getUuid().toString(),
                comp
                );
            }
        if (comp.getName() != null)
            {
            compresourcemap.put(
                comp.getName(),
                comp
                );
            }
        }

    private AbstractExecutableEntity executable;
    public AbstractExecutableEntity getExecutable()
        {
        log.debug("getExecutable() [{}]", (this.executable != null) ? this.executable.getUuid() : "null-executable");
        return this.executable;
        }
    protected void setExecutable(final AbstractExecutableEntity executable)
        {
        this.executable = executable;
        log.debug("setExecutable() [{}]", (this.executable != null) ? this.executable.getUuid() : "null-executable");
        }

    private long mincores;
    @Override
    public long getMinCores()
        {
        return this.mincores;
        }
    public void addMinCores(long delta)
        {
        this.mincores += delta;
        }

    private long maxcores;
    @Override
    public long getMaxCores()
        {
        return this.maxcores;
        }
    public void addMaxCores(long delta)
        {
        this.maxcores += delta;
        }

    private long minmemory;
    @Override
    public long getMinMemory()
        {
        return this.minmemory;
        }
    public void addMinMemory(long delta)
        {
        this.minmemory += delta;
        }

    private long maxmemory;
    @Override
    public long getMaxMemory()
        {
        return this.maxmemory;
        }
    public void addMaxMemory(long delta)
        {
        this.maxmemory += delta;
        }

    @Override
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        this.valid = true;
        this.request = request;
        this.offerset = offerset;
        //
        // Reject storage resources.
        if (request.getResources() != null)
            {
            if (request.getResources().getStorage() != null)
                {
                if (request.getResources().getStorage().size() > 0)
                    {
                    log.warn("Storage resources not supported");
                    offerset.addWarning(
                        "urn:not-supported-message",
                        "Storage resources not supported"
                        );
                    this.fail();
                    }
                }
            }
        //
        // Reject multiple compute resources.
        if (request.getResources() != null)
            {
            if (request.getResources().getCompute() != null)
                {
                if (request.getResources().getCompute().size() > 1)
                    {
                    log.warn("Multiple compute resources not supported");
                    offerset.addWarning(
                        "urn:not-supported-message",
                        "Multiple compute resources not supported"
                        );
                    this.fail();
                    }
                }
            }
        //
        // If there is no resource list, add one.
        if (request.getResources() == null)
            {
            log.debug("Adding empty resources block");
            request.setResources(
                new IvoaExecutionResourceList()
                );
            }
        //
        // If there are no compute resources, add one.
        if (request.getResources().getCompute().isEmpty())
            {
            log.debug("Adding empty compute resource");
            IvoaSimpleComputeResource compute = new IvoaSimpleComputeResource(
                "urn:simple-compute-resource"
                );
            compute.setName(
                "Simple compute resource"
                );
            request.getResources().addComputeItem(
                compute
                );
            }
        //
        // Validate our schedule
        this.validate(
            request.getSchedule()
            );
        //
        // Validate our executable.
        this.validate(
            request.getExecutable()
            );
        //
        // Validate our resources.
        if (request.getResources() != null)
            {
            //
            // Validate our data resources.
            if (request.getResources().getData() != null)
                {
                for (IvoaAbstractDataResource resource : request.getResources().getData())
                    {
                    this.validate(
                        resource
                        );
                    }
                }
            //
            // Validate our compute resources.
            if (request.getResources().getCompute() != null)
                {
                for (IvoaAbstractComputeResource resource : request.getResources().getCompute())
                    {
                    this.validate(
                        resource
                        );
                    }
                }
            }

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

log.debug("Min cores [{}]", mincores);
log.debug("Max cores [{}]", maxcores);
log.debug("Min memory [{}]", minmemory);
log.debug("Max memory [{}]", maxmemory);

log.debug("Executable [{}][{}]", executable.getName(), executable.getClass().getName());

for (SimpleDataResource resource : dataresourcelist)
    {
    log.debug("Data resource [{}][{}]", resource.getName(), resource.getClass().getName());
    }

for (SimpleComputeResource resource : compresourcelist)
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
                    mincores,
                    minmemory
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
                    long corescale   = offerblock.getCores()/this.getMinCores();
                    long memoryscale = offerblock.getMemory()/this.getMinMemory();
                    log.debug("----");
                    log.debug("OfferBlock [{}][{}]", offerblock.getCores(), offerblock.getMemory());
                    log.debug("Cores  [{}][{}][{}]", this.getMinCores(), offerblock.getCores(), corescale);
                    log.debug("Memory [{}][{}][{}]", this.getMinMemory(), offerblock.getMemory(), memoryscale);
                    for (SimpleComputeResourceEntity compresource : compresourcelist)
                        {
                        long offercores  = compresource.getMinRequestedCores()  * corescale;
                        long offermemory = compresource.getMinRequestedMemory() * memoryscale;
                        log.debug("----");
                        log.debug("Computing resource [{}][{}]", compresource.getName(), compresource.getClass().getName());
                        log.debug("Cores  [{}][{}][{}]", compresource.getMinRequestedCores(),  offercores,  corescale);
                        log.debug("Memory [{}][{}][{}]", compresource.getMinRequestedMemory(), offermemory, memoryscale);
                        execution.addCompute(
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

    /**
     * List of requested start intervals.
     *
     */
    List<Interval> startintervals = new ArrayList<Interval>();

    @Override
    public List<Interval> getStartIntervals()
        {
        return this.startintervals;
        }


    /**
     * Requested duration.
     *
     */
    Duration exeduration = null;

    @Override
    public Duration getDuration()
        {
        return this.exeduration;
        }

    /**
     * Default execution duration.
     *
     */
    Duration DEFAULT_EXEC_DURATION = Duration.ofHours(2);

    /**
     * Default duration for the default start interval.
     *
     */
    Duration DEFAULT_START_DURATION = Duration.ofHours(2);

    /**
     * Validate the requested Schedule.
     *
     */
    public void validate(final IvoaOfferSetRequestSchedule schedule)
        {
        log.debug("validate(IvoaExecutionSessionRequestSchedule)");
        if (schedule != null)
            {
            IvoaScheduleRequestBlock requested = schedule.getRequested();
            if (requested != null)
                {
                String durationstr = requested.getDuration();
                if (durationstr != null)
                    {
                    try {
                        Duration durationval = Duration.parse(
                            durationstr
                            );
                        this.exeduration = durationval;
                        log.debug("Duration [{}][{}]", durationstr, durationval);
                        }
                    catch (Exception ouch)
                        {
                        offerset.addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse duration [${string}][${message}]",
                            Map.of(
                                "value",
                                durationstr,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        this.fail();
                        }
                    }

                List<String> startstrlist = requested.getStart();
                if (startstrlist != null)
                    {
                    for (String startstr : startstrlist)
                        {
                        try {
                            Interval startint = Interval.parse(
                                startstr
                                );
                            this.startintervals.add(
                                startint
                                );
                            log.debug("Interval [{}][{}]", startstr, startint);
                            }
                        catch (Exception ouch)
                            {
                            offerset.addWarning(
                                "urn:input-syntax-fail",
                                "Unable to parse interval [${string}][${message}]",
                                Map.of(
                                    "value",
                                    startstr,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            this.fail();
                            }
                        }
                    }
                }
            }

        if (startintervals.isEmpty())
            {
            Interval defaultint = Interval.of(
                Instant.now(),
                DEFAULT_START_DURATION
                );
            log.debug("Interval list is empty, adding default [{}]", defaultint);
            startintervals.add(
                defaultint
                );
            }

        if (exeduration == null)
            {
            log.debug("Duration is empty, using default [{}]", DEFAULT_EXEC_DURATION);
            exeduration = DEFAULT_EXEC_DURATION;
            }
        }

    /**
     * Validate an AbstractExecutable.
     *
     */
    public void validate(final IvoaAbstractExecutable executable)
        {
        log.debug("validate(IvoaAbstractExecutable)");
        switch(executable)
            {
            case IvoaJupyterNotebook notebook:
                validate(
                    notebook
                    );
                break;

            default:
                offerset.addWarning(
                    "urn:unknown-resource-type",
                    "Unknown executable type [${type}][${class}]",
                    Map.of(
                        "type",
                        executable.getType(),
                        "class",
                        executable.getClass().getName()
                        )
                    );
                this.fail();
                break;
            }
        }

    /**
     * Validate a JupyterNotebook Executable.
     *
     */
    public void validate(final IvoaJupyterNotebook request)
        {
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("JupyterNotebook [{}]", request.getName());

        String name = trimString(
            request.getName()
            );
        String location = trimString(
            request.getLocation()
            );

        if ((location == null) || (location.isEmpty()))
            {
            offerset.addWarning(
                "urn:missing-required-value",
                "Notebook location required"
                );
            this.fail();
            }

        JupyterNotebookEntity entity = this.jupyterNotebookFactory.create(
            null,
            name,
            location
            );
        this.setExecutable(
            entity
            );
        }

    /**
     * Validate an AbstractDataResource.
     *
     */
    private void validate(IvoaAbstractDataResource resource)
        {
        switch(resource)
        {
        case IvoaSimpleDataResource simple :
            validate(
                simple
                );
            break;

        case IvoaS3DataResource s3 :
            validate(
                s3
                );
            break;

        default:
            offerset.addWarning(
                "urn:unknown-resource-type",
                "Unknown resource type [${resource}][${type}][${class}]",
                Map.of(
                    "resource",
                    resource.getName(),
                    "type",
                    resource.getType(),
                    "class",
                    resource.getClass().getName()
                    )
                );
            this.fail();
            break;
            }
        }

    /**
     * Validate a SimpleDataResource.
     *
     */
    private void validate(IvoaSimpleDataResource request)
        {
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("DataResource [{}]", request.getName());

        String name = trimString(
            request.getName()
            );
        String location = trimString(
            request.getLocation()
            );

        if ((location == null) || (location.isEmpty()))
            {
            offerset.addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            this.fail();
            }

        SimpleDataResourceEntity entity = this.simpleDataFactory.create(
            null,
            name,
            location
            );
        this.addDataResource(
            entity
            );
        }

    /**
     * Validate a S3DataResource.
     *
     */
    private void validate(IvoaS3DataResource request)
        {
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("DataResource [{}]", request.getName());

        String name = trimString(
            request.getName()
            );
        String endpoint = trimString(
            request.getEndpoint()
            );
        String template = trimString(
            request.getTemplate()
            );
        String bucket = trimString(
            request.getBucket()
            );
        String object = trimString(
            request.getObject()
            );

        if ((endpoint == null) || (endpoint.isEmpty()))
            {
            offerset.addWarning(
                "urn:missing-required-value",
                "S3 service endpoint required"
                );
            this.fail();
            }

        if ((template == null) || (template.isEmpty()))
            {
            offerset.addWarning(
                "urn:missing-required-value",
                "S3 service template required"
                );
            this.fail();
            }

        if ((bucket == null) || (bucket.isEmpty()))
            {
            offerset.addWarning(
                "urn:missing-required-value",
                "S3 bucket name required"
                );
            this.fail();
            }

        AmazonS3DataResourceEntity entity = this.amazonDataFactory.create(
            null,
            name,
            endpoint,
            template,
            bucket,
            object
            );
/*
 *
        // TODO Allow multiple data types.
        this.addDataResource(
            entity
            );
 *
 */
        }

    /**
     * Validate an AbstractComputeResource.
     *
     */
    public void validate(final IvoaAbstractComputeResource resource)
        {
        log.debug("validate(IvoaAbstractComputeResource)");
        log.debug("Compute [{}])", ((resource != null) ? resource .getName() : "null-resource"));
        switch(resource)
            {
            case IvoaSimpleComputeResource simple :
                validate(
                    simple
                    );
                break;

            default:
                offerset.addWarning(
                    "urn:unknown-resource-type",
                    "Unknown resource type [${resource}][${type}][${class}]",
                    Map.of(
                        "resource",
                        resource.getName(),
                        "type",
                        resource.getType(),
                        "class",
                        resource.getClass().getName()
                        )
                    );
                this.fail();
                break;
            }
        }

    /**
     * Validate a SimpleComputeResource.
     *
     */
    public void validate(final IvoaSimpleComputeResource resource)
        {
        log.debug("validate(IvoaSimpleComputeResource)");
        log.debug("Compute [{}])", ((resource != null) ? resource .getName() : "null-resource"));

        Long MIN_CORES_DEFAULT =  1L ;
        Long MAX_CORES_LIMIT   = 16L ;
        Long mincores = MIN_CORES_DEFAULT;
        Long maxcores = MIN_CORES_DEFAULT;
        Boolean minimalcores  = false;

        if (resource.getCores() != null)
            {
            if (resource.getCores().getRequested() != null)
                {
                if (resource.getCores().getRequested().getMin() != null)
                    {
                    mincores = resource.getCores().getRequested().getMin();
                    }
                if (resource.getCores().getRequested().getMax() != null)
                    {
                    maxcores = resource.getCores().getRequested().getMax();
                    }
                if (resource.getCores().getRequested().getMinimal() != null)
                    {
                    minimalcores = resource.getCores().getRequested().getMinimal();
                    }
                }

            if (resource.getCores().getOffered() != null)
                {
                offerset.addWarning(
                    "urn:service-defined",
                    "Offered cores should not be set [${resource}][${offered}]",
                    Map.of(
                        "resource",
                        resource.getName(),
                        "offered",
                        resource.getCores().getOffered()
                        )
                    );
                this.fail();
                }
            }
        if (mincores > MAX_CORES_LIMIT)
            {
            offerset.addWarning(
                "urn:resource-limit",
                "Minimum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    resource.getName(),
                    "cores",
                    mincores,
                    "limit",
                    MAX_CORES_LIMIT
                    )
                );
            this.fail();
            }
        if (maxcores > MAX_CORES_LIMIT)
            {
            offerset.addWarning(
                "urn:resource-limit",
                "Maximum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    resource.getName(),
                    "cores",
                    maxcores,
                    "limit",
                    MAX_CORES_LIMIT
                    )
                );
            this.fail();
            }
        this.addMinCores(
            mincores
            );
        this.addMaxCores(
            maxcores
            );

        Long MIN_MEMORY_DEFAULT =  1L;
        Long MAX_MEMORY_LIMIT   = 16L;
        Long minmemory = MIN_MEMORY_DEFAULT;
        Long maxmemory = MIN_MEMORY_DEFAULT;
        Boolean minimalmemory = false;
        
        //StorageUnit<?> MIN_MEMORY_DEFAULT = StorageUnits.gibibyte(1);
        //StorageUnit<?> MAX_MEMORY_LIMIT   = StorageUnits.gibibyte(16);
        //StorageUnit<?> minmemory = MIN_MEMORY_DEFAULT;

        if (resource.getMemory() != null)
            {
            if (resource.getMemory().getRequested() != null)
                {
                if (resource.getMemory().getRequested().getMin() != null)
                    {
                    minmemory = resource.getMemory().getRequested().getMin();
                    }
                if (resource.getMemory().getRequested().getMax() != null)
                    {
                    maxmemory = resource.getMemory().getRequested().getMax();
                    }
                if (resource.getMemory().getRequested().getMinimal() != null)
                    {
                    minimalmemory = resource.getMemory().getRequested().getMinimal();
                    }
                }

            if (resource.getMemory().getOffered() != null)
                {
                offerset.addWarning(
                    "urn:service-defined",
                    "Offered memory should not be set [${resource}][${offered}]",
                    Map.of(
                        "resource",
                        resource.getName(),
                        "offered",
                        resource.getMemory().getOffered()
                        )
                    );
                this.fail();
                }
            }

        if (minmemory > MAX_MEMORY_LIMIT)
            {
            offerset.addWarning(
                "urn:resource-limit",
                "Minimum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    resource.getName(),
                    "memory",
                    minmemory,
                    "limit",
                    MAX_MEMORY_LIMIT
                    )
                );
            }

        if (maxmemory > MAX_MEMORY_LIMIT)
            {
            offerset.addWarning(
                "urn:resource-limit",
                "Maximum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    resource.getName(),
                    "memory",
                    maxmemory,
                    "limit",
                    MAX_MEMORY_LIMIT
                    )
                );
            }
        
        this.addMinMemory(
            minmemory
            );

        this.addMaxMemory(
            maxmemory
            );
        
        //
        // Process the network ports.
        // ....

        //
        // Process the volume mounts.
        // ....

        SimpleComputeResourceEntity entity = this.simpleComputeFactory.create(
            null,
            resource.getName(),
            mincores,
            maxcores,
            null,
            null,
            minmemory,
            maxmemory,
            null,
            null,
            minimalcores,
            minimalmemory
            );
        this.addComputeResource(
            entity
            );
        }

    /**
     * Trim a String value, skipping it if it is null.
     *
     */
    public static String trimString(String string)
        {
        if (string != null)
            {
            string = string.trim();
            }
        return string;
        }
    }
