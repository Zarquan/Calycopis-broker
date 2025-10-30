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

package net.ivoa.calycopis.datamodel.storage.simple;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * A SimpleStorageResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SimpleStorageResourceEntityFactoryImpl
    extends FactoryBaseImpl
    implements SimpleStorageResourceEntityFactory
    {

    private final SimpleStorageResourceEntityRepository repository;

    @Autowired
    public SimpleStorageResourceEntityFactoryImpl(
        final SimpleStorageResourceEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<SimpleStorageResourceEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    public SimpleStorageResourceEntity create(
        final SessionEntity session,
        final AbstractStorageResourceValidator.Result result)
        {
        return this.repository.save(
            new SimpleStorageResourceEntity(
                session,
                result
                )
            );
        }
    }

