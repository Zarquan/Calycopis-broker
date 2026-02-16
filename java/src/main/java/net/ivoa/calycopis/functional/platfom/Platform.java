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

import java.net.URI;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
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
 * Platform is basically a factory of factories.
 * It provides a set of factories that provide platform specific implementations of the entities and validators.
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
     * Get a LifecycleComponentEntity using the appropriate factory for the kind.
     *  
     */
    public LifecycleComponentEntity select(final URI kind, final UUID uuid);
    
    /**
     * Get the ExecutionSessionEntityFactory for this platform.
     * TODO Do we need the <?> generic wildcard here?
     *
     */
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory();

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

    /**
     * Get the ComponentEntityFactory for this platform.
     *
     */
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory();
    
    /**
     * Get the ComputeResourceOfferFactory for this platform.
     *
     */
    public ComputeResourceOfferFactory getComputeResourceOfferFactory();

    /**
     * Get the ComputeResourceValidatorFactory for this platform.
     *
     */
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators();
    
    /**
     * Get the ComputeResourceEntityFactory for this platform.
     *
    public AbstractComputeResourceEntityFactory getComputeResourceEntityFactory();
     */
    
    /**
     * Get the DataResourceEntityFactory for this platform.
     *
     */
    public AbstractDataResourceEntityFactory getDataResourceEntityFactory();

    /**
     * Get the DataResourceValidatorFactory for this platform.
     *
     */
    public AbstractDataResourceValidatorFactory getDataResourceValidators();

    /**
     * Get the ExecutableValidatorFactory for this platform.
     *
     */
    public AbstractExecutableValidatorFactory getExecutableValidators();

    /**
     * Get the DockerContainerEntityFactory for this platform.
     * TODO Fold this into a generic ExecutableEntityFactory and remove the DockerContainerEntityFactory and JupyterNotebookEntityFactory from the platform interface.
     *
    public DockerContainerEntityFactory getDockerContainerEntityFactory();
     */

    /**
     * Get the JupyterNotebookEntityFactory for this platform.
     * TODO Fold this into a generic ExecutableEntityFactory and remove the DockerContainerEntityFactory and JupyterNotebookEntityFactory from the platform interface.
     *
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory();
     */
    
    /**
     * Get the StorageResourceEntityFactory for this platform.
     *
    public AbstractStorageResourceEntityFactory getStorageResourceEntityFactory();
     */

    /**
     * Get the StorageResourceValidatorFactory for this platform.
     *
     */
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators();

    /**
     * Get the VolumeMountValidatorFactory for this platform.
     *
     */
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators();
    
    }
