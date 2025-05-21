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

package net.ivoa.calycopis.datamodel.resource.data.ivoa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceFactoryImpl;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResource;

/**
 * A IvoaDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class IvoaDataResourceEntityFactoryImpl
    extends AbstractDataResourceFactoryImpl
    implements IvoaDataResourceEntityFactory
    {

    private final IvoaDataResourceEntityRepository entityRepository;

    @Autowired
    public IvoaDataResourceEntityFactoryImpl(
        final IvoaDataResourceEntityRepository entityRepository
        ){
        super();
        this.entityRepository = entityRepository;
        }

    @Override
    public Optional<IvoaDataResourceEntity> select(UUID uuid)
        {
        return this.entityRepository.findById(
            uuid
            );
        }

    @Override
    public IvoaDataResourceEntity create(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaIvoaDataResource template)
        {
        return this.entityRepository.save(
            new IvoaDataResourceEntity(
                session,
                storage,
                template
                )
            );
        }
    }

