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
    // TODO return an UpdateResult, with entity, result and messages.
    public Optional<SessionEntity> update(final UUID uuid, final IvoaAbstractUpdate update)
        {
        log.debug("update(UUID)");
        log.debug("UUID   [{}]", uuid);
        log.debug("Update [{}]", update.getClass());

        Optional<SessionEntity> result = this.repository.findById(
            uuid
            );
        if (result.isEmpty())
            {
            return result ;
            }
        else {
            SessionEntity entity = update(
                result.get(),
                update
                );  
            entity = this.repository.save(
                entity
                );
            return Optional.of(
                entity
                );
            }
        }

    // TODO Pass in an UpdateContext, with entity, result and messages.
    protected SessionEntity update(final SessionEntity entity , final IvoaAbstractUpdate update)
        {
        log.debug("update(Entity, Update)");
        log.debug("Entity [{}]", entity.getUuid());
        log.debug("Update [{}]", update.getClass());
        switch(update)
            {
            case IvoaEnumValueUpdate valueupdate :
                return this.update(
                    entity,
                    valueupdate
                    );

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                // This is an invalid request.
                return null ;
            }
        }

    // TODO Pass in an UpdateContext, with entity, result and messages.
    // TODO Move the phase change checking into the entity.
    protected SessionEntity update(final SessionEntity entity , final IvoaEnumValueUpdate update)
        {
        log.debug("update(Entity, ValueUpdate)");
        log.debug("Entity [{}][{}]", entity.getUuid(), entity.getPhase());
        log.debug("Update [{}][{}]", update.getPath(), update.getValue());
        switch(update.getPath())
            {
            case "phase" :
                IvoaExecutionSessionPhase oldphase = entity.getPhase();
                IvoaExecutionSessionPhase newphase = oldphase;
                try {
                    newphase = IvoaExecutionSessionPhase.fromValue(
                        update.getValue()
                        );
                    }
                catch (IllegalArgumentException ouch)
                    {
                    // Unknown state.
                    }
                //
                // If this is a change.
                if (newphase != oldphase)
                    {
                    // TODO This becomes a big matrix of state transitions.
                    // How do we split this into smaller chunks ?
                    switch(oldphase)
                        {
                        case OFFERED :
                            switch(newphase)
                                {
                                case ACCEPTED:
                                    //
                                    // ACCEPT this Session.
                                    entity.setPhase(
                                        IvoaExecutionSessionPhase.ACCEPTED
                                        );
                                    //
                                    // REJECT the other Sessions in the offer.
                                    for (SessionEntity sibling : entity.getOfferSet().getOffers())
                                        {
                                        // TODO compare by UUID not by reference.
                                        if (sibling != entity)
                                            {
                                            sibling.setPhase(
                                                IvoaExecutionSessionPhase.REJECTED
                                                );
                                            }
                                        }
                                    break;
                                case REJECTED:
                                    entity.setPhase(
                                        IvoaExecutionSessionPhase.REJECTED
                                        );
                                    break;
                                default:
                                    // Invalid state transition.
                                    break;
                                }
                            break;

                        default:
                            // Invalid state transition.
                            break;
                        }
                    }
                break;

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                // This is an invalid request.
                break;
            }
        return entity;
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

