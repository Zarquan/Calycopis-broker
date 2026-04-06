/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-14T19:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 40,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data.simple.mock;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;

/**
 * 
 */
@Slf4j
public class MockSimpleDataResourceValidatorImpl
extends SimpleDataResourceValidatorImpl
implements MockSimpleDataResourceValidator
    {
    /**
     * A factory for StorageResource Validators.
     *
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;

    public MockSimpleDataResourceValidatorImpl(
        final SimpleDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            entityFactory
            );
        this.storageValidators = storageValidators ;
        }

    public static final List<String> EXCLUDED_LOCATIONS = List.of(
        "http://example.com/excluded.dat",
        "http://example.com/excluded.vot"
        );

    @Override
    protected boolean validateLocation(String location, OfferSetRequestParserContext context)
        {
        if (EXCLUDED_LOCATIONS.contains(location))
            {
            context.addWarning(
                "urn:invalid-value",
                "SimpleDataResource - location is excluded [${value}]",
                Map.of(
                    "value",
                    location
                    )
                );
            return false;
            }
        else {
            return true;
            }
        }

    /**
     * Default prepare duration, 30 seconds.
     * 
     */
    public static final Long DEFAULT_PREPARE_ESTIMATE = 30L;

    /**
     * Get the prepare duration for a resource.
     * Returns DEFAULT_PREPARE_ESTIMATE if the request does not specify a value.
     * 
     */
    protected Long getPrepareDuration(final IvoaSimpleDataResource validated)
        {
        Long duration = getPrepareDuration(
            validated.getSchedule()
            );
        if (duration != null)
            {
            return duration ;
            }
        else {
            return DEFAULT_PREPARE_ESTIMATE ;
            }
        }

    /**
     * Default release duration, 30 seconds.
     * 
     */
    public static final Long DEFAULT_RELEASE_ESTIMATE = 30L;

    /**
     * Get the release duration for a resource.
     * 
     */
    protected Long getReleaseDuration(final IvoaSimpleDataResource validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }

    /*
     * We are creating a storage resource for a specific data resource.
     * The type of storage depends on the type of the data resource and the platform.
     * Specifically, on the Docker platform, the storage resource can be a VolumeMount or a BindMount,
     * depending on the protocol of the data location file:// or http://.
     */
    @Override
    protected AbstractStorageResourceValidator.Result linkStorage(
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){
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
                        context.addWarning(
                            "urn:storage-required",
                            "Unable to assign storage resource",
                            Map.of(
                                "storageref",
                                requested.getStorage()
                                )
                            );
                        }
                    }
                else {
                    log.warn("Unexpected storage result state [{}]", storageResult.getEnum());
                    context.addWarning(
                        "urn:storage-required",
                        "Unable to assign storage resource",
                        Map.of(
                            "storageref",
                            requested.getStorage()
                            )
                        );
                    }
                }
            else {
                context.addWarning(
                    "urn:resource-not-found",
                    "Unable to find storage resource [${storageref}]",
                    Map.of(
                        "storageref",
                        requested.getStorage()
                        )
                    );
                }
            }

        else {
            // TODO Replace this with the default storage pool for this platform.

            // Create a new StorageResource template.
            IvoaSimpleStorageResource storageResource = new IvoaSimpleStorageResource()
                .kind(
                    SimpleStorageResource.TYPE_DISCRIMINATOR
                    )                    
                .meta(
                    new IvoaComponentMetadata().name(
                        "Storage for [" + context.makeDataValidatorResultKey(requested) + "]"
                        )
                );

            //
            // Validate the new StorageResource.
            storageValidators.validate(
                storageResource,
                context
                );
            // TODO Check the return value ?
            
            //
            // Find the validation result in the context.
            storageResult = context.findStorageValidatorResult(
                storageResource
                );

            //
            // If the new storage was accepted, add a reference to our data resource.
            if (storageResult != null)
                {
                // TODO This is almost but not quite the same as above.
                // Needs a refactor to remove duplication.
                if (null != storageResult.getObject())
                    {
                    log.debug("Adding storage reference [{}][{}]", storageResult.getObject().getMeta().getName(),storageResult.getObject().getMeta().getUuid());
                    validated.setStorage(
                        context.makeStorageValidatorResultKey(
                            storageResult.getObject()
                            )
                        );
                    }
                else {
                    log.error("Storage result has null object [{}]", storageResult);
                    }
                }
            else {
                log.warn("Storage validation failed");
                context.addWarning(
                    "urn:storage-required",
                    "Unable to assign storage resource"
                    );
                }
            }
        return storageResult;
        }
    }
