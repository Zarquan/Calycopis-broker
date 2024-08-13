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
    private Map<UUID, ExecutionResponse> hashmap = new HashMap<UUID, ExecutionResponse>();

    /**
     * Insert an ExecutionResponse into our HashMap.
     *
     */
    protected void insert(final ExecutionResponse execution)
        {
        this.hashmap.put(
            execution.getUuid(),
            execution
            );
        }

    /**
     * Select an ExecutionResponse based on its identifier.
     *
     */
    @Override
    public ExecutionResponse select(final UUID uuid)
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
     *
     */
    public class ProcessingContext
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

        public ProcessingContext(final OfferSetRequest request, final OfferSetResponse response)
            {
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

        }

    /**
     * Process an OfferSetRequest and populate an OfferSetResponse with ExecutionResponse offers.
     *
     */
    public void process(final OfferSetRequest request, final OfferSetResponse response)
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
            // Transfer results from context to response.
            ExecutionResponseImpl execution = new ExecutionResponseImpl(response);
            this.insert(
                execution
                );
            execution.setName(
                request.getName()
                );
            execution.setExecutable(
                context.getExecutable()
                );
            ExecutionResourceList resourcelist = new ExecutionResourceList();
            for (AbstractDataResource resource : context.getDataResourceList())
                {
                resourcelist.addDataItem(
                    resource
                    );
                }
            for (AbstractComputeResource resource : context.getComputeResourceList())
                {
                resourcelist.addComputeItem(
                    resource
                    );
                }
            execution.setResources(
                resourcelist
                );
            response.addExecutionsItem(
                execution
                );
            response.setResult(
                OfferSetResponse.ResultEnum.YES
                );
// TODO
// The execution needs to have href set.
//
// Change SimpleDataResource to SimpleHttpResource ?

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
// cores < max
// memory < max

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
        JupyterNotebook01 result = new JupyterNotebook01();
        result.setType(
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

        //
        // ....
        //
        context.setExecutable(
            result
            );
        }
    }



