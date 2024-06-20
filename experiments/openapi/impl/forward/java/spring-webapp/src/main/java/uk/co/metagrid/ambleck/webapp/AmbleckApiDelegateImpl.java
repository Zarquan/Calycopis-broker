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

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;

import uk.co.metagrid.ambleck.webapp.AmbleckApiDelegate;
//import uk.co.metagrid.ambleck.webapp.AmbleckApi;

import uk.co.metagrid.ambleck.model.ParcolarOffer;
import uk.co.metagrid.ambleck.model.ParcolarRequest;
import uk.co.metagrid.ambleck.model.ParcolarResponse;

import uk.co.metagrid.ambleck.model.AbstractExecutable;

import uk.co.metagrid.ambleck.model.PingExecutable;
import uk.co.metagrid.ambleck.model.PingSpecific;

import uk.co.metagrid.ambleck.model.DelayExecutable;
import uk.co.metagrid.ambleck.model.DelaySpecific;

import uk.co.metagrid.ambleck.model.Resources;

import uk.co.metagrid.ambleck.model.AbstractComputeResource;
import uk.co.metagrid.ambleck.model.SimpleComputeResource;
import uk.co.metagrid.ambleck.model.SimpleComputeSpecific;

import uk.co.metagrid.ambleck.model.AbstractStorageResource;
import uk.co.metagrid.ambleck.model.SimpleStorageResource;
import uk.co.metagrid.ambleck.model.SimpleStorageSpecific;

@Service
public class AmbleckApiDelegateImpl implements AmbleckApiDelegate {

    private final NativeWebRequest request;

    @Autowired
    public AmbleckApiDelegateImpl(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public AbstractExecutable process(AbstractExecutable requested)
        {
        if (requested instanceof PingExecutable)
            {
            PingExecutable instance = (PingExecutable) requested;
            PingExecutable result = new PingExecutable("urn:ping-executable");
            result.setSpec(
                new PingSpecific().target(
                    instance.getSpec().getTarget()
                    )
                );
            result.setName(
                instance.getName()
                );
            return result;
            }
        else if (requested instanceof DelayExecutable)
            {
            DelayExecutable instance = (DelayExecutable) requested;
            DelayExecutable result = new DelayExecutable("urn:delay-executable");
            result.setSpec(
                new DelaySpecific().duration(
                    instance.getSpec().getDuration()
                    )
                );
            result.setName(
                instance.getName()
                );
            return result;
            }
        else {
            return null ;
            }
        }

    @Override
	public ResponseEntity<ParcolarResponse> ambleckPost(
	    @RequestBody ParcolarRequest request
	    ) {

	    ParcolarResponse response = new ParcolarResponse();
	    response.setResult(ParcolarResponse.ResultEnum.YES);

        for (int i = 0 ; i < 2 ; i++)
            {
            AbstractExecutable executable = process(
                request.getExecutable()
                );

            ParcolarOffer offer = new ParcolarOffer();
            offer.setExecutable(
                executable
                );
            offer.setResources(
                new Resources()
                );

            for (AbstractComputeResource requested : request.getResources().getCompute())
                {
                SimpleComputeResource offered = new SimpleComputeResource(
                    requested.getName()
                    );
                offered.setSpec(
                    new SimpleComputeSpecific().cores(
                        ((SimpleComputeResource)requested).getSpec().getCores()
                        ).memory(
                        ((SimpleComputeResource)requested).getSpec().getMemory()
                        )
                    );
                offer.getResources().addComputeItem(
                    offered
                    );
                }

            for (AbstractStorageResource requested : request.getResources().getStorage())
                {
                SimpleStorageResource offered = new SimpleStorageResource(
                    requested.getName()
                    );
                offered.setSpec(
                    new SimpleStorageSpecific().size(
                        ((SimpleStorageResource)requested).getSpec().getSize()
                        )
                    );
                offer.getResources().addStorageItem(
                    offered
                    );
                }

            response.addOffersItem(
                offer
                );
            }
        return new ResponseEntity<ParcolarResponse>(response, HttpStatus.OK);
	    }
    }


