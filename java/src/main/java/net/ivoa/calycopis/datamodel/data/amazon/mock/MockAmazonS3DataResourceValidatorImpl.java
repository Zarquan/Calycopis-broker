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
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data.amazon.mock;

import net.ivoa.calycopis.datamodel.data.amazon.AmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.amazon.AmazonS3DataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.spring.model.IvoaS3DataResource;

/**
 * 
 */
public class MockAmazonS3DataResourceValidatorImpl
extends AmazonS3DataResourceValidatorImpl
implements MockAmazonS3DataResourceValidator
    {

    public MockAmazonS3DataResourceValidatorImpl(
        final AmazonS3DataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            entityFactory,
            storageValidators
            );
        }

    public static final Long DEFAULT_PREPARE_TIME = 5L;

    @Override
    protected Long estimatePrepareTime(final IvoaS3DataResource validated)
        {
        return DEFAULT_PREPARE_TIME;
        }
    }
