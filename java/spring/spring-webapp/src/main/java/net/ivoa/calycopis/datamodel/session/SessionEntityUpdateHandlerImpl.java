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

    private final SessionEntityRepository repository;

    @Autowired
    public SessionEntityUpdateHandlerImpl(final SessionEntityRepository repository)
        {
        super();
        this.repository = repository;
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
            // Do we need this ?
            // The Sessions set to REJECTED are saved in the database too.
            entity = this.repository.save(
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

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown update class [{}]", update.getClass().getName());
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
                    log.debug("Invalid phase value [{}]", update.getValue());
                    }

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown phase path [{}]", update.getPath());
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
            case REJECTED:
                entity = reject(
                    entity
                    );
            case CANCELLED:
                entity = cancel(
                    entity
                    );
            case FAILED:
                entity = fail(
                    entity
                    );
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid update phase [{}]", newphase);
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
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.ACCEPTED);
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
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.REJECTED);
            }
        return entity ;
        }

    protected SessionEntity cancel(SessionEntity entity)
        {
        log.debug("cancel(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case PROPOSED:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case READY:
            case RUNNING:
                entity.setPhase(
                    IvoaExecutionSessionPhase.CANCELLED
                    );
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.CANCELLED);
            }
        return entity ;
        }

    // TODO This should require a reason.
    protected SessionEntity fail(SessionEntity entity)
        {
        log.debug("fail(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case PROPOSED:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case READY:
            case RUNNING:
            case RELEASING:
                // If a sessions fails during releasing - has it actually failed ?
                entity.setPhase(
                    IvoaExecutionSessionPhase.FAILED
                    );
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), IvoaExecutionSessionPhase.FAILED);
            }
        return entity ;
        }
    }
