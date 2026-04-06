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
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.functional.platfom.docker;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.compute.simple.docker.DockerSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.simple.docker.DockerSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.data.amazon.mock.MockAmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.amazon.mock.MockAmazonS3DataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.ivoa.mock.MockIvoaDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.ivoa.mock.MockIvoaDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.mock.MockDataStorageLinker;
import net.ivoa.calycopis.datamodel.data.simple.mock.MockSimpleDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.simple.mock.MockSimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.skao.mock.MockSkaoDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.skao.mock.MockSkaoDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.docker.DockerDockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.docker.DockerDockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.jupyter.mock.MockJupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.mock.MockJupyterNotebookValidatorImpl;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.storage.simple.mock.MockSimpleStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.simple.mock.MockSimpleStorageResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;

/**
 * 
 */
@Slf4j
@Component
@Profile("docker")
public class DockerPlatformImpl
extends FactoryBaseImpl
implements DockerPlatform
    {

    public DockerPlatformImpl()
        {
        super();
        }

    public void initialize()
        {
        log.debug("initialize()");
        this.executableValidatorFactory.addValidator(
            new MockJupyterNotebookValidatorImpl(
                this.jupyterNotebookEntityFactory
                )
            );
        this.executableValidatorFactory.addValidator(
            new DockerDockerContainerValidatorImpl(
                this
                )
            );
        this.computeResourceValidatorFactory.addValidator(
            new DockerSimpleComputeResourceValidatorImpl(
                this.computeResourceEntityFactory,
                this.volumeMountValidatorFactory
                )
            );
        this.storageResourceValidatorFactory.addValidator(
            new MockSimpleStorageResourceValidatorImpl(
                this.storageResourceEntityFactory
                )
            );

        //
        // Register data resource validators with the most specific types first.
        // The validator factory iterates through validators in registration order
        // and stops at the first one that returns ACCEPTED or FAILED. Although
        // each validator now uses exact class matching (getClass() ==) rather
        // than instanceof, registering specific subtypes before their parent
        // types provides defence in depth against future regressions.
        //
        // SkaoDataResource extends IvoaDataResource in the type hierarchy,
        // so the SKAO validator must be registered before the IVOA validator.
        //
        this.dataResourceValidatorFactory.addValidator(
            new MockSkaoDataResourceValidatorImpl(
                this.jdbcTemplate,
                this.mockSkaoDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new MockIvoaDataResourceValidatorImpl(
                this.mockIvoaDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new MockAmazonS3DataResourceValidatorImpl(
                this.mockAmazonS3DataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new MockSimpleDataResourceValidatorImpl(
                this.mockSimpleDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );

        this.registerFactory(this.dockerContainerEntityFactory);
        this.registerFactory(this.jupyterNotebookEntityFactory);
        this.registerFactory(this.computeResourceEntityFactory);
        this.registerFactory(this.dataResourceEntityFactory);
        this.registerFactory(this.storageResourceEntityFactory);
        
        }

// Docker client

    @Autowired
    private DockerClientFactory dockerClientFactory;
    @Override
    public DockerClientFactory getDockerClientFactory()
        {
        return this.dockerClientFactory;
        }

// Compute    
    
    @Autowired
    private ComputeResourceOfferFactory computeResourceOfferFactory;
    @Override
    public ComputeResourceOfferFactory getComputeResourceOfferFactory()
        {
        return this.computeResourceOfferFactory;
        }
    
    @Autowired
    private DockerSimpleComputeResourceEntityFactory computeResourceEntityFactory;

    @Autowired
    private AbstractComputeResourceValidatorFactory computeResourceValidatorFactory;
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.computeResourceValidatorFactory;
        }
    
// Data   

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockSimpleDataResourceEntityFactory mockSimpleDataResourceEntityFactory;

    @Autowired
    private MockAmazonS3DataResourceEntityFactory mockAmazonS3DataResourceEntityFactory;

    @Autowired
    private MockIvoaDataResourceEntityFactory mockIvoaDataResourceEntityFactory;

    @Autowired
    private MockSkaoDataResourceEntityFactory mockSkaoDataResourceEntityFactory;

    @Autowired
    private AbstractDataResourceEntityFactory dataResourceEntityFactory;
    @Override
    public AbstractDataResourceEntityFactory getDataResourceEntityFactory()
        {
        return this.dataResourceEntityFactory;
        }
    
    @Autowired
    private AbstractDataResourceValidatorFactory dataResourceValidatorFactory;
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.dataResourceValidatorFactory;
        }

// Executable    

    @Autowired
    private AbstractExecutableValidatorFactory executableValidatorFactory;
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.executableValidatorFactory;
        }
    
    @Autowired
    private DockerDockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    @Autowired
    private MockJupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    
// Storage

    @Autowired
    private MockSimpleStorageResourceEntityFactory storageResourceEntityFactory;

    @Autowired
    private AbstractStorageResourceValidatorFactory storageResourceValidatorFactory;
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.storageResourceValidatorFactory;
        }

    @Autowired
    private MockDataStorageLinker dataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.dataStorageLinker;
        }
    
// Volume
    
    @Autowired
    private AbstractVolumeMountValidatorFactory volumeMountValidatorFactory;
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.volumeMountValidatorFactory;
        }

// Session
    
    @Autowired
    private SimpleExecutionSessionEntityFactory executionSessionEntityFactory;
    @Override
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory()
        {
        return this.executionSessionEntityFactory;
        }

// Processing
    
    @Autowired
    private ProcessingRequestFactory processingRequestFactory;
    @Override
    public ProcessingRequestFactory getProcessingRequestFactory()
        {
        return this.processingRequestFactory;
        }

// LifecycleComponent

    @Autowired
    private AbstractLifecycleComponentEntityFactory lifecycleComponentEntityFactory;
    @Override
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory()
        {
        return this.lifecycleComponentEntityFactory;
        }
    
    Map<URI, LifecycleComponentEntityFactory<?>> registry = new HashMap<URI, LifecycleComponentEntityFactory<?>>();
    
    void registerFactory(
        final LifecycleComponentEntityFactory<?> factory
        ){
        this.registry.put(
            factory.getKind(),
            factory
            );
        }
    
    @Override
    public LifecycleComponentEntity select(final URI kind, final UUID uuid)
        {
        LifecycleComponentEntityFactory<?> factory = this.registry.get(kind);
        if (factory != null)
            {
            return factory.select(uuid).orElse(null);
            }
        else {
            return null;
            }
        }
    }
