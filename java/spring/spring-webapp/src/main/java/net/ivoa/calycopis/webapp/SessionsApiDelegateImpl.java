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

package net.ivoa.calycopis.webapp;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityUpdateHandler;
import net.ivoa.calycopis.openapi.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponseFour;
import net.ivoa.calycopis.openapi.model.IvoaUpdateRequest;
import net.ivoa.calycopis.openapi.webapp.SessionsApiDelegate;

@Service
public class SessionsApiDelegateImpl
    extends BaseDelegateImpl
    implements SessionsApiDelegate
    {

    private final SimpleExecutionSessionEntityFactory sessionFactory ;
    private final SimpleExecutionSessionEntityUpdateHandler updateHandler ;

    @Autowired
    public SessionsApiDelegateImpl(
        NativeWebRequest request,
        SimpleExecutionSessionEntityFactory sessionFactory,
        SimpleExecutionSessionEntityUpdateHandler updateHandler 
        )
        {
        super(request);
        this.sessionFactory = sessionFactory ;
        this.updateHandler = updateHandler ;
        }

    @Override
    public ResponseEntity<IvoaExecutionSessionResponseFour> executionSessionGet(
        final UUID uuid
        ) {
        final Optional<SimpleExecutionSessionEntity> found = sessionFactory.select(
            uuid
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaExecutionSessionResponseFour>(
                found.get().makeBean(
                    this.getURIBuilder()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaExecutionSessionResponseFour>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<IvoaExecutionSessionResponseFour> executionSessionPost(
        final UUID uuid,
        final IvoaAbstractUpdate request
        ) {
       final Optional<SimpleExecutionSessionEntity> found = updateHandler.update(
            uuid,
            request
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaExecutionSessionResponseFour>(
                found.get().makeBean(
                    this.getURIBuilder()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaExecutionSessionResponseFour>(
                HttpStatus.NOT_FOUND
                );
            }
        }
    }

