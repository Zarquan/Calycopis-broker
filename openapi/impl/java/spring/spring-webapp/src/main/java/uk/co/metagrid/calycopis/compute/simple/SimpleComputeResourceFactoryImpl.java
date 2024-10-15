/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package uk.co.metagrid.calycopis.compute.simple;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.factory.FactoryBaseImpl;

/**
 * A SimpleComputeResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SimpleComputeResourceFactoryImpl
    extends FactoryBaseImpl
    implements SimpleComputeResourceFactory
    {

    private final SimpleComputeResourceRepository repository;

    @Autowired
    public SimpleComputeResourceFactoryImpl(final SimpleComputeResourceRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<SimpleComputeResourceEntity> select(UUID uuid)
        {
        Optional<SimpleComputeResourceEntity> optional = this.repository.findById(
            uuid
            );
        if (optional.isPresent())
            {
            SimpleComputeResourceEntity found = optional.get();
            found.addMessage(
                LevelEnum.DEBUG,
                "urn:debug",
                "SimpleComputeResourceEntity select(UUID)",
                Collections.emptyMap()
                );
            return Optional.of(
                 this.repository.save(
                     found
                     )
                );
            }
        else {
            return Optional.ofNullable(
                null
                );
            }
        }

    @Override
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final String name, Long requestedcores, Long offeredcores, Long requestedmemory, Long offeredmemory)
        {
        return this.create(
            parent,
            name,
            requestedcores,
            offeredcores,
            requestedmemory,
            offeredmemory,
            true
            );
        }
    
    @Override
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final String name, Long requestedcores, Long offeredcores, Long requestedmemory, Long offeredmemory, boolean save)
        {
        SimpleComputeResourceEntity created = new SimpleComputeResourceEntity(
            parent,
            name,
            requestedcores,
            offeredcores,
            requestedmemory,
            offeredmemory
            );
        log.debug("created [{}]", created.getUuid());
        if ((parent != null) && save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }

    @Override
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final SimpleComputeResourceEntity template)
        {
        log.debug("create(ExecutionEntity, SimpleComputeResourceEntity) [{}]", (template != null) ? template.getUuid() : "null-template");
        return this.create(
            parent,
            template.getName(),
            template.getRequestedCores(),
            template.getOfferedCores(),
            template.getRequestedMemory(),
            template.getOfferedMemory(),
            true
            );
        }

    @Override
    public SimpleComputeResourceEntity create(final ExecutionEntity parent, final SimpleComputeResourceEntity template, long offercores, long offermemory)
        {
        log.debug("create(ExecutionEntity, SimpleComputeResourceEntity) [{}]", (template != null) ? template.getUuid() : "null-template");
        return this.create(
            parent,
            template.getName(),
            template.getRequestedCores(),
            offercores,
            template.getRequestedMemory(),
            offermemory,
            true
            );
        }
    
    
    }

