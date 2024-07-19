/*
 *
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
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.OffsetDateTime;

import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;

import uk.co.metagrid.ambleck.webapp.RequestApiDelegate;

import uk.co.metagrid.ambleck.model.MessageItem;
import uk.co.metagrid.ambleck.model.OffersRequest;
import uk.co.metagrid.ambleck.model.OffersResponse;
import uk.co.metagrid.ambleck.model.AbstractExecutable;
import uk.co.metagrid.ambleck.model.DockerContainer01;
import uk.co.metagrid.ambleck.model.ExecutionResponse;

@Service
public class RequestApiDelegateImpl implements RequestApiDelegate {

    private final NativeWebRequest request;

    public String getUrl() {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString();
            }
        else {
            return "unknown";
            }
        }

    @Autowired
    public RequestApiDelegateImpl(NativeWebRequest request) {
        this.request = request;
        }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
        }

    @Override
    public ResponseEntity<OffersResponse> offersRequest(
        @RequestBody OffersRequest request
        ) {
        return new ResponseEntity<OffersResponse>(
            response(
                request
                ),
            HttpStatus.OK
            );
	    }

    public OffersResponse response(OffersRequest request)
        {
	    OffersResponse response = new OffersResponse();
        response.setUuid(
            UuidCreator.getTimeBased()
            );
        response.setName(
            "My offers"
            );
        response.setHref(
            "https://..../offerset/" + response.getUuid()
            );
        response.expires(
            OffsetDateTime.now().plusMinutes(5)
            );

        // Long way round ..
        response.addMessagesItem(
            new MessageItem()
                .time(
                    OffsetDateTime.now()
                    )
                .type(
                    "https://example.org/message-types/debug"
                    )
                .template(
                    "[{code}] HttpServletRequest [{url}]"
                    )
                .putValuesItem(
                    "code",
                    "DEBUG"
                    )
                .putValuesItem(
                    "url",
                    this.getUrl()
                    )
            );

        if (request.getExecutable() instanceof DockerContainer01)
            {
            ExecutionResponse execution = new ExecutionResponse();
            execution.setUuid(
                UuidCreator.getTimeBased()
                );
            execution.setName(
                "My execution"
                );
            execution.setHref(
                "https://..../execution/" + execution.getUuid()
                );
            execution.setExecutable(
                this.handle(
                    (DockerContainer01) request.getExecutable()
                    )
                );
            execution.setState(
                ExecutionResponse.StateEnum.OFFERED
                );

            response.addOffersItem(
                 execution
                );
            response.setResult(
                OffersResponse.ResultEnum.YES
                );
            }
        else {
            response.setResult(
                OffersResponse.ResultEnum.NO
                );
            MessageItem message = new MessageItem();
            message.time(
                OffsetDateTime.now()
                );
            message.type(
                "https://example.org/message-types/unknown-executable"
                );
            message.template(
                "[{code}] Unable to provide requested executable [{type}]"
                );
            message.putValuesItem(
                "code",
                "ERROR"
                );
            message.putValuesItem(
                "type",
                request.getExecutable().getType()
                );
            message.message(
                "[2415] Unable to provide requested executable [" + request.getExecutable().getType() + "]"
                );
            response.addMessagesItem(
                message
                );
            }
        return response ;
        }

    public DockerContainer01 handle(DockerContainer01 request)
        {
        return request;
        }
    }


