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

package net.ivoa.calycopis.functional.processing.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Component
public class StorageResourceProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements StorageResourceProcessingRequestFactory
    {

    private final StorageResourceProcessingRequestRepository repository;

    @Autowired
    public StorageResourceProcessingRequestFactoryImpl(final StorageResourceProcessingRequestRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareStorageResourceRequest createPrepareStorageResourceRequest(AbstractStorageResourceEntity storage)
        {
        return repository.save(
            new PrepareStorageResourceRequestEntity(
                storage
                )
            );
        }

    @Override
    public void delete(final StorageResourceProcessingRequest request)
        {
        if (request instanceof StorageResourceProcessingRequestEntity)
            {
            repository.delete(
                (StorageResourceProcessingRequestEntity)request
                );  
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected request implementation [{" +request.getClass().getName() + "}]" 
                );
            }
        }
    }
