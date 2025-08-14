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
public class ExecutionSessionEntityFactoryImpl
    extends FactoryBaseImpl
    implements ExecutionSessionEntityFactory
    {

    private final ExecutionSessionEntityRepository repository;

    @Autowired
    public ExecutionSessionEntityFactoryImpl(final ExecutionSessionEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<ExecutionSessionEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public ExecutionSessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer)
        {
        return this.repository.save(
            new ExecutionSessionEntity(
                parent,
                context,
                offer
                )
            );
        }

    @Override
    // TODO return an UpdateContext, with entity, result and messages.
    public Optional<ExecutionSessionEntity> update(final UUID uuid, final IvoaAbstractUpdate update)
        {
        log.debug("update(UUID)");
        log.debug("UUID   [{}]", uuid);
        log.debug("Update [{}]", update.getClass());

        Optional<ExecutionSessionEntity> result = this.repository.findById(
            uuid
            );
        if (result.isEmpty())
            {
            return result ;
            }
        else {
            ExecutionSessionEntity entity = update(
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
    protected ExecutionSessionEntity update(final ExecutionSessionEntity entity , final IvoaAbstractUpdate update)
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
    protected ExecutionSessionEntity update(final ExecutionSessionEntity entity , final IvoaEnumValueUpdate update)
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
                                    for (ExecutionSessionEntity sibling : entity.getOfferSet().getOffers())
                                        {
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
    public List<ExecutionSessionEntity> select(final IvoaExecutionSessionPhase phase)
        {
        return repository.findByPhase(
            phase
            );
        }

    @Override
    public ExecutionSessionEntity save(final ExecutionSessionEntity entity)
        {
        return repository.save(
            entity
            );
        }
    }

