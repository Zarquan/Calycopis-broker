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

package net.ivoa.calycopis.datamodel.data.amazon;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceFactoryImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;

/**
 * A SimpleDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class AmazonS3DataResourceEntityFactoryImpl
    extends AbstractDataResourceFactoryImpl
    implements AmazonS3DataResourceEntityFactory
    {

    private final AmazonS3DataResourceEntityRepository entityRepository;

    @Autowired
    public AmazonS3DataResourceEntityFactoryImpl(
        final AmazonS3DataResourceEntityRepository entityRepository
        ){
        super();
        this.entityRepository = entityRepository;
        }

    @Override
    public Optional<AmazonS3DataResourceEntity> select(UUID uuid)
        {
        return this.entityRepository.findById(
            uuid
            );
        }

    @Override
    public AmazonS3DataResourceEntity create(
        final ExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        return this.entityRepository.save(
            new AmazonS3DataResourceEntity(
                session,
                storage,
                result
                )
            );
        }
    }

