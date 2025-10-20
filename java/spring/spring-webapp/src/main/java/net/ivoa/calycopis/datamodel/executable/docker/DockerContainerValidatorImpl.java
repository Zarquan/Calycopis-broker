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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.podman.PodmanPlatform;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;
import net.ivoa.calycopis.openapi.model.IvoaDockerExternalPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.openapi.model.IvoaDockerInternalPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerNetworkPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerNetworkSpec;
import net.ivoa.calycopis.openapi.model.IvoaDockerPlatformSpec;
import net.ivoa.calycopis.openapi.model.IvoaExecutableAccessMethod;

/**
 * A validator implementation to handle DockerContainers.
 *
 */
@Slf4j
public abstract class DockerContainerValidatorImpl
extends AbstractExecutableValidatorImpl
implements DockerContainerValidator
    {

    private final PodmanPlatform platform;

    @Autowired
    public DockerContainerValidatorImpl(final PodmanPlatform platform)
        {
        this.platform = platform;
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
        IvoaDockerContainer validated = new IvoaDockerContainer(
            DockerContainer.TYPE_DISCRIMINATOR
            );

        //
        // Validate the executable name.
        // TODO Move this to a base class
        success &= validateName(
            requested.getName(),
            validated,
            context
            );

        // Created
        // Messages
        
        //
        // Validate the executable access methods.
        // TODO Move this to a base class
        success &= validateAccess(
            requested.getAccess(),
            validated,
            context
            );
        
        //
        // Validate the image locations.
        success &= validateImage(
            requested.getImage(),
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
        // Validate the requested entrypoint.
        success &= validateEntrypoint(
            requested.getEntrypoint(),
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
        // Validate the container network.
        success &= validateNetwork(
            requested.getNetwork(),
            validated,
            context
            );

        //
        // Calculate the preparation time.
        validated.setSchedule(
            new IvoaComponentSchedule()
            );
        success &= setPrepareDuration(
            context,
            validated.getSchedule(),
            this.predictPrepareTime(
                validated
                )
            );
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new EntityBuilder.
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public AbstractExecutableEntity build(final ExecutionSessionEntity session)
                    {
                    return platform.getDockerContainerEntityFactory().create(
                        session,
                        validated
                        );
                    }
                };
            //
            // Create a new validator Result.
            AbstractExecutableValidator.Result result = new AbstractExecutableValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                ) {
                @Override
                public Long getPreparationTime()
                    {
                    return predictPrepareTime(
                        validated
                        );
                    }
                };
            //
            // Add our Result to our context
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
            // TODO Better checks
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
     * Validate the executable access methods.
     *
     */
    public boolean validateAccess(
        final List<IvoaExecutableAccessMethod> requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateAccess(....)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if ((requested != null) && (requested.isEmpty() == false))
            {
            context.getOfferSetEntity().addWarning(
                "urn:server-assigned-value",
                "Access methods should not be set"
                );
            success = false ;
            }
        return success;
        }

    /**
     * Validate the container image location.
     *
     */
    public boolean validateImage(
        final IvoaDockerImageSpec requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateImage(....)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if (requested != null)
            {
            IvoaDockerImageSpec result = new IvoaDockerImageSpec();
            if ((requested.getLocations() != null) && (requested.getLocations().isEmpty() == false))
                {
                for (String location : requested.getLocations())
                    {
                    // TODO Better checks
                    success &= badValueCheck(
                        location,
                        context
                        );
                    result.addLocationsItem(
                        location
                        );
                    }
                }
            else {
                context.getOfferSetEntity().addWarning(
                    "urn:missig-required-value",
                    "At least one location required"
                    );
                success = false ;
                }

            String digest = requested.getDigest();
            success &= badValueCheck(
                digest,
                context
                );
            if (digest != null)
                {
                result.setDigest(
                    digest
                    );
                }
            else {
                // TODO Make this configurable
                context.getOfferSetEntity().addWarning(
                    "urn:missing-value",
                    "Image digest is required"
                    );
                success = false ;
                }

            success &= validatePlatform(
                requested.getPlatform(),
                result,
                context
                );
            
            validated.setImage(
                result
                );
            }
        else {
            context.getOfferSetEntity().addWarning(
                "urn:missing-value",
                "Container image is required"
                );
            success = false ;
            }
        return success;
        }

    public static final String DEFAULT_PLATFORM_ARCH = "amd64"; 
    public static final String DEFAULT_PLATFORM_OS   = "linux"; 

    public boolean validatePlatform(
        final IvoaDockerPlatformSpec requested,
        final IvoaDockerImageSpec validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePlatform(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
        IvoaDockerPlatformSpec result = new IvoaDockerPlatformSpec(); 

        result.setArchitecture(
            DEFAULT_PLATFORM_ARCH
            );
        result.setOs(
            DEFAULT_PLATFORM_OS
            );

        if (requested != null)
            {
            String platformArch = requested.getArchitecture();  
            result.setArchitecture(platformArch);
            if (platformArch != null)
                {
                // TODO make this configurable
                switch(platformArch)
                    {
                    case DEFAULT_PLATFORM_ARCH:
                        break ;

                    default:
                        context.getOfferSetEntity().addWarning(
                            "urn:invalied-value",
                            "Unsupported platform architecture"
                            );
                        success = false ;
                        break ;
                    }
                }

            String platformOs = requested.getOs();  
            result.setOs(platformOs);
            if (platformOs != null)
                {
                // TODO make this configurable
                switch(platformOs)
                    {
                    case DEFAULT_PLATFORM_OS:
                        break ;

                    default:
                        context.getOfferSetEntity().addWarning(
                            "urn:invalied-value",
                            "Unsupported platform operating system"
                            );
                        success = false;
                        break ;
                    }
                }
            }
        validated.setPlatform(
            result
            );
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
        log.debug("validateNetwork(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
        
        if (requested != null)
            {
            IvoaDockerNetworkSpec result = new IvoaDockerNetworkSpec();
            for (IvoaDockerNetworkPort port : requested.getPorts())
                {
                success &= validateNetworkPort(
                    port,
                    result,
                    context
                    );
                }
            if (success)
                {
                validated.setNetwork(
                    result
                    );
                }
            }
        
        return success;
        }


    /**
     * Validate a container network port.
     *
     */
    public boolean validateNetworkPort(
        final IvoaDockerNetworkPort requested,
        final IvoaDockerNetworkSpec validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePort(...)");
        log.debug("Requested [{}]", requested);
    
        boolean success = true ;
        IvoaDockerNetworkPort  result = new IvoaDockerNetworkPort();

        boolean access = requested.getAccess();
        result.setAccess(access);

        String protocol = requested.getProtocol();
        result.setProtocol(protocol);
        switch(protocol)
            {
            case "UDP":
            case "TCP":
            case "HTTP":
            case "HTTPS":
                break ;

            default:
                context.getOfferSetEntity().addWarning(
                    "urn:invalid-value",
                    "Unrecognised network port protocol[{}]",
                    Map.of(
                        "value",
                        protocol
                        )
                    );
                success = false ;
                break ;
            }

        String path = requested.getPath();
        result.setPath(
            path
            );
        success &= badValueCheck(
            path,
            context
            );

        IvoaDockerInternalPort internal = requested.getInternal();
        Integer portnum = internal.getPort(); 
        result.setInternal(
            new IvoaDockerInternalPort().port(
                portnum 
                )
            );
        if (portnum <= 0)
            {
            context.getOfferSetEntity().addWarning(
                "urn:invalid-value",
                "Negative network port number not supported [{}]",
                Map.of(
                    "value",
                    portnum
                    )
                );
            success = false ;
            }

        IvoaDockerExternalPort external = requested.getExternal();
        if (external != null)
            {
            context.getOfferSetEntity().addWarning(
                "urn:invalid-value",
                "External port details should not be set by client"
                );
            success = false ;
            }
            
        validated.addPortsItem(
            result
            );
        
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
        log.debug("validateEntrypoint(...)");
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
            if (hashmap.isEmpty() == false)
                {
                validated.setEnvironment(
                    hashmap
                    );
                }
            }
        return success;
        }

    /*
     * Platform dependent prepare time.
     * 
     */
    @Deprecated
    protected abstract Long predictPrepareTime(final IvoaDockerContainer validated);
    
    }
