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
package net.ivoa.calycopis.datamodel.resource.data.skao;

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
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaSkaoReplicaItem;

/**
 * A Validator implementation to handle IvoaDataResources.
 *
 */
@Slf4j
public class SkaoDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements SkaoDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     *
     */
    final SkaoDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     *
     */
    public SkaoDataResourceValidatorImpl(
        final SkaoDataResourceEntityFactory entityFactory,
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
        if (requested instanceof IvoaSkaoDataResource)
            {
            return validate(
                (IvoaSkaoDataResource) requested,
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
        final IvoaSkaoDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSkaoDataResource)");
        log.debug("Resource [{}]", context.makeDataValidatorResultKey(requested));

        boolean success = true ;

        IvoaSkaoDataResource validated = new IvoaSkaoDataResource(
            SkaoDataResource.TYPE_DISCRIMINATOR
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

        success &= validate(
            requested.getSkao(),
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
                public SkaoDataResourceEntity build(final ExecutionSessionEntity session)
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

    // Inherit this from Ivoa validator ?
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
        return success ;
        }

    public boolean validate(
        final IvoaSkaoDataResourceBlock requested,
        final IvoaSkaoDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        if (requested != null)
            {
            IvoaSkaoDataResourceBlock block = new IvoaSkaoDataResourceBlock();
            validated.setSkao(block);
            // TODO Validate the values.
            block.setNamespace(requested.getNamespace());
            block.setObjectname(requested.getObjectname());
            block.setObjecttype(requested.getObjecttype());
            block.setDatasize(requested.getDatasize());
            block.setChecksum(requested.getChecksum());

            for (IvoaSkaoReplicaItem replica : requested.getReplicas())
                {
                // TODO Validate the fields
                IvoaSkaoReplicaItem newReplica = new IvoaSkaoReplicaItem();
                newReplica.setRsename(replica.getRsename());
                newReplica.setDataurl(replica.getDataurl());
                block.addReplicasItem(
                    newReplica
                    );
                }
            }
        return success ;
        }
    }
