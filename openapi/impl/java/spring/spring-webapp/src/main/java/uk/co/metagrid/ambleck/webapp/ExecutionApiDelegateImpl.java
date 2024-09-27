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

package uk.co.metagrid.ambleck.webapp;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse;
import net.ivoa.calycopis.openapi.model.IvoaUpdateRequest;
import net.ivoa.calycopis.openapi.webapp.ExecutionApiDelegate;
import uk.co.metagrid.ambleck.model.ExecutionResponseFactory;

@Service
public class ExecutionApiDelegateImpl
    extends BaseDelegateImpl
    implements ExecutionApiDelegate
    {

    private final ExecutionResponseFactory factory ;

    @Autowired
    public ExecutionApiDelegateImpl(
        NativeWebRequest request,
        ExecutionResponseFactory factory
        )
        {
        super(request);
        this.factory = factory ;
        }

    @Override
    public ResponseEntity<IvoaExecutionResponse> executionGet(
        final UUID uuid
        ) {
        IvoaExecutionResponse response = factory.select(uuid);
        if (null != response)
            {
            return new ResponseEntity<IvoaExecutionResponse>(
                response,
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<IvoaExecutionResponse> executionPost(
        final UUID uuid,
        final IvoaUpdateRequest request
        ) {
        IvoaExecutionResponse response = factory.update(
            uuid,
            request.getUpdate()
            );
        if (null != response)
            {
            return new ResponseEntity<IvoaExecutionResponse>(
                response,
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
                );
            }
        }
    }


