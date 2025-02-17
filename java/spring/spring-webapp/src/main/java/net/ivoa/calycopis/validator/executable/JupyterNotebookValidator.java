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

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;

/**
 * A validator implementation to handle IvoaJupyterNotebooks.
 * 
 */
@Slf4j
public class JupyterNotebookValidator
extends ValidatorTools<IvoaAbstractExecutable, AbstractExecutableEntity>
implements Validator<IvoaAbstractExecutable, AbstractExecutableEntity>
    {
    
    @Override
    public Result<IvoaAbstractExecutable, AbstractExecutableEntity> validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaJupyterNotebook jupyterNotebook:
                return validate(
                    jupyterNotebook,
                    state
                    );
            default:
                return continueResult();
            }
        }

    /**
     * Validate an IvoaJupyterNotebook.
     *
     */
    public Result<IvoaAbstractExecutable, AbstractExecutableEntity> validate(
        final IvoaJupyterNotebook requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaJupyterNotebook result = new IvoaJupyterNotebook();

        //
        // Validate the location.
        if (requested.getLocation() == null)
            {
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Notebook location required"
                );
            success = false ;
            }
        else {
            String trimmed = requested.getLocation().trim(); 
            if (trimmed.isEmpty())
                {
                state.getOfferSetEntity().addWarning(
                    "urn:missing-required-value",
                    "Notebook location required"
                    );
                success = false ;
                }
            else {
                result.setLocation(
                    trimmed
                    );
                }
            }
        //
        // Validate the notebook name.
        if (requested.getName() == null)
            {
            // No name is fine.
            }
        else {
            String trimmed = requested.getName().trim();
            if (trimmed.isEmpty())
                {
                // No name is fine.
                }
            else {
                result.setName(
                    trimmed
                    );
                }
            }
        
        //
        // Everything is good, add our result to the state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            state.setExecutable(result);
            return acceptResult(
                result
                );
            }
        //
        // Something wasn't right, fail the validation.
        else {
            state.valid(false);
            return failResult();
            }
        }
    }
