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
 *
 */

package net.ivoa.calycopis.functional.processing.component;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityImpl;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Component
public class ComponentProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements ComponentProcessingRequestFactory
    {

    private final ComponentProcessingRequestRepository repository;
    
    public ComponentProcessingRequestFactoryImpl(final ComponentProcessingRequestRepository repository)
        {
        this.repository = repository;
        }

    @Override
    public ComponentProcessingRequestEntityImpl createPrepareComponentRequest(LifecycleComponentEntityImpl component)
        {
        return repository.save(
            new PrepareComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createMonitorComponentRequest(LifecycleComponentEntityImpl component)
        {
        return repository.save(
            new MonitorComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createReleaseComponentRequest(LifecycleComponentEntityImpl component)
        {
        return repository.save(
            new ReleaseComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createCancelComponentRequest(LifecycleComponentEntityImpl component)
        {
        return repository.save(
            new CancelComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createFailComponentRequest(LifecycleComponentEntityImpl component)
        {
        return repository.save(
            new FailComponentRequestEntity(
                component
                )
            );
        }
    }
