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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaDockerContainer;
import net.ivoa.calycopis.spring.model.IvoaDockerExternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerInternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerPlatformSpec;

/**
 * A validator implementation to handle DockerContainers.
 *
 */
@Slf4j
public class DockerContainerValidatorImpl
extends AbstractExecutableValidatorImpl
implements DockerContainerValidator
    {

    private final Platform platform;

    @Autowired
    public DockerContainerValidatorImpl(final Platform platform)
        {
        this.platform = platform;
        }

    @Override
    public AbstractExecutableValidator.Result validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());
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
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaDockerContainer validated = new IvoaDockerContainer()
            .kind(DockerContainer.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        // Created
        // Messages
        
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
        /*
         * 
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
         * 
         */
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            log.debug("PASS DockerContainer validated [{}]", validated);
            //
            // Create a new validator Result.
            AbstractExecutableValidator.Result result = new AbstractExecutableValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ) {
                @Override
                public AbstractExecutableEntity build(final SimpleExecutionSessionEntity session)
                    {
                    return platform.getDockerContainerEntityFactory().create(
                        session,
                        this
                        );
                    }

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
            log.debug("FAIL DockerContainer NOT validated [{}]", validated);
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }
    

    /**
     * Validate the executable access methods.
     *
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
     */

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
                    success &= notBadValueCheck(
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
                    "DockerContainer - image location required"
                    );
                success = false ;
                }

            String digest = requested.getDigest();
            if (digest != null)
                {
                result.setDigest(
                    digest
                    );
                if (isBadValueCheck(digest,context)) 
                    {
                    context.getOfferSetEntity().addWarning(
                        "urn:bad-value",
                        "DockerContainer - image digest matches badvalue blackist [{}]",
                        Map.of(
                            "value",
                            digest
                            )
                        );
                    success = false ;
                    }
                }
            else {
                // TODO Make this configurable
                context.getOfferSetEntity().addWarning(
                    "urn:missing-value",
                    "DockerContainer - image digest is required"
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
                "DockerContainer - image is required"
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
                            "DockerContainer - platform architecture not supported [{}]",
                            Map.of(
                                "value",
                                platformArch
                                )
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
                            "DockerContainer - platform operating system not supported [{}]",
                            Map.of(
                                "value",
                                platformOs
                                )
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
                    "DockerContainer - unrecognised network port protocol [{}]",
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
        success &= notBadValueCheck(
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
                "DockerContainer - negative network port number not supported [{}]",
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
                "urn:not-supported",
                "DockerContainer - setting external port details not supported"
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
            success &= notBadValueCheck(
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
                "urn:not-supported",
                "DockerContainer - Privileged execution not supported"
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
                if (isBadValueCheck(entry.getKey(),context))
                    {
                    context.getOfferSetEntity().addWarning(
                        "urn:bad-value",
                        "DockerContainer - environment variable name matches badvalue blacklist [{}]",
                        Map.of(
                            "value",
                            entry.getKey()
                            )
                        );
                    success = false ;
                    }
                else if (isBadValueCheck(entry.getValue(),context))
                    {
                    context.getOfferSetEntity().addWarning(
                        "urn:bad-value",
                        "DockerContainer - environment variable value matches badvalue blacklist [{}]",
                        Map.of(
                            "value",
                            entry.getValue()
                            )
                        );
                    success = false ;
                    }
                else {
                    hashmap.put(
                        entry.getKey(),
                        entry.getValue()
                        ); 
                    }
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
     * TODO This will be platform dependent.
     * 
     */
    public static final Long DEFAULT_PREPARE_TIME = 45L;
    @Deprecated
    protected Long predictPrepareTime(final IvoaDockerContainer validated)
        {
        return DEFAULT_PREPARE_TIME;
        }
    
    }
