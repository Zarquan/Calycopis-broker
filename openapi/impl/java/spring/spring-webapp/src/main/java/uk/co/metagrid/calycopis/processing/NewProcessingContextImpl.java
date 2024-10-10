/**
 * 
 */
package uk.co.metagrid.calycopis.processing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.threeten.extra.Interval;

import com.github.f4b6a3.uuid.UuidCreator;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlockItem;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlockValue;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.model.ProcessingContext;
import uk.co.metagrid.ambleck.model.ProcessingContext.ScheduleItem;
import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceEntity;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceEntity;
import uk.co.metagrid.calycopis.executable.AbstractExecutableEntity;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookEntity;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookFactory;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;

/**
 * 
 */
@Slf4j
public class NewProcessingContextImpl
    implements NewProcessingContext
    {

    protected NewProcessingContextImpl()
        {
        }

    private boolean valid;
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
    @Override
    public void fail()
        {
        this.valid = false;
        }

    private IvoaOfferSetRequest request;
    @Override
    public IvoaOfferSetRequest getOfferSetRequest()
        {
        return this.request;
        }

    private OfferSetEntity offerset;
    @Override
    public OfferSetEntity getOfferSetEntity()
        {
        return this.offerset;
        }

    @Override
    public void addDataResource(IvoaAbstractDataResource data)
        {
        // TODO Auto-generated method stub
        }

    private List<SimpleDataResourceEntity> dataresourcelist = new ArrayList<SimpleDataResourceEntity>();  
    @Override
    public List<SimpleDataResourceEntity> getDataResourceList()
        {
        return this.dataresourcelist;
        }

    private Map<String, SimpleDataResourceEntity> dataresourcemap = new HashMap<String, SimpleDataResourceEntity>();  
    @Override
    public SimpleDataResourceEntity findDataResource(String key)
        {
        return dataresourcemap.get(key);
        }

    @Override
    public void addComputeResource(IvoaAbstractComputeResource comp)
        {
        // TODO Auto-generated method stub
        }

    private List<SimpleComputeResourceEntity> compresourcelist = new ArrayList<SimpleComputeResourceEntity>();  
    @Override
    public List<SimpleComputeResourceEntity> getComputeResourceList()
        {
        return this.compresourcelist;
        }

    private Map<String, SimpleComputeResourceEntity> compresourcemap = new HashMap<String, SimpleComputeResourceEntity>();  
    @Override
    public SimpleComputeResourceEntity findComputeResource(String key)
        {
        return compresourcemap.get(key);
        }

    @Override
    public void addExecutable(IvoaAbstractExecutable executable)
        {
        // TODO Auto-generated method stub
        }

    private AbstractExecutableEntity executable;
    @Override
    public AbstractExecutableEntity getExecutable()
        {
        return this.executable;
        }

    private long mincores;
    @Override
    public long getMinCores()
        {
        return this.mincores;
        }
    @Override
    public void addMinCores(long delta)
        {
        this.mincores += delta;
        }

    private long minmemory;
    @Override
    public long getMinMemory()
        {
        return this.minmemory;
        }
    @Override
    public void addMinMemory(long delta)
        {
        this.minmemory += delta;
        }

    public class ScheduleItemImpl
        implements ScheduleItem
        {
        public ScheduleItemImpl(final Interval starttime, final Duration duration)
            {
            this.starttime = starttime ;
            this.duration  = duration;
            }
        private Interval starttime ;
        public Interval getStartTime()
            {
            return this.starttime;
            }
        private Duration duration;
        public Duration getDuration()
            {
            return this.duration;
            }
        }

    private ScheduleItem preptime;
    @Override
    public ScheduleItem getPreparationTime()
        {
        return this.preptime;
        }
    @Override
    public void setPreparationTime(Interval starttime, Duration duration)
        {
        this.preptime = new ScheduleItemImpl(
            starttime,
            duration
            );
        }

    private ScheduleItem exectime;
    @Override
    public ScheduleItem getExecutionTime()
        {
        return this.exectime;
        }
    @Override
    public void setExecutionTime(Interval starttime, Duration duration)
        {
        this.exectime = new ScheduleItemImpl(
            starttime,
            duration
            );
        }

    
    @Override
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        this.request  = request;
        this.offerset = offerset;
        //
        // Reject storage resources.
        if (request.getResources() != null)
            {
            if (request.getResources().getStorage() != null)
                {
                if (request.getResources().getStorage().size() > 0)
                    {
                    offerset.addWarning(
                        "urn:not-supported-message",
                        "Storage resources not supported in by this service"
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
                    offerset.addWarning(
                        "urn:not-supported-message",
                        "Multiple compute resources not supported in by this service"
                        );
                    this.fail();
                    }
                }
            }
        //
        // If there is no resource list, add one.
        if (request.getResources() == null)
            {
            request.setResources(
                new IvoaExecutionResourceList()
                );
            }
        //
        // If there are no compute resources, add one.
        if (request.getResources().getCompute().isEmpty())
            {
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
        // Validate the requested Schedule
        validate(
            request.getSchedule()
            );
        //
        // Validate the executable.
        validate(
            request.getExecutable()
            );
        
        }

    
    
    /**
     * Validate the requested Schedule.
     * This is totally the wrong shape, see #65 
     * https://github.com/ivoa/Calycopis-broker/issues/65
     * 
     */
    public void validate(final IvoaStringScheduleBlock schedule)
        {
        log.debug("validate(StringScheduleBlock)");
        if (schedule != null)
            {
            //
            // Check the offered section is empty.
            // ....

            IvoaStringScheduleBlockItem requested = schedule.getRequested();
            if (requested != null);
                {
                IvoaStringScheduleBlockValue preparing = requested.getPreparing();
                if (preparing != null)
                    {
                    Interval prepstart = null;
                    Duration preptime  = null;

                    if (preparing.getStart() != null)
                        {
                        String string = preparing.getStart();
                        try {
                            prepstart = Interval.parse(
                                string
                                );
                            }
                        catch (Exception ouch)
                            {
                            offerset.addWarning(
                                "urn:input-syntax-message",
                                "Unable to parse start interval [${value}][${message}]",
                                Map.of(
                                    "value",
                                    string,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            this.fail();
                            }
                        }
                    if (preparing.getDuration() != null)
                        {
                        String string = preparing.getDuration();
                        try {
                            preptime = Duration.parse(
                                string
                                );
                            }
                        catch (Exception ouch)
                            {
                            offerset.addWarning(
                                "urn:input-syntax-message",
                                "Unable to parse duration [${value}][${message}]",
                                Map.of(
                                    "value",
                                    string,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            this.fail();
                            }
                        }
                    this.setPreparationTime(
                        prepstart,
                        preptime
                        );
                    }

                IvoaStringScheduleBlockValue executing = requested.getExecuting();
                if (executing != null)
                    {
                    Interval execstart = null;
                    Duration exectime  = null;
                    if (executing.getStart() != null)
                        {
                        String string = executing.getStart();
                        try {
                            execstart = Interval.parse(
                                string
                                );
                            }
                        catch (Exception ouch)
                            {
                            offerset.addWarning(
                                "urn:input-syntax-message",
                                "Unable to parse start interval [${value}][${message}]",
                                Map.of(
                                    "value",
                                    string,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            this.fail();
                            }
                        }
                    if (executing.getDuration() != null)
                        {
                        String string = executing.getDuration();
                        try {
                            exectime = Duration.parse(
                                string
                                );
                            }
                        catch (Exception ouch)
                            {
                            offerset.addWarning(
                                "urn:input-syntax-message",
                                "Unable to parse duration [${value}][${message}]",
                                Map.of(
                                    "value",
                                    string,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            this.fail();
                            }
                        }
                    this.setExecutionTime(
                        execstart,
                        exectime
                        );
                    }
                }
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
                    "urn:unknown-type-message",
                    "Unknown executable type [${type}]",
                    Map.of(
                        "type",
                        executable.getType()
                        )
                    );
                this.fail();
                break;
            }
        }

    
    private JupyterNotebookFactory notebookfactory ;

    /**
     * Validate a JupyterNotebook Executable.
     *
     */
    public void validate(final IvoaJupyterNotebook request)
        {
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("JupyterNotebook [{}]", request.getName());

        JupyterNotebookEntity entity = notebookfactory.create(
            null,
            request.getName(),
            request.getNotebook()
            );

        String filename = request.getNotebook();
        if ((filename == null) || (filename.trim().isEmpty()))
            {
            entity.addWarning(
                "urn:missing-required-value",
                "Notebook location required"
                );
            this.fail();
            }


        this.executable = .notebook..
        this.setExecutable(
            result
            );
        }
    
    
    }
