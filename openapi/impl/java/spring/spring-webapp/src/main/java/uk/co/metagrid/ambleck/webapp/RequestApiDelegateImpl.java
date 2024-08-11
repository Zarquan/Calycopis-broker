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

import java.util.Map;
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

import uk.co.metagrid.ambleck.message.DebugMessage;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.message.InfoMessage;

@Service
public class RequestApiDelegateImpl implements RequestApiDelegate {

    private final BrokerDatabase database ;

    private final NativeWebRequest request;

    public String getRequestURL()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString();
            }
        else {
            return "unknown";
            }
        }

    public String getPathInfo()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getPathInfo();
            }
        else {
            return "unknown";
            }
        }

    public String getContextPath()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getContextPath();
            }
        else {
            return "unknown";
            }
        }

    public String getServletPath()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getServletPath();
            }
        else {
            return "unknown";
            }
        }

    @Autowired
    public RequestApiDelegateImpl(NativeWebRequest request, BrokerDatabase database) {
        this.request  = request;
        this.database = database;
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
	    OffersResponseImpl response = new OffersResponseImpl();
        response.setHref(
            "https://..../offerset/" + response.getUuid()
            );
        response.addMessagesItem(
            new DebugMessage(
                "HttpServletRequest [${url}][${context}][${servlet}]",
                Map.of(
                    "url",
                    this.getRequestURL(),
                    "context",
                    this.getContextPath(),
                    "servlet",
                    this.getServletPath()
                    )
                )
            );
        response.addMessagesItem(
            new InfoMessage(
                "BrokerDatabase [${uuid}]",
                Map.of(
                    "uuid",
                    this.database.getUuid().toString()
                    )
                )
            );
        database.addOffer(
            response
            );

        if (request.getExecutable() instanceof DockerContainer01)
            {
            ExecutionResponseImpl execution = new ExecutionResponseImpl(response);
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
            database.addExecution(
                execution
                );
            }
        else {
            response.addMessagesItem(
                new ErrorMessage(
                    "Unable to provide requested executable [${type}]",
                    Map.of(
                        "type",
                        request.getExecutable().getType()
                        )
                    )
                );
            }
        return (OffersResponse) response ;
        }

    public DockerContainer01 handle(DockerContainer01 request)
        {
        return request;
        }
    }


