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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.co.metagrid.ambleck.datamodel.executable.AbstractExecutable;
import uk.co.metagrid.ambleck.datamodel.executable.PingExecutable;
import uk.co.metagrid.ambleck.datamodel.executable.DelayExecutable;

import uk.co.metagrid.ambleck.datamodel.resource.AbstractResource;
import uk.co.metagrid.ambleck.datamodel.resource.compute.ComputeResource;

@RestController
public class AmbleckController {

    public static final String APPLICATION_YAML_VALUE = "application/yaml" ;
    public static final String[] MEDIA_TYPES_ARRAY = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        APPLICATION_YAML_VALUE
        };

    public AbstractExecutable process(AbstractExecutable requested)
        {
        if (requested instanceof PingExecutable)
            {
            PingExecutable instance = (PingExecutable) requested;
            return new PingExecutable(
                instance.getSpec().getTarget(),
                instance.getName()
                );
            }
        else if (requested instanceof DelayExecutable)
            {
            DelayExecutable instance = (DelayExecutable) requested;
            return new DelayExecutable(
                instance.getSpec().getDuration(),
                instance.getName()
                );
            }
        else {
            return null ;
            }
        }

    @JsonRootName("request")
    public static class RequestObject {

        public RequestObject()
            {
            }

        public RequestObject(final AbstractExecutable executable)
            {
            this.executable = executable;
            }

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            defaultImpl = AbstractExecutable.class
            )
        private AbstractExecutable executable;
        public AbstractExecutable getExecutable()
            {
            return this.executable;
            }
        public void setExecutable(final AbstractExecutable executable)
            {
            this.executable = executable;
            }

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            defaultImpl = AbstractResource.class
            )
        private List<AbstractResource> resources = new ArrayList<AbstractResource>();
        public List<AbstractResource> getResources()
            {
            return this.resources;
            }
        public void setResources(final List<AbstractResource> resources)
            {
            this.resources = resources;
            }
        public void addResource(final AbstractResource resource)
            {
            this.resources.add(
                resource
                );
            }
        }


    @JsonRootName("offer")
    public static class OfferObject {

        public OfferObject()
            {
            }

        public OfferObject(final AbstractExecutable executable)
            {
            this.executable = executable;
            }

        private AbstractExecutable executable;
        public AbstractExecutable getExecutable()
            {
            return this.executable;
            }
        public void setExecutable(final AbstractExecutable executable)
            {
            this.executable = executable;
            }

        private List<AbstractResource> resources = new ArrayList<AbstractResource>();
        public List<AbstractResource> getResources()
            {
            return this.resources;
            }
        public void setResources(final List<AbstractResource> resources)
            {
            this.resources = resources;
            }
        public void addResource(final AbstractResource resource)
            {
            this.resources.add(
                resource
                );
            }
        }

    public static enum ResultCode {
        YES,
        NO
        }

    @JsonRootName("response")
    public static class ResponseObject {

        public ResponseObject()
            {
            this(null, null);
            }

        public ResponseObject(final ResultCode result)
            {
            this(result, null);
            }

        public ResponseObject(final ResultCode result, final List<OfferObject> offers)
            {
            this.result = result;
            if (offers == null)
                {
                this.offers = new ArrayList<OfferObject>();
                }
            else {
                this.offers = offers;
                }
            }

        private ResultCode result ;
        public ResultCode getResult()
            {
            return this.result ;
            }
        public void setResult(final ResultCode result)
            {
            this.result = result;
            }

        private List<OfferObject> offers;
        public List<OfferObject> getOffers()
            {
            return this.offers;
            }
        public void setOffers(final List<OfferObject> offers)
            {
            this.offers = offers;
            }
        public void addOffer(final OfferObject offer)
            {
            this.offers.add(
                offer
                );
            }
        }

    @PostMapping(
        value = "/ambleck-post",
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            APPLICATION_YAML_VALUE
            },
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            APPLICATION_YAML_VALUE
            }
        )
    @ResponseBody
	public ResponseObject ambleckPost(
	    @RequestBody RequestObject request
	    ) {

	    ResponseObject response = new ResponseObject(
	        ResultCode.YES
	        );

        for (int i = 0 ; i < 2 ; i++)
            {
            AbstractExecutable executable = process(
                request.getExecutable()
                );

            OfferObject offer = new OfferObject(
                executable
                );

            for (AbstractResource requested : request.getResources())
                {
                AbstractResource offered = new ComputeResource(
                    requested.getName()
                    );
                offer.addResource(
                    offered
                    );
                }

            response.addOffer(
                offer
                );
            }
        return response;
	    }

    }


