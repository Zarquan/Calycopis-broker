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

package net.ivoa.calycopis.datamodel.component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Component
public class AbstractLifecycleComponentEntityFactoryImpl
extends FactoryBaseImpl
implements AbstractLifecycleComponentEntityFactory
    {

    final AbstractExecutableEntityFactory      executableEntityFactory;
    final AbstractComputeResourceEntityFactory computeResourceEntityFactory;
    final AbstractDataResourceEntityFactory    dataResourceEntityFactory;
    final AbstractStorageResourceEntityFactory storageResourceEntityFactory;
    
    /**
     * 
     */
    public AbstractLifecycleComponentEntityFactoryImpl(
        final AbstractExecutableEntityFactory      executableEntityFactory,
        final AbstractComputeResourceEntityFactory computeResourceEntityFactory,
        final AbstractDataResourceEntityFactory    dataResourceEntityFactory,
        final AbstractStorageResourceEntityFactory storageResourceEntityFactory
        ){
        super();
        this.executableEntityFactory      = executableEntityFactory;
        this.computeResourceEntityFactory = computeResourceEntityFactory;
        this.dataResourceEntityFactory    = dataResourceEntityFactory;
        this.storageResourceEntityFactory = storageResourceEntityFactory;

        this.registerFactory(this.executableEntityFactory);
        this.registerFactory(this.computeResourceEntityFactory);
        this.registerFactory(this.dataResourceEntityFactory);
        this.registerFactory(this.storageResourceEntityFactory);
        
        }

    Map<URI, LifecycleComponentEntityFactory<?>> registry = new HashMap<URI, LifecycleComponentEntityFactory<?>>();

    void registerFactory(
        final LifecycleComponentEntityFactory<?> factory
        ){
        for (final URI kind : factory.getKinds())
            {
            this.registry.put(
                kind,
                factory
                );
            }
        }
    
    @Override
    public LifecycleComponentEntity select(final URI kind, final UUID uuid)
        {
        LifecycleComponentEntityFactory<?> factory = this.registry.get(kind);
        if (factory!=null)
            {
            return factory.select(uuid).orElse(null);
            }
        else {
            return null;
            }
        }
    }
