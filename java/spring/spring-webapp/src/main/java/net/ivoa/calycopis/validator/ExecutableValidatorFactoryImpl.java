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
package net.ivoa.calycopis.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;

/**
 * A factory for ExecutableValidators.
 *   
 */
@Component
public class ExecutableValidatorFactoryImpl
    implements ExecutableValidatorFactory
    {
    /**
     * Our list of ExecutableValidators.
     * TODO Make this configurable ...
     * 
     */
    private List<ExecutableValidator> executableValidators = new ArrayList<ExecutableValidator>();
        {
        executableValidators.add(
            new JupyterNotebookValidator()
            );
        }
    
    @Override
    public ValidatorResult validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserState state
        ){
        ValidatorResult result = null ; 
        //
        // Check the request has an executable.
        if (null == requested)
            {
            state.getOfferSetEntity().addWarning(
                "urn:executable-required",
                "Executable is required"
                );
            state.valid(false);
            result = ValidatorResult.FAILED;
            }
        else {
            //
            // Try each of the validators in our list.
            for (ExecutableValidator validator : executableValidators)
                {
                result = validator.validate(
                    requested,
                    state
                    );
                if (result == ValidatorResult.CONTINUE)
                    {
                    continue;
                    }
                else {
                    break ; 
                    }
                }
            //
            // If we didn't find a matching validator, add a warning and fail the validation.
            if (result == ValidatorResult.CONTINUE)
                {
                state.getOfferSetEntity().addWarning(
                    "urn:unknown-executable-type",
                    "Unknown executable type [${type}][${class}]",
                    Map.of(
                        "type",
                        requested.getType(),
                        "class",
                        requested.getClass().getName()
                        )
                    );
                state.valid(false);
                result = ValidatorResult.FAILED;
                }
            }
        return result;
        }
    }
