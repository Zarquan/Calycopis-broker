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
 * Using Jackson subtypes
 * https://stacktobasics.com/jackson-sub-types
 *
 */

package uk.co.metagrid.ambleck.webapp;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;

import uk.co.metagrid.ambleck.webapp.RequestApiDelegate;

import uk.co.metagrid.ambleck.model.OfferStatus;
import uk.co.metagrid.ambleck.model.OffersRequest;
import uk.co.metagrid.ambleck.model.OffersResponse;
import uk.co.metagrid.ambleck.model.AbstractExecutable;
import uk.co.metagrid.ambleck.model.DockerContainer01;
import uk.co.metagrid.ambleck.model.ExecutionFull;

@Service
public class RequestApiDelegateImpl implements RequestApiDelegate {

    private final NativeWebRequest request;

    @Autowired
    public RequestApiDelegateImpl(NativeWebRequest request) {
        this.request = request;
        }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
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

        if (request.getExecutable() instanceof DockerContainer01)
            {
            ExecutionFull offer = new ExecutionFull();

            offer.setExecutable(
                handle(
                    (DockerContainer01) request.getExecutable()
                    )
                );
            OfferStatus status = new OfferStatus();
            status.status(
                OfferStatus.StatusEnum.OFFERED
                );
            status.expires(
                OffsetDateTime.now().plusMinutes(5)
                );
            offer.setOffer(
                status
                );
            response.addOffersItem(
                 offer
                );
            response.setResult(
                OffersResponse.ResultEnum.YES
                );
            }
        else {
            response.setResult(
                OffersResponse.ResultEnum.NO
                );
            }

        return response ;

        }

    public DockerContainer01 handle(DockerContainer01 request)
        {
        return request;
        }
    }


