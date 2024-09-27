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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval ;

import com.github.f4b6a3.uuid.UuidCreator;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaComputeResourceVolume;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse.StateEnum;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook01;
import net.ivoa.calycopis.openapi.model.IvoaMinMaxInteger;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaS3DataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlockItem;
import net.ivoa.calycopis.openapi.model.IvoaStringScheduleBlockValue;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.InfoMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;

@Slf4j
@Component
public class ExecutionResponseFactoryImpl
    extends FactoryBase
    implements ExecutionResponseFactory
    {

    private ExecutionBlockDatabase database ;

    @Autowired
    public ExecutionResponseFactoryImpl(final ExecutionBlockDatabase database)
        {
        super();
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
    public void create(final String baseurl, final IvoaOfferSetRequest request, final OfferSetAPI offerset, final ProcessingContext<?> context)
        {
        log.debug("Processing a new OfferSetRequest and OfferSetResponse");
        //
        // Reject storage resources.
        if (request.getResources() != null)
            {
            if (request.getResources().getStorage() != null)
                {
                if (request.getResources().getStorage().size() > 0)
                    {
                    offerset.addMessage(
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
                    offerset.addMessage(
                        new WarnMessage(
                            "Multiple compute resources not supported"
                            )
                        );
                    return ;
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
        if (request.getResources().getCompute().size() < 1)
            {
            IvoaSimpleComputeResource compute = new IvoaSimpleComputeResource(
                "urn:simple-compute-resource"
                );
            compute.setUuid(
                UuidCreator.getTimeBased()
                );
            compute.setName(
                "Simple compute resource"
                );
            request.getResources().addComputeItem(
                compute
                );
            }
        //
        // Validate our execution schedule.
        validate(
            request.getSchedule(),
            context
            );
        //
        // Validate our executable.
        validate(
            request.getExecutable(),
            context
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
                    validate(
                        resource,
                        context
                        );
                    }
                }
            //
            // Validate our compute resources.
            if (request.getResources().getCompute() != null)
                {
                for (@Valid IvoaAbstractComputeResource resource : request.getResources().getCompute())
                    {
                    validate(
                        resource,
                        context
                        );
                    }
                }
            }

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
                ExecutionResponseImpl offer = new ExecutionResponseImpl(
                    IvoaExecutionResponse.StateEnum.OFFERED,
                    baseurl,
                    context.getOfferSet(),
                    context.getExecution()
                    );
                this.insert(
                    offer
                    );
                block.setOfferUuid(
                    offer.getUuid()
                    );
                block.setParentUuid(
                    offerset.getUuid()
                    );
                block.setState(
                    IvoaExecutionResponse.StateEnum.OFFERED
                    );
                block.setExpiryTime(
                    offerset.getExpires().toInstant()
                    );
                offer.setName(
                    request.getName()
                    );
                offer.setExecutable(
                    context.getExecutable()
                    );
                if (offer.getSchedule() == null)
                    {
                    offer.setSchedule(
                        new IvoaStringScheduleBlock()
                        );
                    }
                if (offer.getSchedule().getOffered() == null)
                    {
                    offer.getSchedule().setOffered(
                        new IvoaStringScheduleBlockItem()
                        );
                    }
                if (offer.getSchedule().getOffered().getExecuting() == null)
                    {
                    offer.getSchedule().getOffered().setExecuting(
                        new IvoaStringScheduleBlockValue()
                        );
                    }
                offer.getSchedule().getOffered().getExecuting().setStart(
                    OffsetDateTime.ofInstant(
                        block.getInstant(),
                        ZoneId.systemDefault()
                        ).toString()
                    );
                offer.getSchedule().getOffered().getExecuting().setDuration(
                    block.getDuration().toString()
                    );

                IvoaExecutionResourceList resources = new IvoaExecutionResourceList();
                for (IvoaAbstractDataResource resource : context.getDataResourceList())
                    {
                    resources.addDataItem(
                        resource
                        );
                    }
                for (IvoaAbstractComputeResource resource : context.getComputeResourceList())
                    {
                    // Transfer cores and memory from the offer.
                    // Data model shape mismatch.
                    // This assumes we only have one SimpleComputeResource.
                    if (resource instanceof IvoaSimpleComputeResource)
                        {
                        IvoaSimpleComputeResource simple = (IvoaSimpleComputeResource) resource ;
                        // Offer the same minimum as the request.
                        block.setMinCores(
                            simple.getCores().getMin()
                            );
                        // Offer twice the requested maximum IF that is still less than the block.
                        if ((simple.getCores().getMax() * 2) < block.getMaxCores())
                            {
                            block.setMaxCores(
                                simple.getCores().getMax() * 2
                                );
                            }
                        // Offer the same minimum as the request.
                        block.setMinMemory(
                            simple.getMemory().getMin()
                            );
                        // Offer twice the requested maximum IF that is still less than the block.
                        if ((simple.getMemory().getMax() * 2) < block.getMaxMemory())
                            {
                            block.setMaxMemory(
                                simple.getMemory().getMax() * 2
                                );
                            }
                        IvoaSimpleComputeResource result = new IvoaSimpleComputeResource(
                            "urn:simple-compute-resource"
                            );
                        result.setUuid(
                            simple.getUuid()
                            );
                        result.setName(
                            simple.getName()
                            );
                        if (result.getCores() == null)
                            {
                            result.setCores(
                                new IvoaMinMaxInteger()
                                );
                            }
                        result.getCores().setMin(
                            block.getMinCores()
                            );
                        result.getCores().setMax(
                            block.getMaxCores()
                            );
                        if (result.getMemory() == null)
                            {
                            result.setMemory(
                                new IvoaMinMaxInteger()
                                );
                            }
                        result.getMemory().setMin(
                            block.getMinMemory()
                            );
                        result.getMemory().setMax(
                            block.getMaxMemory()
                            );
                        result.setVolumes(
                            simple.getVolumes()
                            );
                        // Add the SimpleComputeResource to our response.
                        resources.addComputeItem(
                            result
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
                offerset.addOffer(
                    offer
                    );
                offerset.setResult(
                        IvoaOfferSetResponse.ResultEnum.YES
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
    public IvoaExecutionResponse update(final UUID uuid, final IvoaAbstractUpdate request)
        {
        ExecutionResponseImpl response = this.select(uuid);
        if (response != null)
            {
            switch(request)
                {
                case IvoaEnumValueUpdate update :
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
            response.updateOptions();
            }
        return response ;
        }

    /**
     * Update an Execution.
     *
     */
    protected void update(final ExecutionResponseImpl response, final IvoaEnumValueUpdate update)
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
                //
                // If this is a change.
                if (updatestate != currentstate)
                    {
                    switch(currentstate)
                        {
                        case OFFERED :
                            switch(updatestate)
                                {
                                case ACCEPTED:
                                    response.setState(
                                        StateEnum.ACCEPTED
                                        );
                                    response.getParent().setAccepted(
                                        response
                                        );
                                    for (IvoaExecutionResponse sibling : response.getParent().getOffers())
                                        {
                                        if (sibling != response)
                                            {
                                            sibling.setState(
                                                StateEnum.REJECTED
                                                );
                                            }
                                        }
                                    database.accept(
                                        response.getUuid()
                                        );
                                    break;
                                case REJECTED:
                                    response.setState(
                                        StateEnum.REJECTED
                                        );
                                    database.reject(
                                        response.getUuid()
                                        );
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
    public void validate(final IvoaAbstractDataResource request, final ProcessingContext<?> context)
        {
        switch(request)
            {
            case IvoaSimpleDataResource simple :
                validate(
                    simple,
                    context
                    );
                break;

            case IvoaS3DataResource s3 :
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
    public void validate(final IvoaSimpleDataResource request, final ProcessingContext<?> context)
        {
        // Create a new DataResource.
        IvoaSimpleDataResource result = new IvoaSimpleDataResource(
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
    public void validate(final IvoaS3DataResource request, final ProcessingContext<?> context)
        {
        // Create a new DataResource.
        IvoaS3DataResource result = new IvoaS3DataResource(
            "urn:s3-data-resource"
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
    public void validate(final IvoaAbstractComputeResource request, final ProcessingContext<?> context)
        {
        switch(request)
            {
            case IvoaSimpleComputeResource simple :
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
    public void validate(final IvoaSimpleComputeResource request, final ProcessingContext<?> context)
        {
        IvoaSimpleComputeResource result = new IvoaSimpleComputeResource(
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
        Integer MIN_CORES_DEFAULT = 1 ;
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
            mincores = MIN_CORES_DEFAULT;
            }
        if (maxcores == null)
            {
            maxcores = mincores;
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
            new IvoaMinMaxInteger()
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
        Integer MIN_MEMORY_DEFAULT = 1 ;
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
            minmemory = MIN_MEMORY_DEFAULT;
            }
        if (maxmemory == null)
            {
            maxmemory = minmemory;
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
            new IvoaMinMaxInteger()
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
        for (IvoaComputeResourceVolume oldvolume : request.getVolumes())
            {
            // Create a new volume
            IvoaComputeResourceVolume newvolume = new IvoaComputeResourceVolume();
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

            IvoaAbstractDataResource found = context.findDataResource(
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
    public void validate(final IvoaAbstractExecutable request, final ProcessingContext<?> context)
        {
        switch(request)
            {
            case IvoaJupyterNotebook01 jupyter:
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
    public void validate(final IvoaJupyterNotebook01 request, final ProcessingContext<?> context)
        {
        log.debug("Validating a JupyterNotebook request [{}]", request.getName());
        IvoaJupyterNotebook01 result = new IvoaJupyterNotebook01(
            "urn:jupyter-notebook-0.1"
            );
        result.setUuid(
            UuidCreator.getTimeBased()
            );
        result.setName(
            request.getName()
            );
        //
        // TODO
        // Validate the notebook schedule ..
        //

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
/*
 * TODO Add the processing step .. in setExecutable()
        CanfarNotebookPreparationStep step = factory.createNotebookStep(
            (CanfarExecution) parent,
            result
            );
 *
 */
        }

    /**
     * Validate the Execution Schedule.
     * TODO Move this to the time classes.
     */
    public void validate(final IvoaStringScheduleBlock schedule, final ProcessingContext<?> context)
        {
        log.debug("Processing StringScheduleBlock");
        if (schedule != null)
            {
            //
            // Check the offered section is empty.
            // ....
            // Check the observed section is empty.
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
                            context.addMessage(
                                new ErrorMessage(
                                    "Unable to parse start interval [${value}][${message}]",
                                    Map.of(
                                        "value",
                                        safeString(string),
                                        "message",
                                        safeString(ouch.getMessage())
                                        )
                                    )
                                );
                            context.fail();
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
                            context.addMessage(
                                new ErrorMessage(
                                    "Unable to parse duration [${value}][${message}]",
                                    Map.of(
                                        "value",
                                        safeString(string),
                                        "message",
                                        safeString(ouch.getMessage())
                                        )
                                    )
                                );
                            context.fail();
                            }
                        }
                    context.setPreparationTime(
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
                            context.addMessage(
                                new ErrorMessage(
                                    "Unable to parse start interval [${value}][${message}]",
                                    Map.of(
                                        "value",
                                        safeString(string),
                                        "message",
                                        safeString(ouch.getMessage())
                                        )
                                    )
                                );
                            context.fail();
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
                            context.addMessage(
                                new ErrorMessage(
                                    "Unable to parse duration [${value}][${message}]",
                                    Map.of(
                                        "value",
                                        safeString(string),
                                        "message",
                                        safeString(ouch.getMessage())
                                        )
                                    )
                                );
                            context.fail();
                            }
                        }
                    context.setExecutionTime(
                        execstart,
                        exectime
                        );
                    }
                }
            }
        }
    }



