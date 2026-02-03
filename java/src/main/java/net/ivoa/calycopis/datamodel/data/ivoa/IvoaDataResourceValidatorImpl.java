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
package net.ivoa.calycopis.datamodel.data.ivoa;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataResourceBlock;
import net.ivoa.calycopis.spring.model.IvoaIvoaObsCoreItem;

/**
 * A Validator implementation to handle IvoaDataResources.
 *
 */
@Slf4j
public class IvoaDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements IvoaDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     *
     */
    final IvoaDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     *
     */
    public IvoaDataResourceValidatorImpl(
        final IvoaDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            storageValidators
            );
        this.entityFactory = entityFactory ;
        }

    @Override
    public AbstractDataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        if (requested instanceof IvoaIvoaDataResource)
            {
            return validate(
                (IvoaIvoaDataResource) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    public AbstractDataResourceValidator.Result validate(
        final IvoaIvoaDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaIvoaDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaIvoaDataResource validated = new IvoaIvoaDataResource()
            .kind(IvoaDataResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        success &= duplicateCheck(
            requested,
            context
            );

        AbstractStorageResourceValidator.Result storage = storageCheck(
            requested,
            validated,
            context
            );
        success &= ResultEnum.ACCEPTED.equals(storage.getEnum());
        
        //
        // Validate the IvoaIvoaDataResourceBlock.
        success &= validate(
            requested.getIvoa(),
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
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public IvoaDataResourceEntity build(final SimpleExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        storage.getEntity(),
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
            context.addDataValidatorResult(
                dataResult
                );
            //
            // Add the DataResource to the StorageResource.
            storage.addDataResourceResult(
                dataResult
                );
            return dataResult ;
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

    public boolean validate(
        final IvoaIvoaDataResourceBlock requested,
        final IvoaIvoaDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        if (requested != null)
            {
            IvoaIvoaDataResourceBlock block = new IvoaIvoaDataResourceBlock();
            validated.setIvoa(block);
            // TODO validate the URI
            URI ivoid = requested.getIvoid();
            if (null != ivoid)
                {
                block.setIvoid(
                    ivoid
                    );
                }
            else {
                context.getOfferSetEntity().addWarning(
                    "urn:missing-required-value",
                    "Ivoa ID (ivoid) required"
                    );
                success = false ;
                }
            
            IvoaIvoaObsCoreItem obscore = requested.getObscore();
            if (null != obscore)
                {
                // TODO process the ObsCore data.
                block.setObscore(obscore);
                }

            IvoaIvoaDataLinkItem datalink = requested.getDatalink();
            if (null != datalink)
                {
                // TODO process the DataLink data.
                block.setDatalink(datalink);
                }
            }
        else {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Ivoa metadata required"
                );
            success = false ;
            }
        return success ;
        }
    
    /*
     * TODO This will be platform dependent.
     * 
     */
    public static final Long DEFAULT_PREPARE_TIME = 5L;
    @Deprecated
    private Long predictPrepareTime(final IvoaIvoaDataResource validated)
        {
        log.debug("predictPrepareTime()");
        return DEFAULT_PREPARE_TIME;
        }
    }
