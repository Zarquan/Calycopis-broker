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
package net.ivoa.calycopis.datamodel.executable.jupyter;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * A validator implementation to handle JupyterNotebooks.
 * 
 */
@Slf4j
public class JupyterNotebookValidatorImpl
extends AbstractValidatorImpl
implements JupyterNotebookValidator
    {
    
    private final Platform platform;

    @Autowired
    public JupyterNotebookValidatorImpl(final Platform platform)
        {
        this.platform = platform;
        }
    
    @Override
    public AbstractExecutableValidator.Result validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());
        if (requested instanceof IvoaJupyterNotebook)
            {
            return validate(
                (IvoaJupyterNotebook) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    /**
     * Validate an IvoaJupyterNotebook.
     *
     */
    public AbstractExecutableValidator.Result validate(
        final IvoaJupyterNotebook requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaJupyterNotebook validated = new IvoaJupyterNotebook(
            JupyterNotebook.TYPE_DISCRIMINATOR
            );

        //
        // Validate the executable name.
        success &= validateName(
            requested.getName(),
            validated,
            context
            );

        //
        // Validate the notebook location.
        success &= validateLocation(
            requested.getLocation(),
            validated,
            context
            );
        
        //
        // Everything is good, add our result to the context.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public AbstractExecutableEntity build(final ExecutionSessionEntity session)
                    {
                    return platform.getJupyterNotebookEntityFactory().create(
                        session,
                        validated
                        );
                    }
                }; 

            AbstractExecutableValidator.Result result = new AbstractExecutableValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );
            /*
            context.getValidatedOfferSetRequest().setExecutable(
                validated
                );
             */
            context.setExecutableResult(
                result
                );
            return result;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }

    /**
     * Validate the executable name.
     * 
     */
    public boolean validateName(
        final String requested,
        final IvoaJupyterNotebook validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateName(String ...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        String name = notEmpty(
            requested
            );
        if (name != null)
            {
            // TODO Make this configurable.
            success &= badValueCheck(
                name,
                context
                );
            }
        if (success)
            {
            validated.setName(
                name
                );
            }
        else {
            validated.setName(
                null
                );
            }
        
        return success;
        }

    
    /**
     * Validate the notebook location.
     * 
     */
    public boolean validateLocation(
        final String requested,
        final IvoaJupyterNotebook validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateLocation(String ...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        String location = notEmpty(
            requested
            );
        if ((location == null) || (location.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "uri:missing-required-value",
                "Notebook location required"
                );
            success = false ;
            }
        else {
            success &= badValueCheck(
                location,
                context
                );
            }
        if (success)
            {
            validated.setLocation(
                location
                );
            }
        else {
            validated.setLocation(
                null
                );
            }
        return success;
        }
    }
