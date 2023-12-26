/*
 *
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2023 by Wizzard Solutions Ltd, wizzard@metagrid.co.uk
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
 */

package uk.co.metagrid.pandak;

//import javax.xml.bind.annotation.*;

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
public class PandakController {

    public static final String APPLICATION_YAML_VALUE = "application/yaml" ;
    public static final String[] MEDIA_TYPES_ARRAY = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        APPLICATION_YAML_VALUE
        };

    @JsonSubTypes(
            {
            @JsonSubTypes.Type(value = RedSpec.class,   name = RedSpec.TYPE_URI),
            @JsonSubTypes.Type(value = GreenSpec.class, name = GreenSpec.TYPE_URI)
            }
        )
    public static abstract class ExeSpec {

        public ExeSpec()
            {
            }
        }

    public static class RedSpec extends ExeSpec {

        public static final String TYPE_URI = "foo:red";

        public RedSpec()
            {
            }

        private Integer size;
        public  Integer getSize()
            {
            return this.size;
            }
        public  void setSize(final Integer size)
            {
            this.size = size;
            }
        }

    public static class GreenSpec extends ExeSpec {

        public static final String TYPE_URI = "foo:green";

        public GreenSpec()
            {
            }

        private String text;
        public  String getText()
            {
            return this.text;
            }
        public  void setText(final String text)
            {
            this.text = text;
            }

        }

    public static class Executable {

        public Executable()
            {
            this(null, null, null);
            }

        public Executable(final String name)
            {
            this(name, null, null);
            }

        public Executable(final String name, final String type)
            {
            this(name, type, null);
            }

        public Executable(final String name, final String type, final ExeSpec spec)
            {
            this.name = name;
            this.type = type;
            this.spec = spec;
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


        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
            )
        private ExeSpec spec;
        public  ExeSpec getSpec()
            {
            return this.spec;
            }
        public  void setSpec(final ExeSpec spec)
            {
            this.spec = spec;
            }
        }

    //@XmlRootElement(name = "my-request")
    @JsonRootName("my-request")
    public static class Request {

        public Request()
            {
            }

        public Request(final Executable executable)
            {
            this.executable = executable;
            }

        private Executable executable;
        public Executable getExecutable()
            {
            return this.executable;
            }
        public void setExecutable(final Executable executable)
            {
            this.executable = executable;
            }

        }

    @GetMapping(
        value = "/pandak-get",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            APPLICATION_YAML_VALUE
            }
        )
    @ResponseBody
	public Request pandakGet() {
		return new Request(
		    new Executable(
		        "test-name",
		        "test-type"
		        )
		    );
	    }


    @PostMapping(
        value = "/pandak-post",
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
	public Request pandakPost(
	    @RequestBody Request request
	    ) {
	    return new Request(
	        new Executable(
	            request.getExecutable().getName(),
	            request.getExecutable().getType(),
	            request.getExecutable().getSpec()
	            )
	        );
	    }


    public static class JacksonErrorResponse {

        public JacksonErrorResponse(final String title)
            {
            this.title = title;
            }

        private String title;
        public String getTitle()
            {
            return this.title;
            }
        private List<String> messages = new ArrayList<String>();
        public List<String> getMessages()
            {
            return this.messages;
            }
        public void addMessage(final String message)
            {
            this.messages.add(
                message
                );
            }
        }

    @ExceptionHandler(
        JsonMappingException.class
        )
    @ResponseBody
    public JacksonErrorResponse jacksonError(JsonMappingException ouch)
        {
        JacksonErrorResponse response = new JacksonErrorResponse(
            "jackson error response"
            );
        response.addMessage(
            ouch.getMessage()
            );
        response.addMessage(
            ouch.getClass().getName()
            );
        return response;
        }

    @ExceptionHandler(
        UnrecognizedPropertyException.class
        )
    @ResponseBody
    public JacksonErrorResponse jacksonError(UnrecognizedPropertyException ouch)
        {
        JacksonErrorResponse response = new JacksonErrorResponse(
            "Unrecognized property"
            );
        response.addMessage(
            "Class [%s]".formatted(
                ouch.getReferringClass().getName()
                )
            );
        response.addMessage(
            "Property [%s]".formatted(
                ouch.getPropertyName()
                )
            );

        StringBuilder builder = new StringBuilder("Path : ");
        ouch.getPath().forEach(
            pathref -> {
                builder.append(
                    "[%s]".formatted(
                        pathref.getFieldName()
                        )
                    );
                }
            );
        response.addMessage(
            builder.toString()
            );

        return response;
        }
    }

