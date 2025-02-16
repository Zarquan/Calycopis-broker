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
package net.ivoa.calycopis.validator.executable;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorBaseImpl;

/**
 * A factory for IvoaAbstractExecutable validators.
 *   
 */
@Component
public class ExecutableValidatorFactoryImpl
    extends ValidatorBaseImpl<IvoaAbstractExecutable>
    implements FactoryBase, Validator<IvoaAbstractExecutable>
    {
    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable. 
     * 
     */
    public ExecutableValidatorFactoryImpl()
        {
        super();
        this.validators.add(
            new JupyterNotebookValidator()
            );
        }
    
    @Override
    public void save(
        final OfferSetRequestParserState state,
        final IvoaAbstractExecutable executable
        ){
        state.getValidatedOfferSetRequest().setExecutable(
            executable
            );
        }

    @Override
    public Validator.ResultSet<IvoaAbstractExecutable> unknown(
        final OfferSetRequestParserState state,
        final IvoaAbstractExecutable executable
        ){
        return super.unknown(
            state,
            executable.getType(),
            executable.getClass().getName()
            );
        }
    }
