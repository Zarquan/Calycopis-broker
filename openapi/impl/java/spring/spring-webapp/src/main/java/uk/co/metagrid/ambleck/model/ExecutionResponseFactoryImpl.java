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
 *
 */
package uk.co.metagrid.ambleck.model;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.f4b6a3.uuid.UuidCreator;

import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponse.StateEnum;

//import uk.co.metagrid.ambleck.model.UpdateRequest;
import uk.co.metagrid.ambleck.model.AbstractUpdate;
import uk.co.metagrid.ambleck.model.EnumValueUpdate;

import uk.co.metagrid.ambleck.message.DebugMessage;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.message.InfoMessage;

@Component
public class ExecutionResponseFactoryImpl
    implements ExecutionResponseFactory
    {

    /*
     * This factory identifier.
     *
     */
    private final UUID uuid ;

    /*
     * Get the factory identifier.
     *
     */
    @Override
    public UUID getUuid()
        {
        return this.uuid ;
        }

    @Autowired
    public ExecutionResponseFactoryImpl()
        {
        this.uuid = UuidCreator.getTimeBased();
        }

    /**
     * Our Map of ExecutionResponses.
     *
     */
    private Map<UUID, ExecutionResponseImpl> hashmap = new HashMap<UUID, ExecutionResponseImpl>();

    /**
     * Insert an ExecutionResponse into our HashMap.
     *
     */
    protected void insert(final ExecutionResponseImpl execution)
        {
        this.hashmap.put(
            execution.getUuid(),
            execution
            );
        }

    /**
     * Select an Execution based on its identifier.
     *
     */
    @Override
    public ExecutionResponseImpl select(final UUID uuid)
        {
        return this.hashmap.get(
            uuid
            );
        }

    /*
     * Check for null values.
     *
     */
    public String safeString(final Object value)
        {
        if (value == null)
            {
            return "null" ;
            }
        else {
            if (value instanceof String)
                {
                return (String) value ;
                }
            else {
                return value.toString();
                }
            }
        }

    /**
     * A local class to hold context during processing.
     * We can pass one thing down the processing chain rather than many things.
     * TODO Move this up a level to OfferSetResponseFactory.
     *
     */
    public static class ProcessingContext
        {
        private boolean valid = true ;
        public boolean valid()
            {
            return this.valid;
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

        public ProcessingContext(final String baseurl, final OfferSetRequest request, final OfferSetResponse response)
            {
            this.baseurl  = baseurl ;
            this.request  = request ;
            this.response = response ;
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

        public int DEFAULT_MIN_CORES = 1 ;
        private int mincores = 0 ;
        public int getMinCores()
            {
            if (this.mincores != 0)
                {
                return this.mincores ;
                }
            else {
                return DEFAULT_MIN_CORES ;
                }
            }
        public void addMinCores(int delta)
            {
            this.mincores += delta ;
            }

        public int DEFAULT_MAX_CORES = 1 ;
        private int maxcores = 0 ;
        public int getMaxCores()
            {
            if (this.maxcores != 0)
                {
                return this.maxcores ;
                }
            else {
                return DEFAULT_MAX_CORES ;
                }
            }
        public void addMaxCores(int delta)
            {
            this.maxcores += delta ;
            }

        public int DEFAULT_MIN_MEMORY = 1 ;
        private int minmemory = 0 ;
        public int getMinMemory()
            {
            if (this.minmemory != 0)
                {
                return this.minmemory ;
                }
            else {
                return DEFAULT_MIN_MEMORY ;
                }
            }
        public void addMinMemory(int delta)
            {
            this.minmemory += delta ;
            }

        public int DEFAULT_MAX_MEMORY = 1 ;
        private int maxmemory = 0 ;
        public int getMaxMemory()
            {
            if (this.maxmemory != 0)
                {
                return this.maxmemory ;
                }
            else {
                return DEFAULT_MAX_MEMORY ;
                }
            }
        public void addMaxMemory(int delta)
            {
            this.maxmemory += delta ;
            }

        public int DEFAULT_MIN_DURATION = 10 ;
        private int minduration = 0 ;
        public int getMinDuration()
            {
            if (this.minduration != 0)
                {
                return this.minduration ;
                }
            else {
                return DEFAULT_MIN_DURATION ;
                }
            }
        public void addMinDuration(int delta)
            {
            this.minduration += delta ;
            }

        public int DEFAULT_MAX_DURATION = 10 ;
        private int maxduration = 0 ;
        public int getMaxDuration()
            {
            if (this.maxduration != 0)
                {
                return this.maxduration ;
                }
            else {
                return DEFAULT_MAX_DURATION ;
                }
            }
        public void addMaxDuration(int delta)
            {
            this.maxduration += delta ;
            }
        }

    /*
     * Resource statistics for an offer.
     *
     */
    public interface ExecutionBlock
        {
        public long getBlockStart();
        public long getBlockLength();
        public int  getMinCores();
        public int  getMaxCores();
        public int  getMinMemory();
        public int  getMaxMemory();
        }

    public static class ExecutionBlockImpl implements ExecutionBlock
        {
        public ExecutionBlockImpl(
            long blockStart,
            long blockLength,
            int minCores,
            int maxCores,
            int minMemory,
            int maxMemory
            ) {
            this.blockStart  = blockStart  ;
            this.blockLength = blockLength ;
            this.minCores = minCores ;
            this.maxCores = maxCores ;
            this.minMemory = minMemory ;
            this.maxMemory = maxMemory ;
            }

        private long blockStart;
        public long getBlockStart()
            {
            return this.blockStart;
            }

        private long blockLength;
        public long getBlockLength()
            {
            return this.blockLength;
            }

        private int minCores;
        public int  getMinCores()
            {
            return this.minCores;
            }

        private int maxCores;
        public int  getMaxCores()
            {
            return this.maxCores;
            }

        private int minMemory;
        public int  getMinMemory()
            {
            return this.minMemory;
            }

        private int maxMemory;
        public int  getMaxMemory()
            {
            return this.maxMemory;
            }
        }

    /*
     * Get a list of potential offers from our database.
     *
     */
    public List<ExecutionBlock> getExecutionBlockList(final ProcessingContext context)
        {
        List<ExecutionBlock> list = new ArrayList<ExecutionBlock>();
        list.add(
            new ExecutionBlockImpl(
                10,2,3,6,4,8
                )
            );
        list.add(
            new ExecutionBlockImpl(
                20,4,6,12,8,16
                )
            );
        return list ;
        }

    /*
     * Insert an Execution into our database.
     *
     */
    public void addExecutionBlock(final ExecutionResponse offer)
        {

        }


    /**
     * Process an OfferSetRequest and populate an OfferSetResponse with ExecutionResponse offers.
     *
     */
    @Override
    public void create(final String baseurl, final OfferSetRequest request, final OfferSetResponse response)
        {
        //
        // Reject explicit scheduling.
        if (request.getSchedule().size() > 0)
            {
            response.addMessagesItem(
                new WarnMessage(
                    "Execution schedule not supported"
                    )
                );
            return ;
            }
        //
        // Reject storage resources.
        if (request.getResources().getStorage().size() > 0)
            {
            response.addMessagesItem(
                new WarnMessage(
                    "Storage resources not supported"
                    )
                );
            return ;
            }
        //
        // Reject multiple compute resources.
        if (request.getResources().getCompute().size() > 1)
            {
            response.addMessagesItem(
                new WarnMessage(
                    "Multiple compute resources not supported"
                    )
                );
            return ;
            }

        //
        // Create our processing context.
        ProcessingContext context = new ProcessingContext(
            baseurl,
            request,
            response
            );

        //
        // Validate our data resources.
        for (AbstractDataResource resource : request.getResources().getData())
            {
            validate(
                resource,
                context
                );
            }

        //
        // Validate our compute resources.
        for (AbstractComputeResource resource : request.getResources().getCompute())
            {
            validate(
                resource,
                context
                );
            }

        //
        // Validate our executable.
        validate(
            request.getExecutable(),
            context
            );

        //
        // Check if everything worked.
        if (context.valid())
            {
            //
            // Get a list of execution blocks.
            for (ExecutionBlock block : getExecutionBlockList(context))
                {
                //
                // Create an offer using the resources in our context.
                ExecutionResponseImpl offer = new ExecutionResponseImpl(baseurl, response);
                this.insert(
                    offer
                    );
                offer.setName(
                    request.getName()
                    );
                offer.setExecutable(
                    context.getExecutable()
                    );
                // Transfer start time and duration from the offer block.
/*
                offer.setStartTime(
                    block.getBlockStart()
                    );
                offer.setDuration(
                    block.getBlockLength()
                    );
 */
                ExecutionResourceList resources = new ExecutionResourceList();
                for (AbstractDataResource resource : context.getDataResourceList())
                    {
                    resources.addDataItem(
                        resource
                        );
                    }
                for (AbstractComputeResource resource : context.getComputeResourceList())
                    {
                    // Transfer cores and memory from the offer.
                    // Data model shape mismatch.
                    // This assumes we only have one SimpleComputeResource.
                    if (resource instanceof SimpleComputeResource)
                        {
                        SimpleComputeResource simple = (SimpleComputeResource) resource ;
                        SimpleComputeResource albert = new SimpleComputeResource(
                            "urn:simple-compute-resource"
                            );
                        albert.setUuid(
                            simple.getUuid()
                            );
                        albert.setName(
                            simple.getName()
                            );
                        if (albert.getCores() == null)
                            {
                            albert.setCores(
                                new MinMaxInteger()
                                );
                            }
                        albert.getCores().setMin(
                            block.getMinCores()
                            );
                        albert.getCores().setMax(
                            block.getMaxCores()
                            );
                        if (albert.getMemory() == null)
                            {
                            albert.setMemory(
                                new MinMaxInteger()
                                );
                            }
                        albert.getMemory().setMin(
                            block.getMinMemory()
                            );
                        albert.getMemory().setMax(
                            block.getMaxMemory()
                            );
                        albert.setVolumes(
                            simple.getVolumes()
                            );
                        resources.addComputeItem(
                            albert
                            );
                        }
                    }
                offer.setResources(
                    resources
                    );
                response.addOffersItem(
                    offer
                    );
                response.setResult(
                    OfferSetResponse.ResultEnum.YES
                    );
                }
            }
        }

    /**
     * Update an Execution.
     * TODO Add an UpdateResponse, which holds messages about the update.
     * TODO Add an ErrorResponse, which holds messages about any errors.
     *
     */
    @Override
    public ExecutionResponse update(final UUID uuid, final AbstractUpdate request)
        {
        ExecutionResponseImpl response = this.select(uuid);
        if (response != null)
            {
            switch(request)
                {
                case EnumValueUpdate update :
                    this.update(
                        response,
                        update
                        );
                    break ;

                default:
                    // We need to be able to return some error messages here.
                    // We need an ErrorResponse structure ..
                    // This is an invalid request.
                    break;
                }
            }
        response.updateOptions();
        return response ;
        }

    /**
     * Update an Execution.
     *
     */
    protected void update(final ExecutionResponseImpl response, final EnumValueUpdate update)
        {
        switch(update.getPath())
            {
            case "state" :
                StateEnum currentstate = response.getState();
                StateEnum updatestate  = currentstate;
                try {
                    updatestate = StateEnum.fromValue(
                        update.getValue()
                        );
                    }
                catch (IllegalArgumentException ouch)
                    {
                    // Unknown state.
                    }
                if (updatestate != currentstate)
                    {
                    switch(currentstate)
                        {
                        case OFFERED :
                            switch(updatestate)
                                {
                                case ACCEPTED:
                                    response.setState(StateEnum.ACCEPTED);
// TODO null expiry time
                                    break;
                                case REJECTED:
                                    response.setState(StateEnum.REJECTED);
                                    break;
                                default:
                                    // Invalid state transition.
                                    break;
                                }
                            break;

                        default:
                            // Invalid state transition.
                            break;
                        }
                    }
                break;

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                // This is an invalid request.
                break;
            }
        }

    /**
     * Validate an AbstractDataResource.
     *
     */
    public void validate(final AbstractDataResource request, final ProcessingContext context)
        {
        switch(request)
            {
            case SimpleDataResource simple :
                validate(
                    simple,
                    context
                    );
                break;

            case S3DataResource s3 :
                validate(
                    s3,
                    context
                    );
                break;

            default:
                context.addMessage(
                    new WarnMessage(
                        "Unknown data resource type [${type}]",
                        Map.of(
                            "type",
                            safeString(request.getType())
                            )
                        )
                    );
                context.fail();
                break;
            }
        }

    /**
     * Validate a SimpleDataResource.
     *
     */
    public void validate(final SimpleDataResource request, final ProcessingContext context)
        {
        // Create a new DataResource.
        SimpleDataResource result = new SimpleDataResource(
            "urn:simple-data-resource"
            );
        if (request.getUuid() != null)
            {
            result.setUuid(
                request.getUuid()
                );
            }
        else {
            result.setUuid(
                UuidCreator.getTimeBased()
                );
            }
        result.setName(
            request.getName()
            );
        // Check for empty location.
        String location = request.getLocation();
        if (location == null)
            {
            location = "";
            }
        location = location.trim();
        if (location.isEmpty())
            {
            context.addMessage(
                new ErrorMessage(
                    "Missing location for data resource [${name}][${uuid}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid())
                        )
                    )
                );
            context.fail();
            }
        else {
            result.setLocation(
                location
                );
            }
        // Add the resource to our context.
        context.addDataResource(
            result
            );
        }

    /**
     * Validate a S3DataResource.
     *
     */
    public void validate(final S3DataResource request, final ProcessingContext context)
        {
        // Create a new DataResource.
        SimpleDataResource result = new SimpleDataResource(
            "urn:simple-data-resource"
            );
        if (request.getUuid() != null)
            {
            result.setUuid(
                request.getUuid()
                );
            }
        else {
            result.setUuid(
                UuidCreator.getTimeBased()
                );
            }
        result.setName(
            request.getName()
            );
        //
        // ....
        //
        // Add the resource to our context.
        context.addDataResource(
            result
            );
        }

    /**
     * Validate an AbstractComputeResource.
     *
     */
    public void validate(final AbstractComputeResource request, final ProcessingContext context)
        {
        switch(request)
            {
            case SimpleComputeResource simple :
                validate(
                    simple,
                    context
                    );
                break;

            default:
                context.addMessage(
                    new WarnMessage(
                        "Unknown compute resource type [${type}]",
                        Map.of(
                            "type",
                            safeString(request.getType())
                            )
                        )
                    );
                context.fail();
                break;
            }
        }

    /**
     * Validate a SimpleComputeResource.
     *
     */
    public void validate(final SimpleComputeResource request, final ProcessingContext context)
        {
        SimpleComputeResource result = new SimpleComputeResource(
            "urn:simple-compute-resource"
            );
        if (request.getUuid() != null)
            {
            result.setUuid(
                request.getUuid()
                );
            }
        else {
            result.setUuid(
                UuidCreator.getTimeBased()
                );
            }
        result.setName(
            request.getName()
            );
        //
        // Validate the compute resource itself.
        Integer mincores = null ;
        Integer maxcores = null ;
        Integer DEFAULT_MIN_CORES = 4 ;
        Integer DEFAULT_MAX_CORES = 8 ;
        Integer MIN_CORES_LIMIT = 2 ;
        Integer MAX_CORES_LIMIT = 16 ;

        String coreunits = null;
        String DEFAULT_CORE_UNITS = "cores" ;

        if (request.getCores() != null)
            {
            mincores  = request.getCores().getMin();
            maxcores  = request.getCores().getMax();
            coreunits = request.getCores().getUnits();
            }
        if (coreunits == null)
            {
            coreunits = DEFAULT_CORE_UNITS;
            }
        if (DEFAULT_CORE_UNITS.equals(coreunits) == false)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] unknown core units [${coreunits}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "coreunits",
                        safeString(coreunits)
                        )
                    )
                );
            context.fail();
            }

        if (mincores == null)
            {
            mincores = DEFAULT_MIN_CORES;
            }
        if (maxcores == null)
            {
            maxcores = DEFAULT_MAX_CORES;
            }
        if (mincores > MAX_CORES_LIMIT)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] minimum cores exceeds available resources [${mincores}][${limit}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "mincores",
                        safeString(mincores),
                        "limit",
                        safeString(MAX_CORES_LIMIT)
                        )
                    )
                );
            context.fail();
            }
        if (maxcores > MAX_CORES_LIMIT)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] maximum cores exceeds available resources [${maxcores}][${limit}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "maxcores",
                        safeString(maxcores),
                        "limit",
                        safeString(MAX_CORES_LIMIT)
                        )
                    )
                );
            context.fail();
            }
        if (mincores > maxcores)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] minimum cores exceeds maximum [${mincores}][${maxcores}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "mincores",
                        safeString(mincores),
                        "maxcores",
                        safeString(maxcores)
                        )
                    )
                );
            context.fail();
            }
        result.setCores(
            new MinMaxInteger()
            );
        result.getCores().setMin(
            mincores
            );
        result.getCores().setMax(
            maxcores
            );
        result.getCores().setUnits(
            coreunits
            );
        context.addMinCores(
            mincores
            );
        context.addMaxCores(
            maxcores
            );

        Integer minmemory = null ;
        Integer maxmemory = null ;
        Integer DEFAULT_MIN_MEMORY = 4 ;
        Integer DEFAULT_MAX_MEMORY = 8 ;
        Integer MIN_MEMORY_LIMIT = 2 ;
        Integer MAX_MEMORY_LIMIT = 16 ;

        String memoryunits = null;
        String DEFAULT_MEMORY_UNITS = "GiB" ;

        if (request.getMemory() != null)
            {
            minmemory   = request.getMemory().getMin();
            maxmemory   = request.getMemory().getMax();
            memoryunits = request.getMemory().getUnits();
            }
        if (memoryunits == null)
            {
            memoryunits = DEFAULT_MEMORY_UNITS;
            }
        if (DEFAULT_MEMORY_UNITS.equals(memoryunits) == false)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] unknown memory units [${memoryunits}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "memoryunits",
                        safeString(memoryunits)
                        )
                    )
                );
            context.fail();
            }

        if (minmemory == null)
            {
            minmemory = DEFAULT_MIN_MEMORY;
            }
        if (maxmemory == null)
            {
            maxmemory = DEFAULT_MAX_MEMORY;
            }
        if (minmemory > MAX_MEMORY_LIMIT)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] minimum memory exceeds available resources [${minmemory}][${limit}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "minmemory",
                        safeString(minmemory),
                        "limit",
                        safeString(MAX_MEMORY_LIMIT)
                        )
                    )
                );
            context.fail();
            }
        if (maxmemory > MAX_MEMORY_LIMIT)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] maximum memory exceeds available resources [${maxmemory}][${limit}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "maxmemory",
                        safeString(maxmemory),
                        "limit",
                        safeString(MAX_MEMORY_LIMIT)
                        )
                    )
                );
            context.fail();
            }
        if (minmemory > maxmemory)
            {
            context.addMessage(
                new InfoMessage(
                    "Compute resource [${name}][${uuid}] minimum memory exceeds maximum [${minmemory}][${maxmemory}]",
                    Map.of(
                        "name",
                        safeString(request.getName()),
                        "uuid",
                        safeString(request.getUuid()),
                        "minmemory",
                        safeString(minmemory),
                        "maxmemory",
                        safeString(maxmemory)
                        )
                    )
                );
            context.fail();
            }
        result.setMemory(
            new MinMaxInteger()
            );
        result.getMemory().setMin(
            minmemory
            );
        result.getMemory().setMax(
            maxmemory
            );
        result.getMemory().setUnits(
            memoryunits
            );
        context.addMinMemory(
            minmemory
            );
        context.addMaxMemory(
            maxmemory
            );

        //
        // Process the network ports.
        // ....

        //
        // Process the volume mounts.
        for (ComputeResourceVolume oldvolume : request.getVolumes())
            {
            // Create a new volume
            ComputeResourceVolume newvolume = new ComputeResourceVolume();
            if (oldvolume.getUuid() != null)
                {
                newvolume.setUuid(
                    oldvolume.getUuid()
                    );
                }
            else {
                newvolume.setUuid(
                    UuidCreator.getTimeBased()
                    );
                }
            newvolume.setName(
                oldvolume.getName()
                );
            // Check for an empty path.
            String path = oldvolume.getPath();
            if (path == null)
                {
                path = "";
                }
            path = path.trim();
            if (path.isEmpty())
                {
                context.addMessage(
                    new ErrorMessage(
                        "Missing path for compute resource [${cname}][${cuuid}] volume [${vname}][${vuuid}]",
                        Map.of(
                            "cname",
                            safeString(request.getName()),
                            "cuuid",
                            safeString(request.getUuid()),
                            "vname",
                            safeString(oldvolume.getName()),
                            "vuuid",
                            safeString(oldvolume.getUuid())
                            )
                        )
                    );
                context.fail();
                }
            else {
                newvolume.setPath(
                    path
                    );
                }

            AbstractDataResource found = context.findDataResource(
                oldvolume.getResource()
                );
            if (found != null)
                {
                context.addMessage(
                    new InfoMessage(
                        "Found data resource [${rname}][${ruuid}] for volume [${vname}][${vuuid}]",
                        Map.of(
                            "rname",
                            safeString(found.getName()),
                            "ruuid",
                            safeString(found.getUuid()),
                            "vname",
                            safeString(newvolume.getName()),
                            "vuuid",
                            safeString(newvolume.getUuid())
                            )
                        )
                    );
                // Link the data resource to the volume.
                // TODO Make this an actual java reference.
                // VolumeImpl setResource(AbstractDataResource)
                newvolume.setResource(
                    safeString(found.getUuid())
                    );
                // Add the volume to our compute resource.
                result.addVolumesItem(
                    newvolume
                    );
                }
            else {
                context.addMessage(
                    new ErrorMessage(
                        "Unable to find data resource [${reference}] for volume [${name}][${uuid}]",
                        Map.of(
                            "name",
                            safeString(oldvolume.getName()),
                            "uuid",
                            safeString(oldvolume.getUuid()),
                            "reference",
                            safeString(oldvolume.getResource())
                            )
                        )
                    );
                context.fail();
                }
            }
        // Add the resource to our context.
        context.addComputeResource(
            result
            );
        }

    /**
     * Validate an AbstractExecutable.
     *
     */
    public void validate(final AbstractExecutable request, final ProcessingContext context)
        {
        switch(request)
            {
            case JupyterNotebook01 jupyter:
                validate(
                    jupyter,
                    context
                    );
                break;

            default:
                context.addMessage(
                    new WarnMessage(
                        "Unknown executable type [${type}]",
                        Map.of(
                            "type",
                            safeString(request.getType())
                            )
                        )
                    );
                context.fail();
                break;
            }
        }

    /**
     * Validate a JupyterNotebook Executable.
     *
     */
    public void validate(final JupyterNotebook01 request, final ProcessingContext context)
        {
        JupyterNotebook01 result = new JupyterNotebook01(
            "urn:jupyter-notebook-0.1"
            );
        if (request.getUuid() != null)
            {
            result.setUuid(
                request.getUuid()
                );
            }
        else {
            result.setUuid(
                UuidCreator.getTimeBased()
                );
            }
        result.setName(
            request.getName()
            );

        String notebook = request.getNotebook();
        if ((notebook != null) && (notebook.trim().isEmpty()))
            {
            context.addMessage(
                new ErrorMessage(
                    "Null or empty notebook location"
                    )
                );
            context.fail();
            }
        else {
            result.setNotebook(
                notebook.trim()
                );
            }
        context.setExecutable(
            result
            );
        }
    }



