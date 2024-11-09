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
import net.ivoa.calycopis.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;

/**
 * An Execution Factory implementation.
 *
 */
@Slf4j
@Component
public class ExecutionFactoryImpl
    extends FactoryBaseImpl
    implements ExecutionFactory
    {

    private final ExecutionRepository repository;

    @Autowired
    public ExecutionFactoryImpl(final ExecutionRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<ExecutionEntity> select(UUID uuid)
        {
        log.debug("select(UUID)");
        log.debug("UUID [{}]", uuid);
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context)
        {
        return this.create(
            offerblock,
            parent,
            context,
            IvoaExecutionSessionStatus.OFFERED,
            true
            );
        }
    
    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state)
        {
        return this.create(
            offerblock,
            parent,
            context,
            state,
            true
            );
        }
    
    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state, boolean save)
        {
        ExecutionEntity created = new ExecutionEntity(
            offerblock,
            parent,
            context,
            state
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
    public Optional<ExecutionEntity> update(final UUID uuid, final IvoaAbstractUpdate update)
        {
        log.debug("update(UUID)");
        log.debug("UUID   [{}]", uuid);
        log.debug("Update [{}]", update.getClass());

        Optional<ExecutionEntity> result = this.repository.findById(
            uuid
            );
        if (result.isEmpty())
            {
            return result ;
            }
        else {
            ExecutionEntity entity = update(
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
    protected ExecutionEntity update(final ExecutionEntity entity , final IvoaAbstractUpdate update)
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
    protected ExecutionEntity update(final ExecutionEntity entity , final IvoaEnumValueUpdate update)
        {
        log.debug("update(Entity, ValueUpdate)");
        log.debug("Entity [{}]", entity.getUuid());
        log.debug("Update [{}][{}]", update.getPath(), update.getValue());
        switch(update.getPath())
            {
            case "state" :
                IvoaExecutionSessionStatus oldstate = entity.getState();
                IvoaExecutionSessionStatus newstate = oldstate;
                try {
                    newstate = IvoaExecutionSessionStatus.fromValue(
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
                                    entity.setState(
                                        IvoaExecutionSessionStatus.ACCEPTED
                                        );
                                    /*
                                     * 
                                    entity.getParent().setAccepted(
                                        entity
                                        );
                                     * 
                                     */
                                    for (ExecutionEntity sibling : entity.getParent().getOffers())
                                        {
                                        if (sibling != entity)
                                            {
                                            sibling.setState(
                                                IvoaExecutionSessionStatus.REJECTED
                                                );
                                            }
                                        }
                                    break;
                                case REJECTED:
                                    entity.setState(
                                        IvoaExecutionSessionStatus.REJECTED
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

