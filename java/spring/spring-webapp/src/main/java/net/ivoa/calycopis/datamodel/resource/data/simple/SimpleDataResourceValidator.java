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
package net.ivoa.calycopis.datamodel.resource.data.simple;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.builder.Builder;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.ValidatorTools;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageSize;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageSizeRequested;

/**
 * A Validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleDataResourceValidator
extends ValidatorTools
implements AbstractDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     * 
     */
    final SimpleDataResourceEntityFactory entityFactory;

    /**
     * The set of StorageResource Validators.
     * 
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;
    
    /**
     * Public constructor.
     * 
     */
    public SimpleDataResourceValidator(
        final SimpleDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super();
        this.entityFactory = entityFactory ;
        this.storageValidators = storageValidators ;
        }
    
    @Override
    public AbstractDataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", context.makeDataValidatorResultKey(requested), requested.getClass().getName());
        if (requested instanceof IvoaSimpleDataResource)
            {
            return validate(
                (IvoaSimpleDataResource) requested,
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
     * Validate a simple data resource.
     *
     */
    public AbstractDataResourceValidator.Result validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}]", context.makeDataValidatorResultKey(requested));

        boolean success = true ;
        
        //
        // Check for a duplicate resource.
        AbstractDataResourceValidator.Result duplicate = context.findDataValidatorResult(
            requested
            );
        if (duplicate != null)
            {
            context.getOfferSetEntity().addWarning(
                "urn:duplicate-resource",
                "Duplicate data resource found [${requested}][${duplicate }]",
                Map.of(
                    "requested",
                    context.makeDataValidatorResultKey(requested),
                    "duplicate",
                    context.makeDataValidatorResultKey(duplicate)
                    )
                );
            success = false ;
            }
        
        //
        // Create our validated object.
        IvoaSimpleDataResource validated = new IvoaSimpleDataResource();

        validated.setUuid(
            requested.getUuid()
            );
        validated.setName(
            trim(requested.getName())
            );

        String location = trim(
            requested.getLocation()
            );
        validated.setLocation(location);
        if ((location == null) || (location.isEmpty()))
            {
            context.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            success = false ;
            }
            
        //
        // Make a guess at the data size.
        // TODO Add optional size to data resource.
        long size = 1024L;
        
        Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity> storageResult = null;
        
        //
        // If the data resource has a storage reference.
        if (requested.getStorage() != null)
            {
            // Try to find the storage resource.
            storageResult = context.findStorageValidatorResult(
                requested.getStorage()
                );
            //
            // If we found a storage resource.
            if (storageResult != null)
                {
                // 
                // Check the result has an object.
                IvoaAbstractStorageResource storageResource = storageResult.getObject(); 
                if (storageResource != null)
                    {
                    //
                    // Check the size is big enough.
                    if (storageResource instanceof IvoaSimpleStorageResource)
                        {
                        //
                        // Check the size is big enough.
                        Long min = ((IvoaSimpleStorageResource) storageResource).getSize().getRequested().getMin(); 
                        Long max = ((IvoaSimpleStorageResource) storageResource).getSize().getRequested().getMax();
                        if ((min >= size) && (max >= size))
                            {
                            log.debug("PASS : Storage is big enough [{}][{}][{}]", size, min, max);
                            }
                        else {
                            log.warn("FAIL : Storage is NOT big enough [{}][{}][{}]", size, min, max);
                            context.getOfferSetEntity().addWarning(
                                "urn:size-error",
                                "Storage resource [${storagename}][${storagesize}] is not big enough for data [${dataname}][${datasize}]",
                                Map.of(
                                    "storagename",
                                    context.makeStorageValidatorResultKey(storageResource),
                                    "storagesize",
                                    min,
                                    "dataname",
                                    context.makeDataValidatorResultKey(requested),
                                    "datasize",
                                    size
                                    )
                                );
                            success = false ;
                            }
                        }
                    else {
                        log.warn("Unexpected storage type [{}]", storageResource.getClass().getName());
                        }
                    }
                // If the storage result doesn't have an object.
                else {
                    log.error("StorageResult has null object [{}]", storageResult);
                    context.getOfferSetEntity().addWarning(
                        "urn:resource-not-found",
                        "Unable to find storage resource [${storageref}]",
                        Map.of(
                            "storageref",
                            requested.getStorage()
                            )
                        );
                    success = false ;
                    }
                }
            //
            // If we couldn't find the storage resource.
            else {
                context.getOfferSetEntity().addWarning(
                    "urn:resource-not-found",
                    "Unable to find storage resource [${storageref}]",
                    Map.of(
                        "storageref",
                        requested.getStorage()
                        )
                    );
                success = false ;
                }
            }
        //
        // If the data resource doesn't have a storage reference.
        else {
            // Create a request for a new StorageResource.
            IvoaSimpleStorageResource storageResource = new IvoaSimpleStorageResource();
            storageResource.setName("Storage for [" + context.makeDataValidatorResultKey(requested) + "]");
            storageResource.setSize(
                new IvoaSimpleStorageSize()
                );
            storageResource.getSize().setRequested(
                new IvoaSimpleStorageSizeRequested()
                );
            storageResource.getSize().getRequested().setMin(size);
            storageResource.getSize().getRequested().setMax(size);

            //
            // Validate the new StorageResource.
            storageResult = storageValidators.validate(
                storageResource,
                context
                );
            
            //
            // If the new storage was accepted, add a reference to our data resource.
            if (ResultEnum.ACCEPTED.equals(storageResult.getEnum()))
                {
                validated.setStorage(
                    context.makeStorageValidatorResultKey(
                        storageResult.getObject()
                        )
                    );
                }
            else {
                context.getOfferSetEntity().addWarning(
                    "urn:storage-required",
                    "Unable to create new storage resource",
                    Map.of(
                        "storageref",
                        context.makeStorageValidatorResultKey(
                            storageResource
                            )
                        )
                    );
                success = false ;
                }
            }
        //
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            log.debug("Success");

            log.debug("Creating Builder.");
            Builder<AbstractDataResourceEntity> builder = new Builder<AbstractDataResourceEntity>()
                {
                @Override
                public SimpleDataResourceEntity build(ExecutionSessionEntity parent)
                    {
                    return entityFactory.create(
                        parent,
                        validated
                        );
                    }
                }; 
            
            log.debug("Creating Result.");
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
/*
 * 
            state.addDataStorageResult(
                dataResult,
                storageResult
                );
 * 
 */

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
