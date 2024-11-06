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

package net.ivoa.calycopis.data.simple;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.execution.ExecutionEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;

/**
 * A SimpleDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SimpleDataResourceFactoryImpl
    extends FactoryBaseImpl
    implements SimpleDataResourceFactory
    {

    private final SimpleDataResourceRepository repository;

    @Autowired
    public SimpleDataResourceFactoryImpl(final SimpleDataResourceRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<SimpleDataResourceEntity> select(UUID uuid)
        {
        Optional<SimpleDataResourceEntity> optional = this.repository.findById(
            uuid
            );
        if (optional.isPresent())
            {
            SimpleDataResourceEntity found = optional.get();
            found.addMessage(
                LevelEnum.DEBUG,
                "urn:debug",
                "SimpleDataResourceEntity select(UUID)",
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
    public SimpleDataResourceEntity create(final ExecutionEntity parent, final String name, final String location)
        {
        return this.create(
            parent,
            name,
            location,
            false
            );
        }

    @Override
    public SimpleDataResourceEntity create(final ExecutionEntity parent, final String name, final String location, boolean save)
        {
        SimpleDataResourceEntity created = new SimpleDataResourceEntity(
            parent,
            name,
            location
            );
        log.debug("created [{}]", created.getUuid());
        if (save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }
    }

