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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookValidatorImpl;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
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
            new JupyterNotebookValidatorImpl(
                this
                )
            );
        this.executableValidatorFactory.addValidator(
            new DockerContainerValidatorImpl(
                this
                )
            );
        }
    
    @Autowired
    private DockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    @Autowired
    private JupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    @Override
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory()
        {
        return this.jupyterNotebookEntityFactory;
        }

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

    @Autowired
    private AbstractComputeResourceEntityFactory computeResourceEntityFactory;
    @Override
    public AbstractComputeResourceEntityFactory getComputeResourceEntityFactory()
        {
        return this.computeResourceEntityFactory;
        }

    @Autowired
    private AbstractDataResourceEntityFactory dataResourceEntityFactory;
    @Override
    public AbstractDataResourceEntityFactory getDataResourceEntityFactory()
        {
        return this.dataResourceEntityFactory;
        }

    @Autowired
    private AbstractExecutableEntityFactory executableEntityFactory;
    @Override
    public AbstractExecutableEntityFactory getExecutableEntityFactory()
        {
        return this.executableEntityFactory;
        }

    @Autowired
    private AbstractStorageResourceEntityFactory storageResourceEntityFactory;
    @Override
    public AbstractStorageResourceEntityFactory getStorageResourceEntityFactory()
        {
        return this.storageResourceEntityFactory;
        }

    @Autowired
    private AbstractLifecycleComponentEntityFactory lifecycleComponentEntityFactory;
    @Override
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory()
        {
        return this.lifecycleComponentEntityFactory;
        }

    @Autowired
    private ComputeResourceOfferFactory computeResourceOfferFactory;
    @Override
    public ComputeResourceOfferFactory getComputeResourceOfferFactory()
        {
        return this.computeResourceOfferFactory;
        }

    @Autowired
    private AbstractExecutableValidatorFactory executableValidatorFactory;
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.executableValidatorFactory;
        }

    @Autowired
    private AbstractStorageResourceValidatorFactory storageResourceValidatorFactory;
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.storageResourceValidatorFactory;
        }

    @Autowired
    private AbstractDataResourceValidatorFactory dataResourceValidatorFactory;
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.dataResourceValidatorFactory;
        }

    @Autowired
    private AbstractVolumeMountValidatorFactory volumeMountValidatorFactory;
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.volumeMountValidatorFactory;
        }

    @Autowired
    private AbstractComputeResourceValidatorFactory computeResourceValidatorFactory;
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.computeResourceValidatorFactory;
        }

    @Autowired
    private SimpleExecutionSessionEntityFactory executionSessionEntityFactory;
    @Override
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory()
        {
        return this.executionSessionEntityFactory;
        }
    }
