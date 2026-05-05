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

package net.ivoa.calycopis.broker.engine.entities.storage.docker;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.SimpleStorageResourceEntityFactoryImpl;

/**
 * 
 */
@Slf4j
@Component
public class DockerVolumeMountStorageEntityFactoryImpl
extends SimpleStorageResourceEntityFactoryImpl
implements DockerVolumeMountStorageEntityFactory
    {
    @Override
    public URI getKind()
        {
        return null;
        }

    private final DockerVolumeMountStorageEntityRepository repository;

    @Autowired
    public DockerVolumeMountStorageEntityFactoryImpl(
        final DockerVolumeMountStorageEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractStorageResourceEntityImpl> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    public DockerVolumeMountStorageEntity create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractStorageResourceValidator.Result result
        ){
        log.debug(
            "Creating DockerVolumeMountStorageEntity for [{}]",
            result.getObject()
            );
        DockerVolumeMountStorageEntity entity = this.repository.save(
            new DockerVolumeMountStorageEntity(
                session,
                result
                )
            );
        return entity ;
        }
    }
