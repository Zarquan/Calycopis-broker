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
import net.ivoa.calycopis.functional.processing.SimpleReleaseAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingAction;
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
        
        return new ComponentProcessingAction()
            {

            private String containerId;
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.AVAILABLE;

            @Override
            public void preProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Pre-processing component [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                }
            
            @Override
            public void process()
                {
                log.debug(
                    "Preparing Docker container [{}][{}]",
                    resourceUuid,
                    resourceClassName
                    );

                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "Unable to create Docker client. CONTAINER_HOST / DOCKER_HOST environment variable may not be set"
                            );
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        // TODO Add some messages to explain why.
                        return ;
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
                    boolean hasResourceLimits = false;
                    if (maxCores != null)
                        {
                        long nanoCpus = maxCores * 1_000_000_000L;
                        hostConfig.withNanoCPUs(nanoCpus);
                        hasResourceLimits = true;
                        }
                    if (maxMemory != null)
                        {
                        long memoryBytes = maxMemory * 1_073_741_824L;
                        hostConfig.withMemory(memoryBytes);
                        hasResourceLimits = true;
                        }

                    //
                    // Create and start the container, retrying without resource limits
                    // if the cgroup controllers are not available (e.g. nested containers).
                    this.containerId = createAndStartContainer(
                        dockerClient,
                        imageName,
                        envList,
                        cmdList,
                        hostConfig,
                        resourceUuid
                        );
                    if (this.containerId == null && hasResourceLimits)
                        {
                        log.warn(
                            "Retrying without resource limits for [{}]",
                            resourceUuid
                            );
                        this.containerId = createAndStartContainer(
                            dockerClient,
                            imageName,
                            envList,
                            cmdList,
                            HostConfig.newHostConfig(),
                            resourceUuid
                            );
                        }

                    if (this.containerId != null)
                        {
                        log.debug(
                            "Docker container started [{}] for resource [{}]",
                            this.containerId,
                            resourceUuid
                            );
                        nextPhase = IvoaLifecyclePhase.AVAILABLE;
                        }
                    else
                        {
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        }
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to prepare Docker container [{}]",
                        resourceUuid,
                        e
                        );
                    nextPhase = IvoaLifecyclePhase.FAILED;
                    }
                }

            @Override
            public void postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    postProcess(
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
                    nextPhase = IvoaLifecyclePhase.FAILED;
                    // TODO Add some messages to explain why.
                    }
                }
                
            public void postProcess(final DockerSimpleComputeResourceEntity component)
                {
                log.debug(
                    "Post processing Docker compute resource [{}][{}] with container [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.containerId
                    );
                component.dockerContainerId = this.containerId;
                component.setPhase(
                    nextPhase
                    );
                }
            };
        }

    /**
     * Create and start a Docker container, returning the container ID on success or null on failure.
     */
    private String createAndStartContainer(
        final DockerClient dockerClient,
        final String imageName,
        final List<String> envList,
        final List<String> cmdList,
        final HostConfig hostConfig,
        final UUID resourceUuid
        )
        {
        try {
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
            String id = container.getId();

            log.debug(
                "Starting Docker container [{}]",
                id
                );
            dockerClient.startContainerCmd(id).exec();
            return id;
            }
        catch (Exception e)
            {
            log.warn(
                "Failed to create/start Docker container for [{}]: {}",
                resourceUuid,
                e.getMessage()
                );
            return null;
            }
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
            // TODO fail the component.
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
            // TODO fail the component.
            return ProcessingAction.NO_ACTION;
            }
        
        return new ComponentProcessingAction()
            {
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.RUNNING;
            private Integer exitCode;

            @Override
            public void preProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Pre-processing component [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                }

            @Override
            public void process()
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
                        return ;
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
                            // TODO Add some messages to explain why.
                            this.nextPhase = IvoaLifecyclePhase.FAILED;
                            }
                        }
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to inspect Docker container [{}] for resource [{}]",
                        containerId,
                        resourceUuid,
                        e
                        );
                    // TODO Add some messages to explain why.
                    this.nextPhase = IvoaLifecyclePhase.FAILED;
                    }
                }

            @Override
            public void postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Post-processing component [{}][{}] next phase [{}] exit code [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.nextPhase,
                    this.exitCode
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    postProcess(
                        (DockerSimpleComputeResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected type [{}] for post processing component [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    component.addError(
                        "uri:internal-error",
                        "Unexpected component type, see logs for details"
                        );
                    }
                }

            public void postProcess(final DockerSimpleComputeResourceEntity component)
                {
                component.dockerContainerExitCode = this.exitCode;
                component.setPhase( 
                    this.nextPhase
                    );
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(Platform platform, ComponentProcessingRequest request)
        {
        return new SimpleReleaseAction(
            this,
            10_000
            );
        }
    }
