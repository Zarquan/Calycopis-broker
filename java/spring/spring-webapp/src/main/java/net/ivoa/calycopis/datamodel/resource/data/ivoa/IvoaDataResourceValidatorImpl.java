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
package net.ivoa.calycopis.datamodel.resource.data.ivoa;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaIvoaObsCoreItem;

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
        log.debug("Resource [{}][{}]", context.makeDataValidatorResultKey(requested), requested.getClass().getName());
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
        log.debug("Resource [{}]", context.makeDataValidatorResultKey(requested));

        boolean success = true ;

        IvoaIvoaDataResource validated = new IvoaIvoaDataResource(
            IvoaDataResource.TYPE_DISCRIMINATOR
            );

        success &= duplicateCheck(
            requested,
            context
            );

        success &= storageCheck(
            requested,
            validated,
            context
            );
        
        validated.setUuid(
            requested.getUuid()
            );
        validated.setName(
            trim(
                requested.getName()
                )
            );
        success &= validate(
            requested.getIvoa(),
            validated,
            context
            );

        //
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public IvoaDataResourceEntity build(final ExecutionSessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        validated
                        );
                    }
                };

            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );
            context.addDataValidatorResult(
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
    }
