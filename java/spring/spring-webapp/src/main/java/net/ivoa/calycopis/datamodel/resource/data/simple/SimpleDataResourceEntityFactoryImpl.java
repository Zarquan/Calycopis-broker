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

package net.ivoa.calycopis.datamodel.resource.data.simple;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceFactoryImpl;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;

/**
 * A SimpleDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SimpleDataResourceEntityFactoryImpl
    extends AbstractDataResourceFactoryImpl
    implements SimpleDataResourceEntityFactory
    {

    private final SimpleDataResourceEntityRepository entityRepository;

    @Autowired
    public SimpleDataResourceEntityFactoryImpl(
        final SimpleDataResourceEntityRepository entityRepository
        )
        {
        super();
        this.entityRepository = entityRepository;
        }

    @Override
    public Optional<SimpleDataResourceEntity> select(UUID uuid)
        {
        return this.entityRepository.findById(
            uuid
            );
        }

    @Override
    public SimpleDataResourceEntity create(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaSimpleDataResource template)
        {
        return this.entityRepository.save(
            new SimpleDataResourceEntity(
                session,
                storage,
                template
                )
            );
        }
    }

