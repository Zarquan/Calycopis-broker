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
package net.ivoa.calycopis.validator.data;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.builder.Builder;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceEntity;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaS3DataResource;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;
import net.ivoa.calycopis.validator.storage.StorageResourceValidator;

/**
 * A validator implementation to handle simple data resources.
 * TODO A lot of this should be inherited from SimpleDataResourceValidator.
 * TODO Create a common base class with methods that can be inherited.
 * 
 */
@Slf4j
public class S3DataResourceValidator
extends ValidatorTools
implements DataResourceValidator
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
    public S3DataResourceValidator(final AmazonS3DataResourceEntityFactory entityFactory)
        {
        super();
        this.entityFactory = entityFactory ;
        }

    @Override
    public DataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        if (requested instanceof IvoaS3DataResource)
            {
            return validate(
                (IvoaS3DataResource) requested,
                state
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
    public DataResourceValidator.Result validate(
        final IvoaS3DataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaS3DataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaS3DataResource validated = new IvoaS3DataResource();

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
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "S3 service endpoint required"
                );
            success = false;
            }

        if ((template == null) || (template.isEmpty()))
            {
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "S3 service template required"
                );
            success = false;
            }

        if ((bucket == null) || (bucket.isEmpty()))
            {
            state.getOfferSetEntity().addWarning(
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
        StorageResourceValidator.Result storageResult = null;

        //
        // Everything is good, so accept the request.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            log.debug("Success");

            log.debug("Creating Builder.");
            Builder<AbstractDataResourceEntity> builder = new Builder<AbstractDataResourceEntity>()
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
            
            log.debug("Creating Result.");
            DataResourceValidator.Result dataResult = new DataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );

            //
            // Save the DataResource in the state.
            state.addDataValidatorResult(
                dataResult
                );
            //
            // Save the StorageResource in the state.
            /*
             * Probably already done.
            state.addStorageValidatorResult(
                storageResult
                );
             * 
             */
            // Add the link between the DataResource and StorageResource.
            state.addDataStorageResult(
                dataResult,
                storageResult
                );

            return dataResult ;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            state.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }
    }
