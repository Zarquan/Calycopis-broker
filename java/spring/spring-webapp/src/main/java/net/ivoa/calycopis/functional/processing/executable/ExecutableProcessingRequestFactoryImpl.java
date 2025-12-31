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

package net.ivoa.calycopis.functional.processing.executable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Component
public class ExecutableProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements ExecutableProcessingRequestFactory
    {

    private final ExecutableProcessingRequestRepository repository;

    @Autowired
    public ExecutableProcessingRequestFactoryImpl(final ExecutableProcessingRequestRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareExecutableRequest createPrepareExecutableRequest(AbstractExecutableEntity executable)
        {
        return repository.save(
            new PrepareExecutableRequestEntity(
                executable
                )
            );
        }

    @Override
    public void delete(final ExecutableProcessingRequest request)
        {
        if (request instanceof ExecutableProcessingRequestEntity)
            {
            repository.delete(
                (ExecutableProcessingRequestEntity)request
                );  
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected request implementation [{" +request.getClass().getName() + "}]" 
                );
            }
        }
    
    }
