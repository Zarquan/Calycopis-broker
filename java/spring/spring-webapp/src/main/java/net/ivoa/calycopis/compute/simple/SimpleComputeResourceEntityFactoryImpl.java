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

package net.ivoa.calycopis.compute.simple;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

/**
 * A SimpleComputeResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SimpleComputeResourceEntityFactoryImpl
    extends FactoryBaseImpl
    implements SimpleComputeResourceEntityFactory
    {

    private final SimpleComputeResourceEntityRepository repository;

    @Autowired
    public SimpleComputeResourceEntityFactoryImpl(final SimpleComputeResourceEntityRepository repository)
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
    public SimpleComputeResourceEntity create(final ExecutionSessionEntity parent, final IvoaSimpleComputeResource template)
        {
        // TODO This is terrible code.
        log.debug("create(ExecutionSessionEntity , IvoaSimpleComputeResource) [{}][{}]", parent, template);
        return this.create(
            parent,
            template.getName(),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMin()  : null) : null),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMax()  : null) : null),
            ((template.getCores()  != null) ? ((template.getCores().getOffered()    != null) ? template.getCores().getOffered().getMin()    : null) : null),
            ((template.getCores()  != null) ? ((template.getCores().getOffered()    != null) ? template.getCores().getOffered().getMax()    : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMin() : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMax() : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getOffered()   != null) ? template.getMemory().getOffered().getMin()   : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getOffered()   != null) ? template.getMemory().getOffered().getMax()   : null) : null),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMinimal()  : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMinimal() : null) : null),
            true
            );
        }

    @Override
    public SimpleComputeResourceEntity create(
        final ExecutionSessionEntity parent,
        final IvoaSimpleComputeResource template,
        final OfferBlock offerBlock
        ){
        // TODO This is terrible code.
        log.debug("create(ExecutionSessionEntity , IvoaSimpleComputeResource, OfferBlock) [{}][{}]", parent, template);
        return this.create(
            parent,
            template.getName(),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMin()  : null) : null),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMax()  : null) : null),
            offerBlock.getCores(),
            offerBlock.getCores(),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMin() : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMax() : null) : null),
            offerBlock.getMemory(),
            offerBlock.getMemory(),
            ((template.getCores()  != null) ? ((template.getCores().getRequested()  != null) ? template.getCores().getRequested().getMinimal()  : null) : null),
            ((template.getMemory() != null) ? ((template.getMemory().getRequested() != null) ? template.getMemory().getRequested().getMinimal() : null) : null),
            true
            );
        }
    
    
    @Override
    public SimpleComputeResourceEntity create(
        final ExecutionSessionEntity parent,
        final String name,
        final Long minrequestedcores,
        final Long maxrequestedcores,
        final Long minofferedcores,
        final Long maxofferedcores,
        final Long minrequestedmemory,
        final Long maxrequestedmemory,
        final Long minofferedmemory,
        final Long maxofferedmemory,
        final Boolean minimalcores,
        final Boolean minimalmemory,
        boolean save
        ){
        SimpleComputeResourceEntity created = new SimpleComputeResourceEntity(
            parent,
            name,
            minrequestedcores,
            maxrequestedcores,
            minofferedcores,
            maxofferedcores,
            minrequestedmemory,
            maxrequestedmemory,
            minofferedmemory,
            maxofferedmemory,
            minimalcores,
            minimalmemory
            );
        log.debug("created [{}]", created.getUuid());
        if ((parent != null) && save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }
    }

