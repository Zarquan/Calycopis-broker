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
package net.ivoa.calycopis.datamodel.volume.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaSimpleVolumeMount;

/**
 * A Validator implementation to handle simple storage resources.
 *
 */
@Slf4j
public class SimpleVolumeMountValidatorImpl
extends AbstractVolumeMountValidatorImpl
implements SimpleVolumeMountValidator
    {
    /**
     * Factory for creating Entities.
     *
     */
    final SimpleVolumeMountEntityFactory entityFactory;

    /**
     * Public constructor.
     *
     */
    public SimpleVolumeMountValidatorImpl(
        final SimpleVolumeMountEntityFactory entityFactory
        ){
        super();
        this.entityFactory = entityFactory ;
        }

    @Override
    public AbstractVolumeMountValidator.Result validate(
        final IvoaAbstractVolumeMount requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractVolumeMount)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleVolumeMount simple:
                return validate(
                        simple,
                        context
                        );
            default:
                return new ResultBean(
                    Validator.ResultEnum.CONTINUE
                    );
            }
        }

    /**
     * Validate an IvoaSimpleVolumeMount.
     *
     */
    public AbstractVolumeMountValidator.Result validate(
        final IvoaSimpleVolumeMount requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleVolumeMount)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleVolumeMount validated = new IvoaSimpleVolumeMount()
           .kind(SimpleVolumeMount.TYPE_DISCRIMINATOR)
           .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
               );
        
        validated.setPath(
            requested.getPath()
            );
        validated.setMode(
            requested.getMode()
            );
        validated.setCardinality(
            requested.getCardinality()
            );
        //
        // TODO Check the list of data resources.
        //
        
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new validator Result.
            AbstractVolumeMountValidator.Result volumeResult = new AbstractVolumeMountValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public SimpleVolumeMountEntity build(final SimpleExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        this
                        );
                    }

                @Override
                public Long getPreparationTime()    
                    {
                    // TODO This will be platform dependent.
                    return DEFAULT_PREPARE_TIME;
                    }
                };
            //
            // Add our Result to our context.
            context.addVolumeValidatorResult(
                volumeResult
                );
            return volumeResult;
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
    }
