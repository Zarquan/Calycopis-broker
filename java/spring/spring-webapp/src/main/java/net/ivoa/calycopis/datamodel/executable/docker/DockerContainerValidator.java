/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.builder.Builder;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.ValidatorTools;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;
import net.ivoa.calycopis.openapi.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.openapi.model.IvoaDockerNetworkSpec;

/**
 * A validator implementation to handle DockerContainers.
 *
 */
@Slf4j
public class DockerContainerValidator
extends ValidatorTools
implements AbstractExecutableValidator
    {

    private final DockerContainerEntityFactory factory;

    @Autowired
    public DockerContainerValidator(final DockerContainerEntityFactory factory)
        {
        this.factory = factory;
        }

    @Override
    public AbstractExecutableValidator.Result validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());
        if (requested instanceof IvoaDockerContainer)
            {
            return validate(
                (IvoaDockerContainer) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    /**
     * Validate an IvoaDockerContainer.
     *
     */
    public AbstractExecutableValidator.Result validate(
        final IvoaDockerContainer requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaDockerContainer)");
        log.debug("Executable [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaDockerContainer validated = new IvoaDockerContainer();

        //
        // Validate the executable name.
        success &= validateName(
            requested.getName(),
            validated,
            context
            );

        //
        // Validate the image locations.
        success &= validateImageSpec(
            requested.getImage(),
            validated,
            context
            );

        //
        // Validate the container network.
        success &= validateNetwork(
            requested.getNetwork(),
            validated,
            context
            );

        //
        // Validate the requested endpoint.
        success &= validateEntrypoint(
            requested.getEntrypoint(),
            validated,
            context
            );

        //
        // Validate the privileged flag.
        success &= validatePrivileged(
            requested.getPrivileged(),
            validated,
            context
            );

        //
        // Validate the environment variables.
        success &= validateEnvironment(
            requested.getEnvironment(),
            validated,
            context
            );
        
        //
        // Everything is good, add our result to the state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            log.debug("Success - creating Result.");

            Builder<AbstractExecutableEntity> builder = new Builder<AbstractExecutableEntity>()
                {
                @Override
                public AbstractExecutableEntity build(final ExecutionSessionEntity parent)
                    {
                    return factory.create(
                        parent,
                        validated
                        );
                    }
                };

            AbstractExecutableValidator.Result result = new AbstractExecutableValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );
            context.getValidatedOfferSetRequest().setExecutable(
                validated
                );
            context.setExecutableResult(
                result
                );
            return result;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }
    
    /**
     * Validate the executable name.
     * 
     */
    public boolean validateName(
        final String requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateName(String ...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        String name = notEmpty(
            requested
            );
        if (name != null)
            {
            // TODO Make this configurable.
            success &= badValueCheck(
                name,
                context
                );
            }
        if (success)
            {
            validated.setName(
                name
                );
            }
        else {
            validated.setName(
                null
                );
            }
        
        return success;
        }
    
    /**
     * Validate the container image location.
     *
     */
    public boolean validateImageSpec(
        final IvoaDockerImageSpec requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateImageSpec(....)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
        IvoaDockerImageSpec result = new IvoaDockerImageSpec();

        List<String> locations = new ArrayList<String>();

        if (requested != null)
            {
            for (String location : requested.getLocations())
                {
                // TODO Add better checks ..
                boolean notbad = badValueCheck(
                    location,
                    context
                    );
                if (notbad)
                    {
                    locations.add(
                        location
                        );
                    }
                success &= notbad;
                }
            }
        
        validated.setImage(result);
        
        return success;
        }

    /**
     * Validate the container network.
     *
     */
    public boolean validateNetwork(
        final IvoaDockerNetworkSpec requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateNetwork(IvoaDockerNetworkSpec...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        // TODO Do some checking here ...
        validated.setNetwork(requested);
        
        return success;
        }

    /**
     * Validate the container entrypoint.
     * 
     */
    public boolean validateEntrypoint(
        final String requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateEntrypoint(String ...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        String entrypoint = notEmpty(
            requested
            );
        if (entrypoint != null)
            {
            // TODO Make this configurable.
            success &= badValueCheck(
                entrypoint,
                context
                );
            }

        if (success)
            {
            validated.setEntrypoint(
                entrypoint
                );
            }
        else {
            validated.setEntrypoint(
                null
                );
            }
        
        return success;
        }
    
    /**
     * Validate the privileged flag.
     * 
     */
    public boolean validatePrivileged(
        final Boolean requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePrivileged(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        // This implementation doesn't support privileged execution, so fail the request.
        // TODO Make this configurable. 
        if ((requested != null) && (requested == true))
            {
            context.getOfferSetEntity().addWarning(
                "urn:functionality-not-supported",
                "Privileged execution not supported"
                );
            success = false ;
            }
        else {
            validated.setPrivileged(false);
            }

        return success;
        }

    /**
     * Validate the environment variables.
     * 
     */
    public boolean validateEnvironment(
        final Map<String, String> requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateEnvironment(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if (requested != null)
            {
            Map<String, String> hashmap = new HashMap<String, String>();
            for (Map.Entry<String,String> entry : requested.entrySet())
                {
                // TODO Add better checks ..
                boolean notbad = badValueCheck(
                    entry.getKey(),
                    context
                    );
                if (notbad)
                    {
                    hashmap.put(
                        entry.getKey(),
                        entry.getValue()
                        ); 
                    }
                success &= notbad;
                }
            //
            // Don't add an empty Map.
            if (hashmap.isEmpty())
                {
                validated.setEnvironment(null);
                }
            else {
                validated.setEnvironment(hashmap);
                }
            }
        else {
            validated.setEnvironment(null);
            }
        return success;
        }
    }

