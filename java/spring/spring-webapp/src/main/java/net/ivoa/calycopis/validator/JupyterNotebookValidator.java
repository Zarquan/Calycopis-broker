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

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * A validator implementation to handle IvoaJupyterNotebooks.
 * 
 */
@Slf4j
public class JupyterNotebookValidator
implements ExecutableValidator
    {
    
    @Override
    public ValidatorResult validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}]", requested.getClass().getName());
        switch(requested)
            {
            case IvoaJupyterNotebook jupyterNotebook:
                return validate(
                    jupyterNotebook,
                    state
                    );
            default:
                return ValidatorResult.CONTINUE;
            }
        }

    /**
     * Validate an IvoaJupyterNotebook.
     *
     */
    public ValidatorResult validate(
        final IvoaJupyterNotebook requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("Notebook [{}]", requested.getName());

        //
        // Validate the location.
        if (requested.getLocation() == null)
            {
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Notebook location required"
                );
            state.valid(false);
            return ValidatorResult.FAILED;
            }
        else {
            requested.setLocation(
                requested.getLocation().trim()
                );
            if (requested.getLocation().isEmpty())
                {
                state.getOfferSetEntity().addWarning(
                    "urn:missing-required-value",
                    "Notebook location required"
                    );
                state.valid(false);
                return ValidatorResult.FAILED;
                }
            }
        //
        // Validate the notebook name.
        if (requested.getName() == null)
            {
            // No name is fine.
            }
        else {
            String name = requested.getName().trim();
            if (name.isEmpty())
                {
                // Empty name is not allowed.
                requested.setName(null);
                }
            else {
                requested.setName(
                    name
                    );
                }
            }

        //
        // Add our executable to the state.
        state.setRequestedExecutable(
            requested
            );
        //
        // Everything is good, so accept the request.
        return ValidatorResult.ACCEPTED;

        }
    }
