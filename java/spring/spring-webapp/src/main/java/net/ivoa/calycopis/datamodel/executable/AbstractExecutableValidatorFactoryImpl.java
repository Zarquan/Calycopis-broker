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
package net.ivoa.calycopis.datamodel.executable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.validator.ValidatorFactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;

/**
 * A factory for IvoaAbstractExecutable validators.
 *   
 */
@Component
public class AbstractExecutableValidatorFactoryImpl
    extends ValidatorFactoryBaseImpl<IvoaAbstractExecutable, AbstractExecutableEntity>
    implements AbstractExecutableValidatorFactory
    {
    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * TODO Make the platform abstract. 
     * 
     */
    @Autowired
    public AbstractExecutableValidatorFactoryImpl(
        final Platform platform

        ){
        super();
        this.validators.add(
            new JupyterNotebookValidatorImpl(
                platform
                )
            );
        this.validators.add(
            new DockerContainerValidatorImpl(
                platform
                )
            );
        }
    
    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractExecutable executable
        ){
        unknown(
            context,
            executable.getKind(),
            executable.getClass().getName()
            );
        }
    }
