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

package net.ivoa.calycopis.functional.platfom;

import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequestFactory;

/**
 * 
 */
public interface Platform
extends FactoryBase
    {

    /**
     * Initialize the platform.
     *
     */
    public void initialize();
    
    /**
     * Get the DockerContainerEntityFactory for this platform.
     *
     */
    public DockerContainerEntityFactory getDockerContainerEntityFactory();

    /**
     * Get the JupyterNotebookEntityFactory for this platform.
     *
     */
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory();

    /**
     * Get the ProcessingRequestFactory for this platform.
     *
     */
    public ProcessingRequestFactory getProcessingRequestFactory();
    
    /**
     * Get the SessionProcessingRequestFactory for this platform.
     *
     */
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory();
    
    /**
     * Get the ComponentProcessingRequestFactory for this platform.
     *
     */
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory();
    
    
    public ComputeResourceOfferFactory             getComputeResourceOfferFactory();
    public AbstractComputeResourceEntityFactory getComputeResourceEntityFactory();
    
    public AbstractDataResourceEntityFactory getDataResourceEntityFactory();
    
    public AbstractExecutableEntityFactory getExecutableEntityFactory();

    public AbstractStorageResourceEntityFactory getStorageResourceEntityFactory();
    
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory();

    public AbstractExecutableValidatorFactory       getExecutableValidators();
    public AbstractStorageResourceValidatorFactory  getStorageResourceValidators();
    public AbstractDataResourceValidatorFactory     getDataResourceValidators();
    public AbstractVolumeMountValidatorFactory      getVolumeMountValidators();
    public AbstractComputeResourceValidatorFactory  getComputeResourceValidators();
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory();
    
    }
