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

package net.ivoa.calycopis.datamodel.data;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;

/**
 * 
 */
@Slf4j
public abstract class AbstractDataResourceValidatorImpl
extends AbstractValidatorImpl
implements AbstractDataResourceValidator
    {
    /**
     * A factory for StorageResource Validators.
     *
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;

    /**
     * 
     */
    public AbstractDataResourceValidatorImpl(
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super();
        this.storageValidators = storageValidators ;
        }

    /**
     * Check our context for for a duplicate resource.
     * 
     */
    protected boolean duplicateCheck(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        AbstractDataResourceValidator.Result duplicate = context.findDataValidatorResult(
            requested
            );
        if (duplicate != null)
            {
            context.getOfferSetEntity().addWarning(
                "urn:duplicate-resource",
                "Duplicate data resource found [${requested}][${duplicate}]",
                Map.of(
                    "requested",
                    context.makeDataValidatorResultKey(requested),
                    "duplicate",
                    context.makeDataValidatorResultKey(duplicate)
                    )
                );
            success = false ;
            }
        return success;
        }

    /**
     * Find the corresponding storage resource.
     *  
     */
    protected AbstractStorageResourceValidator.Result storageCheck(
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        AbstractStorageResourceValidator.Result storageResult ;
        
        if (requested.getStorage() != null)
            {
            storageResult = context.findStorageValidatorResult(
                requested.getStorage()
                );
            if (storageResult != null)
                {
                if (ResultEnum.ACCEPTED.equals(storageResult.getEnum()))
                    {
                    IvoaAbstractStorageResource storageResource = storageResult.getObject(); 
                    if (null != storageResource)
                        {
                        validated.setStorage(
                            context.makeStorageValidatorResultKey(
                                storageResource 
                                )
                            );
                        }
                    else {
                        log.error("Storage result has null object [{}]", storageResult);
                        context.getOfferSetEntity().addWarning(
                            "urn:storage-required",
                            "Unable to assign storage resource",
                            Map.of(
                                "storageref",
                                requested.getStorage()
                                )
                            );
                        success = false ;
                        }
                    }
                else {
                    log.warn("Unexpected storage result state [{}]", storageResult.getEnum());
                    context.getOfferSetEntity().addWarning(
                        "urn:storage-required",
                        "Unable to assign storage resource",
                        Map.of(
                            "storageref",
                            requested.getStorage()
                            )
                        );
                    success = false ;
                    }
                }
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

        else {
            // TODO Replace this with the default storage pool for this platform.
            // Storage create is delegated to the Platform

// The CANFAR version of this should call Cavern to create a session directory in the user's home space.
// /users/home/<username>/sessions/<sessionid>
        
            // Create a request for a new StorageResource.
            IvoaSimpleStorageResource storageResource = new IvoaSimpleStorageResource();
            storageResource.setName("Storage for [" + context.makeDataValidatorResultKey(requested) + "]");

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
                if (null != storageResult.getObject())
                    {
                    log.debug("Adding storage reference [{}][{}]", storageResult.getObject().getName(),storageResult.getObject().getUuid());
                    validated.setStorage(
                        context.makeStorageValidatorResultKey(
                            storageResult.getObject()
                            )
                        );
                    }
                else {
                    log.error("Storage result has null object [{}]", storageResult);
                    success = false ;
                    }
                }
            else {
                log.warn("Unexpected storage result state [{}]", storageResult.getEnum());
                context.getOfferSetEntity().addWarning(
                    "urn:storage-required",
                    "Unable to assign storage resource",
                    Map.of(
                        "storageref",
                        requested.getStorage()
                        )
                    );
                success = false ;
                }
            }
        return storageResult;
        }
    }
