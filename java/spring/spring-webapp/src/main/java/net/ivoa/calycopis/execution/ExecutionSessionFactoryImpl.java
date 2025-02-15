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

package net.ivoa.calycopis.execution;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offerset.OfferSetEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * An ExecutionSessionFactory implementation.
 *
 */
@Slf4j
@Component
public class ExecutionSessionFactoryImpl
    extends FactoryBaseImpl
    implements ExecutionSessionFactory
    {

    private final ExecutionSessionRepository repository;

    @Autowired
    public ExecutionSessionFactoryImpl(final ExecutionSessionRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<ExecutionSessionEntity> select(UUID uuid)
        {
        log.debug("select(UUID)");
        log.debug("UUID [{}]", uuid);
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context)
        {
        return this.create(
            offerblock,
            parent,
            context,
            IvoaExecutionSessionPhase.OFFERED,
            true
            );
        }
    
    @Override
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context, final IvoaExecutionSessionPhase phase)
        {
        return this.create(
            offerblock,
            parent,
            context,
            phase,
            true
            );
        }
    
    @Override
    public ExecutionSessionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParserState context, final IvoaExecutionSessionPhase phase, boolean save)
        {
        ExecutionSessionEntity created = new ExecutionSessionEntity(
            offerblock,
            parent,
            context,
            phase
            );
        log.debug("created [{}]", created.getUuid());
        if (save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
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
    protected ExecutionSessionEntity update(final ExecutionSessionEntity entity , final IvoaEnumValueUpdate update)
        {
        log.debug("update(Entity, ValueUpdate)");
        log.debug("Entity [{}]", entity.getUuid());
        log.debug("Update [{}][{}]", update.getPath(), update.getValue());
        switch(update.getPath())
            {
            case "phase" :
                IvoaExecutionSessionPhase oldstate = entity.getPhase();
                IvoaExecutionSessionPhase newstate = oldstate;
                try {
                    newstate = IvoaExecutionSessionPhase.fromValue(
                        update.getValue()
                        );
                    }
                catch (IllegalArgumentException ouch)
                    {
                    // Unknown state.
                    }
                //
                // If this is a change.
                if (newstate != oldstate)
                    {
                    switch(oldstate)
                        {
                        case OFFERED :
                            switch(newstate)
                                {
                                case ACCEPTED:
                                    entity.setPhase(
                                        IvoaExecutionSessionPhase.ACCEPTED
                                        );
                                    /*
                                     * 
                                    entity.getParent().setAccepted(
                                        entity
                                        );
                                     * 
                                     */
                                    for (ExecutionSessionEntity sibling : entity.getParent().getOffers())
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
    }

