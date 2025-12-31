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

package net.ivoa.calycopis.functional.processing.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Component
public class DataResourceProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements DataResourceProcessingRequestFactory
    {

    private final DataResourceProcessingRequestRepository repository;

    @Autowired
    public DataResourceProcessingRequestFactoryImpl(final DataResourceProcessingRequestRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareDataResourceRequest createPrepareDataResourceRequest(AbstractDataResourceEntity data)
        {
        return repository.save(
            new PrepareDataResourceRequestEntity(
                data
                )
            );
        }

    @Override
    public void delete(final DataResourceProcessingRequest request)
        {
        if (request instanceof DataResourceProcessingRequestEntity)
            {
            repository.delete(
                (DataResourceProcessingRequestEntity)request
                );  
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected request implementation [{" +request.getClass().getName() + "}]" 
                );
            }
        }

    }
