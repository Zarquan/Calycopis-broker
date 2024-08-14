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

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.metagrid.ambleck.webapp.ExecutionApiDelegate;

import uk.co.metagrid.ambleck.model.ExecutionResponse;
import uk.co.metagrid.ambleck.model.ExecutionResponseFactory;

import uk.co.metagrid.ambleck.model.UpdateRequest;

import uk.co.metagrid.ambleck.message.DebugMessage;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.message.InfoMessage;

@Service
// https://saranganjana.medium.com/componentscan-in-spring-boot-ec828569df26
@ComponentScan("uk.co.metagrid.ambleck.model")
public class ExecutionApiDelegateImpl
    extends BaseDelegateImpl
    implements ExecutionApiDelegate {

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
    public ResponseEntity<ExecutionResponse> executionGet(
        final UUID uuid
        ) {
        ExecutionResponse response = factory.select(uuid);
        if (null != response)
            {
            return new ResponseEntity<ExecutionResponse>(
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
    public ResponseEntity<ExecutionResponse> executionPost(
        final UUID uuid,
        final UpdateRequest request
        ) {
        ExecutionResponse response = factory.update(
            uuid,
            request.getUpdate()
            );
        if (null != response)
            {
            return new ResponseEntity<ExecutionResponse>(
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


