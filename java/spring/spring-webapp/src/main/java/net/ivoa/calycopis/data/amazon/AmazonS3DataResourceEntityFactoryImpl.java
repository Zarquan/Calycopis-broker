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

package net.ivoa.calycopis.data.amazon;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaAmazonS3DataResource;

/**
 * A SimpleDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class AmazonS3DataResourceEntityFactoryImpl
    extends FactoryBaseImpl
    implements AmazonS3DataResourceEntityFactory
    {

    private final AmazonS3DataResourceEntityRepository repository;

    @Autowired
    public AmazonS3DataResourceEntityFactoryImpl(final AmazonS3DataResourceEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AmazonS3DataResourceEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public AmazonS3DataResourceEntity create(final ExecutionSessionEntity parent, final IvoaAmazonS3DataResource template)
        {
        return this.repository.save(
            new AmazonS3DataResourceEntity(
                parent,
                template
                )
            );
        }
    }

