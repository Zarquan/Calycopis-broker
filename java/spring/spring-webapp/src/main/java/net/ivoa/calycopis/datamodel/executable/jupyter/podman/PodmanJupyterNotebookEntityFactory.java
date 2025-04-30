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

package net.ivoa.calycopis.datamodel.executable.jupyter.podman;

import java.util.UUID;

import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * 
 */
public interface PodmanJupyterNotebookEntityFactory
extends JupyterNotebookEntityFactory
    {
    /**
     * Find a Entity based on UUID.
     * 
     */
    public PodmanJupyterNotebookEntity select(final UUID uuid);

    /**
     * Create a new Entity based on a template.
     *
     */
    public PodmanJupyterNotebookEntity create(final ExecutionSessionEntity session, final IvoaJupyterNotebook template);

    }
