/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.asynchronous.AsyncSessionHandler;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Component
public class SessionEntityUpdateHandlerImpl
extends FactoryBaseImpl
implements SessionEntityUpdateHandler
    {

    private final SessionEntityRepository sessionRepository;
    private final AsyncSessionHandler asyncHandler;

    @Autowired
    public SessionEntityUpdateHandlerImpl(final SessionEntityRepository repository, final AsyncSessionHandler asyncHandler)
        {
        super();
        this.sessionRepository = repository;
        this.asyncHandler = asyncHandler;
        }

    @Override
    // TODO return an UpdateResult, with entity, result and messages.
    public Optional<SessionEntity> update(final UUID uuid, final IvoaAbstractUpdate update)
        {
        log.debug("update(UUID)");
        log.debug("UUID   [{}]", uuid);
        log.debug("Update [{}]", update.getClass());

        Optional<SessionEntity> result = this.sessionRepository.findById(
            uuid
            );
        if (result.isEmpty())
            {
            log.warn("Session not found [{}]", uuid);
            return result ;
            }
        else {
            SessionEntity entity = update(
                result.get(),
                update
                );  
            // Do we need this ?
            // The Sessions set to REJECTED are saved in the database too.
            entity = this.sessionRepository.save(
                entity
                );
            return Optional.of(
                entity
                );
            }
        }

    // TODO Pass in an UpdateResult, with entity, result and messages.
    protected SessionEntity update(SessionEntity entity , final IvoaAbstractUpdate update)
        {
        log.debug("update(Entity, Update)");
        log.debug("Entity [{}]", entity.getUuid());
        log.debug("Update [{}]", update.getClass());
        switch(update)
            {
            case IvoaEnumValueUpdate valueupdate :
                entity = this.update(
                    entity,
                    valueupdate
                    );
                break ;

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown update class [{}]", update.getClass().getName());
                break ;
            }
        return entity ;
        }

    // TODO Pass in an UpdateResult, with entity, result and messages.
    protected SessionEntity update(SessionEntity entity , final IvoaEnumValueUpdate update)
        {
        log.debug("update(Entity, EnumValueUpdate)");
        log.debug("Entity [{}][{}]", entity.getUuid(), entity.getPhase());
        log.debug("Update [{}][{}]", update.getPath(), update.getValue());
        switch(update.getPath())
            {
            case "phase" :
                try {
                    IvoaExecutionSessionPhase newphase = IvoaExecutionSessionPhase.fromValue(
                        update.getValue()
                        );
                    entity = this.update(
                        entity,
                        newphase
                        );
                    }
                catch (IllegalArgumentException ouch)
                    {
                    log.warn("Invalid update value [{}]", update.getValue());
                    }
                break ;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown phase path [{}]", update.getPath());
                break ;
            }
        return entity ;
        }

    protected SessionEntity update(SessionEntity entity , final IvoaExecutionSessionPhase newphase)
        {
        log.debug("update(Entity, Phase) [{}][{}][{}]", entity.getUuid(), entity.getPhase(), newphase);
        switch(newphase)
            {
            case ACCEPTED:
                entity = accept(
                    entity
                    );
                break ;
            case REJECTED:
                entity = reject(
                    entity
                    );
                break ;
            case CANCELLED:
                entity = cancel(
                    entity
                    );
                break ;
            case FAILED:
                entity = fail(
                    entity
                    );
                break ;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), newphase);
                break ;
            }
        return entity ;
        }

    protected SessionEntity accept(SessionEntity entity)
        {
        log.debug("accept(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case OFFERED:
                entity.setPhase(
                    IvoaExecutionSessionPhase.ACCEPTED
                    );
                //
                // REJECT the other Sessions in the offer.
                for (SessionEntity sibling : entity.getOfferSet().getOffers())
                    {
                    if (sibling.getUuid().equals(entity.getUuid()) == false)
                        {
                        reject(sibling);
                        }
                    }
                log.debug("Calling async handler for accepted session [{}]", entity.getUuid());
                asyncHandler.process(
                    entity.getUuid()
                    );
                log.debug("Back from async handler for accepted session [{}]", entity.getUuid());
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.ACCEPTED);
                break;
            }
        return entity ;
        }

    protected SessionEntity reject(SessionEntity entity)
        {
        log.debug("reject(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case OFFERED:
                entity.setPhase(
                    IvoaExecutionSessionPhase.REJECTED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.REJECTED);
                break;
            }
        return entity ;
        }

    protected SessionEntity cancel(SessionEntity entity)
        {
        log.debug("cancel(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case INITIAL:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case AVAILABLE:
            case RUNNING:
                entity.setPhase(
                    IvoaExecutionSessionPhase.CANCELLED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.CANCELLED);
                break;
            }
        return entity ;
        }

    // TODO This should require a reason.
    protected SessionEntity fail(SessionEntity entity)
        {
        log.debug("fail(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case INITIAL:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
                // If a sessions fails during releasing - has it actually failed ?
                entity.setPhase(
                    IvoaExecutionSessionPhase.FAILED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.FAILED);
                break;
            }
        return entity ;
        }
    }
