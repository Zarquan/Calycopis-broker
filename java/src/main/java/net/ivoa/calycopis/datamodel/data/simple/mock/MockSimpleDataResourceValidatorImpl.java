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

import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * 
 */
public class MockSimpleDataResourceValidatorImpl
extends SimpleDataResourceValidatorImpl
implements MockSimpleDataResourceValidator
    {

    public MockSimpleDataResourceValidatorImpl(
        final SimpleDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            entityFactory,
            storageValidators
            );
        }

    public static final List<String> LOCATION_BLACKLIST = List.of(
        "http://example.com/blacklisted.dat",
        "http://example.com/forbidden.dat"
        );

    @Override
    protected boolean validateLocation(String location, OfferSetRequestParserContext context)
        {
        if (LOCATION_BLACKLIST.contains(location))
            {
            context.addWarning(
                "urn:invalid-value",
                "SimpleDataResource - location is blacklisted [${value}]",
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

    public static final Long DEFAULT_PREPARE_TIME = 5L;

    @Override
    protected Long estimatePrepareTime(final IvoaSimpleDataResource validated)
        {
        return DEFAULT_PREPARE_TIME;
        }
    }
