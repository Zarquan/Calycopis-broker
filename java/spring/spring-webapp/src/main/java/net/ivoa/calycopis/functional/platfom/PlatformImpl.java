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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequestFactory;

/**
 * 
 */
@Component
public class PlatformImpl
extends FactoryBaseImpl
implements Platform
    {

    public PlatformImpl()
        {
        super();
        }
    
    @Autowired
    public PlatformImpl(
        final DockerContainerEntityFactory dockerContainerEntityFactory,
        final JupyterNotebookEntityFactory jupyterNotebookEntityFactory,
        final ProcessingRequestFactory processingRequestFactory,
        final SessionProcessingRequestFactory sessionProcessingRequestFactory,
        final ComponentProcessingRequestFactory componentResourceProcessingRequestFactory
        ){
        this.dockerContainerEntityFactory = dockerContainerEntityFactory ;
        this.jupyterNotebookEntityFactory = jupyterNotebookEntityFactory ;
        this.processingRequestFactory = processingRequestFactory;
        this.sessionProcessingRequestFactory = sessionProcessingRequestFactory;
        this.componentProcessingRequestFactory = componentResourceProcessingRequestFactory;
        }

    private DockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    private JupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    @Override
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory()
        {
        return this.jupyterNotebookEntityFactory;
        }

    private ProcessingRequestFactory processingRequestFactory;
    @Override
    public ProcessingRequestFactory getProcessingRequestFactory()
        {
        return this.processingRequestFactory;
        }

    private SessionProcessingRequestFactory sessionProcessingRequestFactory;
    @Override
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory()
        {
        return this.sessionProcessingRequestFactory;
        }
    
    private ComponentProcessingRequestFactory componentProcessingRequestFactory;
    @Override
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory()
        {
        return this.componentProcessingRequestFactory;
        }
    
    }
