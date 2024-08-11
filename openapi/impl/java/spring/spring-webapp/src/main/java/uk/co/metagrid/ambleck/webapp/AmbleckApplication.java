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
 * Using the @SpringBootApplication Annotation
 * https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/using-boot-using-springbootapplication-annotation.html
 *
 * What a mess.
 * https://stackoverflow.com/questions/7854030/configuring-objectmapper-in-spring
 *
 * Add JSR310.JavaTimeModule
 * https://mkyong.com/java/jackson-java-8-date-time-type-java-time-localdate-not-supported-by-default/
 *
 */

package uk.co.metagrid.ambleck.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;

import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter ;

import com.fasterxml.jackson.databind.SerializationFeature ;
import com.fasterxml.jackson.databind.DeserializationFeature ;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.co.metagrid.ambleck.util.YamlConverter;

@SpringBootApplication
@Import(
    { YamlConverter.class }
    )
public class AmbleckApplication {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    /**
     * This manages to catch the built-in converters,
     * but it misses the YAML converter.
     *
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event)
        {
        handlerAdapter
            .getMessageConverters()
            .stream()
            .forEach(
                c -> {
                    if (c instanceof MappingJackson2HttpMessageConverter)
                        {
                        System.out.print("Found MappingJackson2HttpMessageConverter [" + c.getClass().getName() + "]");
                        MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) c;
                        ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                        objectMapper.disable(
                            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                            );
                        objectMapper.registerModule(
                            new JavaTimeModule()
                            );
                        }
                    }
                );
        }

	public static void main(String[] args) {
		SpringApplication.run(
		    AmbleckApplication.class,
		    args
		    );
	    }
    }

