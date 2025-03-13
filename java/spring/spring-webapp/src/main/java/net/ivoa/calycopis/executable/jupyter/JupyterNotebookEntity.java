/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.executable.jupyter;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * A Jupyter notebook executable.
 *
 */
@Entity
@Table(
    name = "jupyternotebooks"
    )
@DiscriminatorValue(
    value = "uri:jupyter-notebook"
    )
public class JupyterNotebookEntity
    extends AbstractExecutableEntity
    implements JupyterNotebook
    {

    protected JupyterNotebookEntity()
        {
        super();
        }

    protected JupyterNotebookEntity(final ExecutionSessionEntity parent, final String name, final String location)
        {
        super(parent, name);
        this.location = location;
        }

    private String location;
    @Override
    public String getLocation()
        {
        return this.location;
        }

    @Override
    public IvoaAbstractExecutable getIvoaBean(final String baseurl)
        {
        IvoaJupyterNotebook bean = new IvoaJupyterNotebook(
            JupyterNotebook.TYPE_DISCRIMINATOR
            );
        bean.setUuid(
            this.getUuid()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        bean.location(this.getLocation());
        return null;
        }
    }
