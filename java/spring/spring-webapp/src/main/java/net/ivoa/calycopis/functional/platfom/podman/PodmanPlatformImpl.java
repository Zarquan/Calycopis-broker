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

package net.ivoa.calycopis.functional.platfom.podman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.executable.docker.podman.PodmanDockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.podman.PodmanJupyterNotebookEntityFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Component
public class PodmanPlatformImpl
extends FactoryBaseImpl
implements PodmanPlatform
    {

    /**
     * 
     */
    @Autowired
    public PodmanPlatformImpl(
        final PodmanDockerContainerEntityFactory dockerContainerEntityFactory,
        final PodmanJupyterNotebookEntityFactory jupyterNotebookEntityFactory
        ){
        this.dockerContainerEntityFactory = dockerContainerEntityFactory ;
        this.jupyterNotebookEntityFactory = jupyterNotebookEntityFactory ;
        }

    private PodmanDockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public PodmanDockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    private PodmanJupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    @Override
    public PodmanJupyterNotebookEntityFactory getJupyterNotebookEntityFactory()
        {
        return this.jupyterNotebookEntityFactory;
        }
    }
