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

package net.ivoa.calycopis.functional.processing.test.compute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequestRepository;
import net.ivoa.calycopis.functional.processing.compute.ComputeResourceProcessingRequestFactory;

/**
 *
 */
@Component
public class TestComputeResourceProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements ComputeResourceProcessingRequestFactory
    {

    private final ComponentProcessingRequestRepository repository;

    @Autowired
    public TestComputeResourceProcessingRequestFactoryImpl(final ComponentProcessingRequestRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareComputeResourceRequest createPrepareComputeResourceRequest(AbstractComputeResourceEntity compute)
        {
        return repository.save(
            new PrepareComputeResourceRequestEntity(
                compute
                )
            );
        }

    @Override
    public ReleaseComputeResourceRequest createReleaseComputeResourceRequest(AbstractComputeResourceEntity compute)
        {
        return repository.save(
            new ReleaseComputeResourceRequestEntity(
                compute
                )
            );
        }

    @Override
    public CancelTestComputeResourceRequest createCancelComputeResourceRequest(AbstractComputeResourceEntity compute)
        {
        return repository.save(
            new CancelTestComputeResourceRequestEntity(
                compute
                )
            );
        }
    
    @Override
    public void delete(final ComputeResourceProcessingRequest request)
        {
        if (request instanceof ComputeResourceProcessingRequestEntity)
            {
            repository.delete(
                (ComputeResourceProcessingRequestEntity)request
                );  
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected request implementation [{" +request.getClass().getName() + "}]" 
                );
            }
        }
    }
