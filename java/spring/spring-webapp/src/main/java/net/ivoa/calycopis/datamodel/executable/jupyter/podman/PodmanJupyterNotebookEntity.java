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

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "podmanjupyternotebooks"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PodmanJupyterNotebookEntity
extends JupyterNotebookEntity
implements PodmanJupyterNotebook
    {

    /**
     * 
     */
    public PodmanJupyterNotebookEntity()
        {
        super();
        }

    /**
     *
     */
    public PodmanJupyterNotebookEntity(
        final ExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    // Extra Podman related stuff ...
    
    }
