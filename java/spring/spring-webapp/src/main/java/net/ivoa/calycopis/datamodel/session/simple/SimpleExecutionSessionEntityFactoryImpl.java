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

package net.ivoa.calycopis.datamodel.session.simple;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;

/**
 * An ExecutionSessionFactory implementation.
 *
 */
@Slf4j
@Component
public class SimpleExecutionSessionEntityFactoryImpl
    extends FactoryBaseImpl
    implements SimpleExecutionSessionEntityFactory
    {

    private final SimpleExecutionSessionEntityRepository repository;

    @Autowired
    public SimpleExecutionSessionEntityFactoryImpl(final SimpleExecutionSessionEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<SimpleExecutionSessionEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public SimpleExecutionSessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer)
        {
        return this.repository.save(
            new SimpleExecutionSessionEntity(
                parent,
                context,
                offer
                )
            );
        }

    @Override
    public List<SimpleExecutionSessionEntity> select(final IvoaSimpleExecutionSessionPhase phase)
        {
        return repository.findByPhase(
            phase
            );
        }

    @Override
    public SimpleExecutionSessionEntity save(final SimpleExecutionSessionEntity entity)
        {
        return repository.save(
            entity
            );
        }
    }

