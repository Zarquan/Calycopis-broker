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

import uk.co.metagrid.ambleck.webapp.OffersetApiDelegate;

import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.OfferSetResponse;
import uk.co.metagrid.ambleck.model.OfferSetResponseFactory;

import uk.co.metagrid.ambleck.message.DebugMessage;
import uk.co.metagrid.ambleck.message.ErrorMessage;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.message.InfoMessage;

@Service
// https://saranganjana.medium.com/componentscan-in-spring-boot-ec828569df26
@ComponentScan("uk.co.metagrid.ambleck.model")
public class OffersetApiDelegateImpl
    extends BaseDelegateImpl
    implements OffersetApiDelegate {

    private final OfferSetResponseFactory factory ;

    @Autowired
    public OffersetApiDelegateImpl(
        NativeWebRequest request,
        OfferSetResponseFactory factory
        )
        {
        super(request);
        this.factory = factory ;
        }

    @Override
    public ResponseEntity<OfferSetResponse> offerSetGet(final UUID uuid)
        {
        OfferSetResponse response = factory.select(uuid);
        if (null != response)
            {
            return new ResponseEntity<OfferSetResponse>(
                response,
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<OfferSetResponse>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<OfferSetResponse> offerSetPost(
        @RequestBody OfferSetRequest request
        ) {
	    OfferSetResponse response = factory.create(
	        request
	        );
        response.setHref(
            this.getBaseUrl() + "/offerset/" + response.getUuid()
            );
/*
 *
        response.addMessagesItem(
            new DebugMessage(
                "HttpServletRequest [${uri}][${url}][${context}][${servlet}]",
                Map.of(
                    "uri",
                    this.getRequestURI(),
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
                "OfferSetResponseFactory [${uuid}]",
                Map.of(
                    "uuid",
                    this.factory.getUuid().toString()
                    )
                )
            );
 *
 */
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            URI.create(
                response.getHref()
                )
            );
        return new ResponseEntity<OfferSetResponse>(
            response,
            headers,
            HttpStatus.SEE_OTHER
            );
	    }
    }


