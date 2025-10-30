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

package net.ivoa.calycopis.datamodel.session;

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
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * An ExecutionSessionFactory implementation.
 *
 */
@Slf4j
@Component
public class SessionEntityFactoryImpl
    extends FactoryBaseImpl
    implements SessionEntityFactory
    {

    private final SessionEntityRepository repository;

    @Autowired
    public SessionEntityFactoryImpl(final SessionEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<SessionEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public SessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer)
        {
        return this.repository.save(
            new SessionEntity(
                parent,
                context,
                offer
                )
            );
        }

    @Override
    public List<SessionEntity> select(final IvoaExecutionSessionPhase phase)
        {
        return repository.findByPhase(
            phase
            );
        }

    @Override
    public SessionEntity save(final SessionEntity entity)
        {
        return repository.save(
            entity
            );
        }
    }

