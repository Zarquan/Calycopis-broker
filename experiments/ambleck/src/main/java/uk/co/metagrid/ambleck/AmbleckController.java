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

package uk.co.metagrid.ambleck;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
public class AmbleckController {

    public static final String APPLICATION_YAML_VALUE = "application/yaml" ;
    public static final String[] MEDIA_TYPES_ARRAY = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        APPLICATION_YAML_VALUE
        };

    @JsonSubTypes(
            {
            @JsonSubTypes.Type(
                value = PingExecutable.class,
                name = PingExecutable.TYPE_URL
                ),
            @JsonSubTypes.Type(
                value = DelayExecutable.class,
                name = DelayExecutable.TYPE_URL
                )
            }
        )
    public static abstract class BaseExecutable {

        public static final String TYPE_URL = "urn:base-executable";

        public BaseExecutable()
            {
            this(TYPE_URL, null);
            }

        public BaseExecutable(final String type)
            {
            this(type, null);
            }

        public BaseExecutable(final String type, final String name)
            {
            this.type = type;
            this.name = name;
            }

        private String type;
        public  String getType()
            {
            return this.type;
            }
        public  void setType(final String type)
            {
            this.type = type;
            }

        private String name;
        public  String getName()
            {
            return this.name;
            }
        public  void setName(final String name)
            {
            this.name = name;
            }

        public static class BaseSpecific {
            }

        public abstract BaseSpecific getSpec();

        }

    public static class PingExecutable extends BaseExecutable {

        public static final String TYPE_URL = "urn:ping-executable";

        public static final String DEFAULT_NAME = "Ping test";
        public static final String DEFAULT_TARGET = "localhost";

        public PingExecutable()
            {
            super(TYPE_URL, DEFAULT_NAME);
            }

        public PingExecutable(final String target)
            {
            this(target, DEFAULT_NAME);
            }

        public PingExecutable(final String target, final String name)
            {
            super(TYPE_URL, name);
            this.spec = new PingSpecific(
                target
                );
            }

        public static class PingSpecific extends BaseSpecific {

            public PingSpecific()
                {
                super();
                }

            public PingSpecific(final String target)
                {
                super();
                this.target = target ;
                }

            private String target ;
            public String getTarget()
                {
                return this.target;
                }
            public void setTarget(final String target)
                {
                this.target = target;
                }
            }

        private PingSpecific spec ;
        public PingSpecific getSpec()
            {
            return this.spec;
            }
        public void setSpec(final PingSpecific spec)
            {
            this.spec = spec;
            }
        }


    public static class DelayExecutable extends BaseExecutable {

        public static final String TYPE_URL = "urn:delay-executable";

        public static final String  DEFAULT_NAME = "Delay test";
        public static final Integer DEFAULT_DURATION = new Integer(20);

        public DelayExecutable()
            {
            super(TYPE_URL, DEFAULT_NAME);
            }

        public DelayExecutable(final Integer duration)
            {
            this(duration, DEFAULT_NAME);
            }

        public DelayExecutable(final Integer duration, final String name)
            {
            super(TYPE_URL, name);
            this.spec = new DelaySpecific(
                duration
                ) ;
            }

        public static class DelaySpecific extends BaseSpecific {

            public DelaySpecific()
                {
                super();
                }

            public DelaySpecific(final Integer duration)
                {
                super();
                this.duration = duration ;
                }

            private Integer duration ;
            public Integer getDuration()
                {
                return this.duration ;
                }
            public void setDuration(final Integer duration)
                {
                this.duration = duration ;
                }
            }

        private DelaySpecific spec ;
        public DelaySpecific getSpec()
            {
            return this.spec;
            }
        public void setSpec(final DelaySpecific spec)
            {
            this.spec = spec;
            }
        }

    public BaseExecutable process(BaseExecutable requested)
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

    public static class BaseResource {

        public BaseResource()
            {
            this(null, null);
            }

        public BaseResource(final String name)
            {
            this(name, null);
            }

        public BaseResource(final String name, final String type)
            {
            this.name = name;
            this.type = type;
            }

        private String name;
        public  String getName()
            {
            return this.name;
            }
        public  void setName(final String name)
            {
            this.name = name;
            }

        private String type;
        public  String getType()
            {
            return this.type;
            }
        public  void setType(final String type)
            {
            this.type = type;
            }
        }

    public static class ComputeResource extends BaseResource {

        }

    @JsonRootName("request")
    public static class RequestObject {

        public RequestObject()
            {
            }

        public RequestObject(final BaseExecutable executable)
            {
            this.executable = executable;
            }

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            defaultImpl = BaseExecutable.class
            )
        private BaseExecutable executable;
        public BaseExecutable getExecutable()
            {
            return this.executable;
            }
        public void setExecutable(final BaseExecutable executable)
            {
            this.executable = executable;
            }

        private List<BaseResource> resources = new ArrayList<BaseResource>();
        public List<BaseResource> getResources()
            {
            return this.resources;
            }
        public void setResources(final List<BaseResource> resources)
            {
            this.resources = resources;
            }
        public void addResource(final BaseResource resource)
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

        public OfferObject(final BaseExecutable executable)
            {
            this.executable = executable;
            }

        private BaseExecutable executable;
        public BaseExecutable getExecutable()
            {
            return this.executable;
            }
        public void setExecutable(final BaseExecutable executable)
            {
            this.executable = executable;
            }

        private List<BaseResource> resources = new ArrayList<BaseResource>();
        public List<BaseResource> getResources()
            {
            return this.resources;
            }
        public void setResources(final List<BaseResource> resources)
            {
            this.resources = resources;
            }
        public void addResource(final BaseResource resource)
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
            BaseExecutable executable = process(
                request.getExecutable()
                );

            OfferObject offer = new OfferObject(
                executable
                );

            for (BaseResource requested : request.getResources())
                {
                BaseResource offered = new BaseResource(
                    requested.getName(),
                    requested.getType()
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


