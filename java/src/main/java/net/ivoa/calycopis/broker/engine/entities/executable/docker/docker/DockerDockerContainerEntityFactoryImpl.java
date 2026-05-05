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

package net.ivoa.calycopis.broker.engine.entities.executable.docker.docker;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 * Factory implementation for DockerDockerContainerEntity.
 *
 */
@Slf4j
@Component
public class DockerDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements DockerDockerContainerEntityFactory
    {

    private final DockerDockerContainerEntityRepository repository;

    @Autowired
    public DockerDockerContainerEntityFactoryImpl(
        final DockerDockerContainerEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractExecutableEntityImpl> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public DockerDockerContainerEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        ){
        DockerDockerContainerEntityImpl entity = this.repository.save(
            new DockerDockerContainerEntityImpl(
                session,
                result
                )
            );
        return entity;
        }
    }
