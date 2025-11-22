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

package net.ivoa.calycopis.datamodel.data.skao;

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
 * A IvoaDataResource Factory implementation.
 *
 */
@Slf4j
@Component
public class SkaoDataResourceEntityFactoryImpl
    extends AbstractDataResourceFactoryImpl
    implements SkaoDataResourceEntityFactory
    {

    /**
     * 
     */
    private final SkaoDataResourceEntityRepository repository;

    @Autowired
    public SkaoDataResourceEntityFactoryImpl(
        final SkaoDataResourceEntityRepository entityRepository
        ){
        super();
        this.repository = entityRepository;
        }

    @Override
    public Optional<SkaoDataResourceEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public SkaoDataResourceEntity create(
        final ExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        return this.repository.save(
            new SkaoDataResourceEntity(
                session,
                storage,
                result
                )
            );
        }
    }

