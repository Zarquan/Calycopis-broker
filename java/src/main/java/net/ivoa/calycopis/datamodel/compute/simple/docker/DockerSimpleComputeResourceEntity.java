/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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

package net.ivoa.calycopis.datamodel.compute.simple.docker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.HostConfig;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainer;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.platfom.docker.DockerClientFactory;
import net.ivoa.calycopis.functional.platfom.docker.DockerPlatform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 * A Docker SimpleComputeResource entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "dockersimplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:docker-simple-compute-resources"
    )
public class DockerSimpleComputeResourceEntity
extends SimpleComputeResourceEntity
implements DockerSimpleComputeResource
    {

    /**
     * Protected constructor.
     *
     */
    protected DockerSimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with session, validation result, and offer.
     *
     */
    public DockerSimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final DockerSimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        super(
            session,
            result,
            offer,
            (IvoaSimpleComputeResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with session, template and offer.
     * 
     */
    public DockerSimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer,
        final IvoaSimpleComputeResource validated
        ){
        super(
            session,
            result,
            offer,
            validated
            );
        }

    @Column(name="dockercontainerid")
    private String dockerContainerId;
    public String getDockerContainerId()
        {
        return this.dockerContainerId;
        }

    @Column(name="dockercontainerexitcode")
    private Integer dockerContainerExitCode;
    public Integer getDockerContainerExitCode()
        {
        return this.dockerContainerExitCode;
        }

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        // Eagerly resolve all data from the Hibernate session while still inside the transaction.
        final UUID resourceUuid = this.getUuid();
        final String resourceClassName = this.getClass().getSimpleName();
        final Long maxCores = this.getMaxOfferedCores();
        final Long maxMemory = this.getMaxOfferedMemory();

        final AbstractExecutableEntity executable = this.session.getExecutable();
        final String imageName;
        final List<String> envList = new ArrayList<String>();
        final List<String> cmdList = new ArrayList<String>();

        if (executable instanceof DockerContainerEntity)
            {
            DockerContainerEntity dockerExecutable = (DockerContainerEntity) executable;
            DockerContainer.DockerContainerImage image = dockerExecutable.getImage();
            // TODO We should iterate the list rather than just taking the first one.
            if (image != null && image.getLocations() != null && !image.getLocations().isEmpty())
                {
                imageName = image.getLocations().get(0);
                }
            else
                {
                imageName = null;
                // TODO fail the prepare step
                return ProcessingAction.NO_ACTION;
                }
            Map<String, String> environment = dockerExecutable.getEnvironment();
            if (environment != null)
                {
                for (Map.Entry<String, String> entry : environment.entrySet())
                    {
                    envList.add(entry.getKey() + "=" + entry.getValue());
                    }
                }
            List<String> command = dockerExecutable.getCommand();
            if (command != null)
                {
                cmdList.addAll(command);
                }
            }
        else
            {
            imageName = null;
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }

        final DockerClientFactory clientFactory ;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            clientFactory = null;
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform]",
                platform.getClass().getSimpleName()
                );
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }
        
        return new ProcessingAction()
            {

            private String containerId;
            private boolean processSucceeded = false;

            @Override
            public boolean process()
                {
                log.debug(
                    "Preparing Docker container [{}][{}]",
                    resourceUuid,
                    resourceClassName
                    );

                if (!(executable instanceof DockerContainerEntity))
                    {
                    String executableType;
                    if (executable != null)
                        {
                        executableType = executable.getClass().getSimpleName();
                        }
                    else {
                        executableType = "null";
                        }
                    log.error(
                        "Session executable is not a DockerContainerEntity [{}]",
                        executableType
                        );
                    this.processSucceeded = false;
                    return false;
                    }

                if (imageName == null)
                    {
                    log.error(
                        "No image location found for Docker executable [{}]",
                        executable.getUuid()
                        );
                    this.processSucceeded = false;
                    return false;
                    }

                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "CONTAINER_HOST / DOCKER_HOST environment variable is not set"
                            );
                        this.processSucceeded = false;
                        return false;
                        }

                    //
                    // Pull the Docker image.
                    log.debug(
                        "Pulling Docker image [{}]",
                        imageName
                        );
                    // TODO The image should already have been pulled into the local cache by the DockerDockerContainerEntity prepare phase.
                    // TODO We should check that the image has already been cached and issue a warning if it hasn't.
                    dockerClient.pullImageCmd(imageName)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion();

                    //
                    // Configure host resource limits based on offered cores and memory.
                    // TODO also check the minCores and MinMemory.
                    HostConfig hostConfig = HostConfig.newHostConfig();
                    if (maxCores != null)
                        {
                        long nanoCpus = maxCores * 1_000_000_000L;
                        hostConfig.withNanoCPUs(nanoCpus);
                        }
                    if (maxMemory != null)
                        {
                        long memoryBytes = maxMemory * 1_073_741_824L;
                        hostConfig.withMemory(memoryBytes);
                        }

                    //
                    // Create the container.
                    log.debug(
                        "Creating Docker container from image [{}]",
                        imageName
                        );
                    var createCmd = dockerClient.createContainerCmd(imageName)
                        .withEnv(envList)
                        .withHostConfig(hostConfig);
                    if (!cmdList.isEmpty())
                        {
                        createCmd.withCmd(cmdList);
                        }
                    CreateContainerResponse container = createCmd.exec();
                    this.containerId = container.getId();

                    //
                    // Start the container.
                    log.debug(
                        "Starting Docker container [{}]",
                        this.containerId
                        );
                    dockerClient.startContainerCmd(this.containerId).exec();

                    log.debug(
                        "Docker container started [{}] for resource [{}]",
                        this.containerId,
                        resourceUuid
                        );
                    this.processSucceeded = true;
                    return true;
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to prepare Docker container [{}]",
                        resourceUuid,
                        e
                        );
                    this.processSucceeded = false;
                    return false;
                    }
                }

            @Override
            public UUID getRequestUuid()
                {
                return request.getUuid();
                }

            @Override
            public IvoaLifecyclePhase getNextPhase()
                {
                if (this.processSucceeded)
                    {
                    return IvoaLifecyclePhase.AVAILABLE;
                    }
                else {
                    return IvoaLifecyclePhase.FAILED;
                    }
                }
            
            @Override
            public boolean postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    return postProcess(
                        (DockerSimpleComputeResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected component type [{}] post processing [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    return false;
                    }
                }
                
            public boolean postProcess(final DockerSimpleComputeResourceEntity component)
                {
                log.debug(
                    "Post processing Docker resource [{}][{}] container [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.containerId
                    );
                component.dockerContainerId = this.containerId;
                return true;
                }
            };
        }

    @Override
    public ProcessingAction getMonitorAction(final Platform platform, final ComponentProcessingRequest request)
        {
        final UUID resourceUuid = this.getUuid();
        final String resourceClassName = this.getClass().getSimpleName();
        final String containerId = this.dockerContainerId;

        if (containerId == null || containerId.isEmpty())
            {
            log.error(
                "No Docker container ID for resource [{}][{}]",
                resourceUuid,
                resourceClassName
                );
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }

        final DockerClientFactory clientFactory ;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            clientFactory = null;
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform]",
                platform.getClass().getSimpleName()
                );
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }
        
        return new ProcessingAction()
            {
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.RUNNING;
            private Integer exitCode;

            @Override
            public boolean process()
                {
                log.debug(
                    "Monitoring Docker container [{}] for resource [{}][{}]",
                    containerId,
                    resourceUuid,
                    resourceClassName
                    );
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "CONTAINER_HOST / DOCKER_HOST environment variable is not set"
                            );
                        this.nextPhase = IvoaLifecyclePhase.FAILED;
                        return false;
                        }

                    InspectContainerResponse inspection = dockerClient.inspectContainerCmd(containerId).exec();
                    InspectContainerResponse.ContainerState state = inspection.getState();

                    log.debug(
                        "Container [{}] state: status=[{}] running=[{}] exitCode=[{}]",
                        containerId,
                        state.getStatus(),
                        state.getRunning(),
                        state.getExitCode()
                        );

                    if (Boolean.TRUE.equals(state.getRunning()))
                        {
                        this.nextPhase = IvoaLifecyclePhase.RUNNING;
                        }
                    else {
                        this.exitCode = state.getExitCode();
                        if (this.exitCode != null && this.exitCode == 0)
                            {
                            this.nextPhase = IvoaLifecyclePhase.RELEASING;
                            }
                        else {
                            log.warn(
                                "Container [{}] exited with non-zero code [{}]",
                                containerId,
                                this.exitCode
                                );
                            this.nextPhase = IvoaLifecyclePhase.FAILED;
                            }
                        }
                    return true;
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to inspect Docker container [{}] for resource [{}]",
                        containerId,
                        resourceUuid,
                        e
                        );
                    this.nextPhase = IvoaLifecyclePhase.FAILED;
                    return false;
                    }
                }

            @Override
            public UUID getRequestUuid()
                {
                return request.getUuid();
                }

            @Override
            public IvoaLifecyclePhase getNextPhase()
                {
                return this.nextPhase;
                }

            @Override
            public boolean postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Monitor post-processing [{}][{}] next phase [{}] exit code [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.nextPhase,
                    this.exitCode
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    ((DockerSimpleComputeResourceEntity) component).dockerContainerExitCode = this.exitCode;
                    }
                return true;
                }
            };
        }
    }
