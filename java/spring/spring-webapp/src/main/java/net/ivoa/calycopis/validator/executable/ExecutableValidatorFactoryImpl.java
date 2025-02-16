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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.validator.Validator;

/**
 * A factory for IvoaAbstractExecutable validators.
 *   
 */
@Component
public class ExecutableValidatorFactoryImpl
    extends FactoryBaseImpl
    implements FactoryBase, Validator<IvoaAbstractExecutable>
    {
    /**
     * Our list of ExecutableValidators.
     * TODO Make this configurable ...
     * 
     */
    private List<Validator<IvoaAbstractExecutable>> validators = new ArrayList<Validator<IvoaAbstractExecutable>>();
        {
        validators.add(
            new JupyterNotebookValidator()
            );
        }
    
    @Override
    public ResultSet<IvoaAbstractExecutable> validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserState state
        ){
        //
        // Try each of the validators in our list.
        for (Validator<IvoaAbstractExecutable> validator : validators)
            {
            ResultSet<IvoaAbstractExecutable> result = validator.validate(
                requested,
                state
                );
            switch(result.getEnum())
                {
                case ResultEnum.ACCEPTED:
                    state.getValidatedOfferSetRequest().setExecutable(
                        result.getObject()
                        );
                    return result ;
                case ResultEnum.FAILED:
                    return result ;
                default:
                    continue;
                }
            }
        //
        // If we didn't find a matching validator, add a warning and fail the validation.
        state.getOfferSetEntity().addWarning(
            "urn:unknown-type",
            "Unknown executable type [${type}][${class}]",
            Map.of(
                "type",
                requested.getType(),
                "class",
                requested.getClass().getName()
                )
            );
        state.valid(false);
        return new ResultSetBean<IvoaAbstractExecutable>(
            ResultEnum.FAILED
            );
        }
    }
