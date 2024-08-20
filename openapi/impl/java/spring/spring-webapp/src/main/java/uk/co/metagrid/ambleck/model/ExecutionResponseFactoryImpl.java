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

import java.time.Instant;
import java.time.Duration;
import org.threeten.extra.Interval ;

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

    private ExecutionBlockDatabase database ;

    @Autowired
    public ExecutionResponseFactoryImpl(final ExecutionBlockDatabase database)
        {
        this.uuid = UuidCreator.getTimeBased();
        this.database = database ;
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
     * Process an OfferSetRequest and populate an OfferSetResponse with ExecutionResponse offers.
     *
     */
    @Override
    public void create(final String baseurl, final OfferSetRequest request, final OfferSetResponse response)
        {
        //
        // Reject storage resources.
        if (request.getResources() != null)
            {
            if (request.getResources().getStorage() != null)
                {
                if (request.getResources().getStorage().size() > 0)
                    {
                    response.addMessagesItem(
                        new WarnMessage(
                            "Storage resources not supported"
                            )
                        );
                    return ;
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
                    response.addMessagesItem(
                        new WarnMessage(
                            "Multiple compute resources not supported"
                            )
                        );
                    return ;
                    }
                }
            }

        //
        // Create our processing context.
        ProcessingContext context = new ProcessingContextImpl(
            baseurl,
            request,
            response
            );

        //
        // Validate our scheduling request.
        validate(
            request.getSchedule(),
            context
            );

        if (request.getResources() != null)
            {
            validate(
                request.getResources().getData()
                );
            validate(
                request.getResources().getCompute()
                );
            }

        //
        // Check for an enpty compute list.
        if (context.getComputeResourceList().isEmpty())
            {
            // Need to add a default compute resource.
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
            for (ExecutionBlock block : database.generate(context))
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
                        // Add the SimpleComputeResource to our response.
                        resources.addComputeItem(
                            albert
                            );
                        // Add the ExecutionBlock to our database.
                        database.insert(
                            block
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
     * Validate a list of data resources.
     *
     */
    public void validate(final List<AbstractDataResource> list, final ProcessingContext context)
        {
        if (list != null)
            {
            for (AbstractDataResource resource : request.getResources().getData())
                {
                validate(
                    resource,
                    context
                    );
                }
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
     * Validate our ComputeResource list.
     *
     */
    public void validate(final List<AbstractComputeResource> list, final ProcessingContext context)
        {
        if (request != null)
            {
            for (AbstractComputeResource resource : list)
                {
                validate(
                    resource,
                    context
                    );
                }
            }
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

    /**
     * Validate the Execution Schedule.
     *
     */
    public void validate(final ExecutionSchedule schedule, final ProcessingContext context)
        {
        if (schedule != null)
            {
            //
            // Check the offered section is empty.

            //
            // Check the observed section is empty.

            //
            // Validate each of our requested items.
            for (ScheduleRequestItem item : schedule.getRequested())
                {
                validate(
                    item,
                    context
                    );
                }
            }
        }

    /**
     * Validate a ScheduleRequestItem.
     *
     */
    public void validate(final ScheduleRequestItem item, final ProcessingContext context)
        {
        Interval starttime = null;
        Duration minduration = null;
        Duration maxduration = null;

        if (item.getStart() != null)
            {
            try {
                starttime = Interval.parse​(
                    item.getStart()
                    );
                }
            catch (Exception ouch)
                {
                context.addMessage(
                    new ErrorMessage(
                        "Unable to parse schedule request start time [${value}][${message}]",
                        Map.of(
                            "value",
                            safeString(item.getStart()),
                            "message",
                            safeString(ouch.getMessage())
                            )
                        )
                    );
                context.fail();
                }
            }

        if (item.getDuration() != null)
            {
            if (item.getDuration().getMin() != null)
                {
                String text = item.getDuration().getMin() ;
                try {
                    minduration = Duration.parse​(text);
                    }
                catch (Exception ouch)
                    {
                    context.addMessage(
                        new ErrorMessage(
                            "Unable to parse schedule request duration [${value}][${message}]",
                            Map.of(
                                "value",
                                safeString(text),
                                "message",
                                safeString(ouch.getMessage())
                                )
                            )
                        );
                    context.fail();
                    }
                }
            if (item.getDuration().getMax() != null)
                {
                String text = item.getDuration().getMax() ;
                try {
                    minduration = Duration.parse​(text);
                    }
                catch (Exception ouch)
                    {
                    context.addMessage(
                        new ErrorMessage(
                            "Unable to parse schedule request duration [${value}][${message}]",
                            Map.of(
                                "value",
                                safeString(text),
                                "message",
                                safeString(ouch.getMessage())
                                )
                            )
                        );
                    context.fail();
                    }
                }
            }
        context.addScheduleItem(
            starttime,
            minduration,
            maxduration
            );
        }
    }



