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

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.execution.ExecutionSessionFactory;
import net.ivoa.calycopis.execution.ExecutionSessionResponseBean;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaUpdateRequest;
import net.ivoa.calycopis.openapi.webapp.SessionsApiDelegate;

@Service
public class SessionsApiDelegateImpl
    extends BaseDelegateImpl
    implements SessionsApiDelegate
    {

    private final ExecutionSessionFactory factory ;

    @Autowired
    public SessionsApiDelegateImpl(
        NativeWebRequest request,
        ExecutionSessionFactory factory
        )
        {
        super(request);
        this.factory = factory ;
        }

    @Override
    public ResponseEntity<IvoaExecutionSessionResponse> executionSessionGet(
        final UUID uuid
        ) {
        final Optional<ExecutionSessionEntity> found = factory.select(
            uuid
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaExecutionSessionResponse>(
                new ExecutionSessionResponseBean(
                    this.getBaseUrl(),
                    found.get()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaExecutionSessionResponse>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<IvoaExecutionSessionResponse> executionSessionPost(
        final UUID uuid,
        final IvoaUpdateRequest request
        ) {
       final Optional<ExecutionSessionEntity> found = factory.update(
            uuid,
            request.getUpdate()
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaExecutionSessionResponse>(
                new ExecutionSessionResponseBean(
                    this.getBaseUrl(),
                    found.get()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaExecutionSessionResponse>(
                HttpStatus.NOT_FOUND
                );
            }
        }
    }

