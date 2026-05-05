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

package net.ivoa.calycopis.broker.engine.entities.executable.jupyter;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaJupyterNotebook;

/**
 * A Jupyter notebook executable.
 *
 */
@Entity
@Table(
    name = "jupyternotebooks"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class JupyterNotebookEntityImpl
    extends AbstractExecutableEntityImpl
    implements JupyterNotebook
    {
    @Override
    public URI getKind()
        {
        return JupyterNotebook.TYPE_DISCRIMINATOR ;
        }

    protected JupyterNotebookEntityImpl()
        {
        super();
        }

    // TODO Get rid of the class cast.    
    protected JupyterNotebookEntityImpl(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        ){
        this(   
            session,
            result,
            (IvoaJupyterNotebook) result.getObject()
            );
        }

    protected JupyterNotebookEntityImpl(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result,
        final IvoaJupyterNotebook validated
        ){
        super(
            session,
            result,
            validated.getMeta()
            );
        this.location = validated.getLocation();
        }

    private String location;
    @Override
    public String getLocation()
        {
        return this.location;
        }

    @Override
    public IvoaJupyterNotebook makeBean(final URIBuilder builder)
        {
        return this.fillBean(
            new IvoaJupyterNotebook().meta(
                this.makeMeta(
                    builder
                    )
                )
            );
        }

    protected IvoaJupyterNotebook fillBean(final IvoaJupyterNotebook bean)
        {
        super.fillBean(bean);
        return bean;
        }
    }
