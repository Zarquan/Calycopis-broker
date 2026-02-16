/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.functional.platfom.mock;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.compute.simple.mock.MockSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.simple.mock.MockSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.mock.MockDockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.mock.MockDockerContainerValidatorImpl;
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
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequestFactory;

/**
 * 
 */
@Slf4j
@Component
public class MockPlatformImpl
extends FactoryBaseImpl
implements Platform
    {

    public MockPlatformImpl()
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
            new MockDockerContainerValidatorImpl(
                this.dockerContainerEntityFactory
                )
            );
        this.computeResourceValidatorFactory.addValidator(
            new MockSimpleComputeResourceValidatorImpl(
                this.computeResourceEntityFactory
                )
            );
        this.storageResourceValidatorFactory.addValidator(
            new MockSimpleStorageResourceValidatorImpl(
                this.storageResourceEntityFactory
                )
            );

        this.registerFactory(this.dockerContainerEntityFactory);
        this.registerFactory(this.jupyterNotebookEntityFactory);
        this.registerFactory(this.computeResourceEntityFactory);
        this.registerFactory(this.dataResourceEntityFactory);
        this.registerFactory(this.storageResourceEntityFactory);
        
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
    private MockSimpleComputeResourceEntityFactory computeResourceEntityFactory;

    @Autowired
    private AbstractComputeResourceValidatorFactory computeResourceValidatorFactory;
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.computeResourceValidatorFactory;
        }
    
// Data   

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
    private MockDockerContainerEntityFactory dockerContainerEntityFactory;  

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

    @Autowired
    private SessionProcessingRequestFactory sessionProcessingRequestFactory;
    @Override
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory()
        {
        return this.sessionProcessingRequestFactory;
        }
    
    @Autowired
    private ComponentProcessingRequestFactory componentProcessingRequestFactory;
    @Override
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory()
        {
        return this.componentProcessingRequestFactory;
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
