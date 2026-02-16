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
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaJupyterNotebook;

/**
 * A validator implementation to handle JupyterNotebooks.
 * 
 */
@Slf4j
public abstract class JupyterNotebookValidatorImpl
extends AbstractValidatorImpl<IvoaAbstractExecutable, AbstractExecutableEntity>
implements JupyterNotebookValidator
    {
    
    private final JupyterNotebookEntityFactory entityFactory;

    @Autowired
    public JupyterNotebookValidatorImpl(final JupyterNotebookEntityFactory entityFactory)
        {
        this.entityFactory = entityFactory;
        }
    
    @Override
    public void validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());
        if (requested instanceof IvoaJupyterNotebook)
            {
            validate(
                (IvoaJupyterNotebook) requested,
                context
                );
            }
        }

    /**
     * Validate an IvoaJupyterNotebook.
     *
     */
    public void validate(
        final IvoaJupyterNotebook requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaJupyterNotebook validated = new IvoaJupyterNotebook()
            .kind(JupyterNotebook.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        //
        // Validate the notebook location.
        success &= validateLocation(
            requested.getLocation(),
            validated,
            context
            );

        //
        // Calculate the preparation time.
        /*
         * 
        validated.setSchedule(
            new IvoaComponentSchedule()
            );
        success &= setPrepareDuration(
            context,
            validated.getSchedule(),
            this.predictPrepareTime(
                validated
                )
            );
         * 
         */
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new validator Result.
            AbstractExecutableValidator.Result result = new AbstractExecutableValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public AbstractExecutableEntity build(final SimpleExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        this
                        );
                    }

                @Override
                public Long getPreparationTime()
                    {
                    return estimatePrepareTime(
                        validated
                        );
                    }
                };
            //
            // Add our Result to our context
            context.setExecutableResult(
                result
                );
            context.dispatched(true);
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            context.dispatched(true);
            }
        }

    /**
     * Apply any platform specific validation rules.
     * 
     */
    protected abstract boolean validateLocation(final String location, final OfferSetRequestParserContext context);

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
            context.addWarning(
                "uri:missing-required-value",
                "JupyterNotebook - location is required"
                );
            success = false ;
            }
        else {
            success &= validateLocation(
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
        
        return success;
        }

    /**
     * Predict the time to prepare a DockerContainer for execution.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long estimatePrepareTime(final IvoaJupyterNotebook validated);

    /**
     * Predict the time to release a DockerContainer.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long estimateReleaseTime(final IvoaJupyterNotebook validated);
    
    }

