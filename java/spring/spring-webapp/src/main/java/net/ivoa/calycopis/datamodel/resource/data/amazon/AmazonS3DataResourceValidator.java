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
package net.ivoa.calycopis.datamodel.resource.data.amazon;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.ValidatorTools;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAmazonS3DataResource;

/**
 * A validator implementation to handle simple data resources.
 * TODO A lot of this should be inherited from SimpleDataResourceValidator.
 * TODO Create a common base class with methods that can be inherited.
 * 
 */
@Slf4j
public class AmazonS3DataResourceValidator
extends ValidatorTools
implements AbstractDataResourceValidator
    {
    /**
     * Factory for creating Entities.
     * 
     */
    final AmazonS3DataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public AmazonS3DataResourceValidator(final AmazonS3DataResourceEntityFactory entityFactory)
        {
        super();
        this.entityFactory = entityFactory ;
        }

    @Override
    public AbstractDataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        if (requested instanceof IvoaAmazonS3DataResource)
            {
            return validate(
                (IvoaAmazonS3DataResource) requested,
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
     * Validate an S3 data resource.
     *
     */
    public AbstractDataResourceValidator.Result validate(
        final IvoaAmazonS3DataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaS3DataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaAmazonS3DataResource validated = new IvoaAmazonS3DataResource();

        String name = trim(
            requested.getName()
            );
        String endpoint = trim(
            requested.getEndpoint()
            );
        String template = trim(
                requested.getTemplate()
            );
        String bucket = trim(
                requested.getBucket()
            );
        String object = trim(
                requested.getObject()
            );

        if ((endpoint == null) || (endpoint.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "S3 service endpoint required"
                );
            success = false;
            }

        if ((template == null) || (template.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "S3 service template required"
                );
            success = false;
            }

        if ((bucket == null) || (bucket.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "S3 bucket name required"
                );
            success = false;
            }

        //
        // Accumulate state and return the fail here.
        //

        validated.setName(name);
        validated.setEndpoint(endpoint);
        validated.setTemplate(template);
        validated.setBucket(bucket);
        validated.setObject(object);

        //
        // Make a guess at the data size.
        // TODO Add optional size to data resource.
        long size = 1024L;

        //
        // Find or create the storage resource.
        AbstractStorageResourceValidator.Result storageResult = null;

        //
        // Everything is good, so accept the request.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public AmazonS3DataResourceEntity build(ExecutionSessionEntity parent)
                    {
                    return entityFactory.create(
                        parent,
                        validated
                        );
                    }
                }; 
            
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );

            //
            // Save the DataResource in the state.
            context.addDataValidatorResult(
                dataResult
                );
            //
            // Add the link between the DataResource and StorageResource.
            context.addDataStorageResult(
                dataResult,
                storageResult
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
    }
