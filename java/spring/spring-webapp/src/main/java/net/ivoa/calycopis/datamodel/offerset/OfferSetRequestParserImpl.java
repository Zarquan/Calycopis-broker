/**
 * 
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.resource.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.resource.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.datamodel.resource.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntityFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.validator.Validator.Result;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaComponentSchedule;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaOfferedScheduleInstant;
import net.ivoa.calycopis.openapi.model.IvoaRequestedScheduleBlock;
import net.ivoa.calycopis.openapi.model.IvoaRequestedScheduleItem;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {
    /**
     * Our schedule block factory.
     * 
     */
    private final ComputeResourceOfferFactory computeOfferFactory;

    // TODO Replace this with a builder ..
    private final ExecutionSessionEntityFactory executionSessionFactory;

    /**
     * Executable Validators.
     * 
     */
    private final AbstractExecutableValidatorFactory executableValidators;

    /**
     * Storage resource Validators.
     * 
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;

    /**
     * Data resource Validators.
     * 
     */
    private final AbstractDataResourceValidatorFactory dataValidators;

    /**
     * Volume mount Validators.
     * 
     */
    private final AbstractVolumeMountValidatorFactory volumeValidators;

    /**
     * Compute resource Validators.
     * 
     */
    private final AbstractComputeResourceValidatorFactory computeValidators;

    @Autowired
    public OfferSetRequestParserImpl(
        final ComputeResourceOfferFactory offerBlockFactory, 
        final AbstractExecutableValidatorFactory executableValidators,
        final AbstractStorageResourceValidatorFactory storageValidators, 
        final AbstractDataResourceValidatorFactory dataValidators, 
        final AbstractVolumeMountValidatorFactory volumeValidators, 
        final AbstractComputeResourceValidatorFactory computeValidators,
        final ExecutionSessionEntityFactory executionSessionFactory
        ){
        super();
        this.computeOfferFactory  = offerBlockFactory ;
        this.executableValidators = executableValidators ;
        this.storageValidators    = storageValidators ;
        this.dataValidators       = dataValidators ;
        this.volumeValidators     = volumeValidators ;
        this.computeValidators    = computeValidators ;
        this.executionSessionFactory = executionSessionFactory;
        }
    
    @Override
    public void process(final IvoaOfferSetRequest offersetRequest, final OfferSetEntity offersetEntity)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        OfferSetRequestParserContext context = new OfferSetRequestParserContextImpl(
            this,
            offersetRequest,
            offersetEntity
            );
        //
        // Validate the request.
        validate(
            context
            );
        }
    
    /**
     * Validate the request components.
     * TODO Move this into an OfferSetValidator.
     * 
     */
    public void validate(final OfferSetRequestParserContext context)
        {
        log.debug("validate(OfferSetRequestParserState)");
        final IvoaOfferSetRequest offersetRequest = context.getOriginalOfferSetRequest();
        final IvoaOfferSetRequest offersetResult  = context.getValidatedOfferSetRequest();
        //
        // Initialise our result. 
        if (offersetResult.getResources() == null)
            {
            offersetResult.setResources(
                new IvoaExecutionResourceList()
                );
            }
        //
        // Validate the requested resources.
        log.debug("Validating the requested resources");
        if (offersetRequest.getResources() != null)
            {
            //
            // Validate the requested storage resources.
            log.debug("Validating the requested storage resources");
            if (offersetRequest.getResources().getStorage() != null)
                {
                for (IvoaAbstractStorageResource resource : offersetRequest.getResources().getStorage())
                    {
                    storageValidators.validate(
                        resource,
                        context
                        );
                    // TODO Check the result ?
                    }
                }
            //
            // Validate the requested data resources.
            log.debug("Validating the requested data resources");
            if (offersetRequest.getResources().getData() != null)
                {
                for (IvoaAbstractDataResource resource : offersetRequest.getResources().getData())
                    {
                    dataValidators.validate(
                        resource,
                        context
                        );
                    // TODO Check the result ?
                    }
                }
            //
            // Validate the requested volume mounts.
            log.debug("Validating the requested volume mounts");
            if (offersetRequest.getResources().getVolumes() != null)
                {
                for (IvoaAbstractVolumeMount resource : offersetRequest.getResources().getVolumes())
                    {
                    volumeValidators.validate(
                        resource,
                        context
                        );
                    // TODO Check the result ?
                    }
                }

            //
            // Validate the requested compute resources.
            log.debug("Validating the requested compute resources");
            if (offersetRequest.getResources().getCompute() != null)
                {
                for (IvoaAbstractComputeResource resource : offersetRequest.getResources().getCompute())
                    {
                    computeValidators.validate(
                        resource,
                        context
                        );            
                    // TODO Check the result ?
                    }
                }
            }
        log.debug("Finished validating the resources");
        
        // Exit if errors ..
        
        //
        // If we haven't found a compute resource, add a default.
        log.debug("Checking for empty compute resource list");
        if (context.getComputeValidatorResults().isEmpty())
            {
            log.debug("Adding a default compute resource");
            IvoaSimpleComputeResource compute = new IvoaSimpleComputeResource(
                SimpleComputeResource.TYPE_DISCRIMINATOR 
                );
            computeValidators.validate(
                compute,
                context
                );
            // TODO Check the result ?
            }

        // Exit if errors ..

        //
        // Validate the requested executable.
        log.debug("Validating the requested executable");
        if (offersetRequest.getExecutable() != null)
            {
            executableValidators.validate(
                offersetRequest.getExecutable(),
                context
                );
            // TODO Check the result ?
            }
        else {
            log.error("Offerset request has no executable");
            context.getOfferSetEntity().addWarning(
                "urn:executable-required",
                "Description of the executable is required"
                );
            context.valid(false);
            }
        
        //
        // Validate the schedule.
        validate(
            offersetRequest.getSchedule(),
            context
            );
        
        //
        // This is specific to the CANMFAR platforms.
        // Fail if we have found too many compute resources,
        // Other platforms may be able to support more than one compute resources.
        if (context.getComputeValidatorResults().size() > 1)
            {
            log.warn("Found more than one compute resources");
            context.getOfferSetEntity().addWarning(
                "urn:not-supported-message",
                "Multiple compute resources not supported"
                );
            context.valid(false);
            }
        
        // Exit if errors ..
        
        build(context);
        }

    
//
// TODO Move this part to a separate schedule validator.
//
    
    /**
     * Default session duration, 2 hours.
     *
     */
    public static final Duration DEFAULT_SESSION_DURATION = Duration.ofHours(2);

    /**
     * Default duration for the default start interval.
     * Sometime in the next 2 hours.
     *
     */
    public static final Duration DEFAULT_START_DURATION = Duration.ofHours(2);

    /**
     * Validate the requested Schedule.
     *
     */
    public boolean validate(final IvoaRequestedScheduleBlock schedule, final OfferSetRequestParserContext context)
        {
        boolean success = true ;

        log.debug("validate(IvoaRequestedScheduleBlock)");

        Duration prepareDuration = context.getMaxPreparationDuration();
        Instant prepareDoneInstant = Instant.now().plus(
            prepareDuration
            );
        
        log.debug("Prepare duration [{}]", prepareDuration);
        log.debug("Prepare done [{}]", prepareDoneInstant);

        if (schedule != null)
            {
            IvoaRequestedScheduleItem requested = schedule.getRequested();
            if (requested != null)
                {
                String durationString = requested.getDuration();
                if (durationString != null)
                    {
                    try {
                        log.debug("Duration string [{}]", durationString);
                        Duration durationValue = Duration.parse(
                            durationString
                            );
                        log.debug("Duration value [{}]", durationValue);
                        context.setExecutionDuration(
                            durationValue
                            );
                        }
                    catch (Exception ouch)
                        {
                        context.getOfferSetEntity().addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse duration [${string}][${message}]",
                            Map.of(
                                "value",
                                durationString,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        success = false ;
                        context.valid(false);
                        }
                    }
                
                List<String> startStringList = requested.getStart();
                if (startStringList != null)
                    {
                    for (String startString : startStringList)
                        {
                        try {
                            log.debug("Interval String [{}]", startString);
                            Interval startinterval = Interval.parse(
                                startString
                                );
                            log.debug("Interval value [{}]", startinterval);
                            if (startinterval.startsBefore(prepareDoneInstant))
                                {
                                log.warn("Start interval starts before preparation time [{}][{}]", startinterval.getStart(), prepareDoneInstant);
                                // TODO Add a message ..
                                }
                            else {
                                context.addStartInterval(
                                    startinterval
                                    );
                                }
                            }
                        catch (Exception ouch)
                            {
                            log.debug("Exception [{}][{}]", ouch.getMessage(), ouch.getClass());
                            context.getOfferSetEntity().addWarning(
                                "urn:input-syntax-fail",
                                "Unable to parse interval [${string}][${message}]",
                                Map.of(
                                    "string",
                                    startString,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            success = false ;
                            context.valid(false);
                            }
                        }
                    }
                }
            }

        if (context.getStartIntervals().isEmpty())
            {
            // Default start is after the prepare time.
            Interval defaultint = Interval.of(
                prepareDoneInstant,
                DEFAULT_START_DURATION
                );
            log.debug("Interval list is empty, adding default [{}]", defaultint);
            context.addStartInterval(
                defaultint
                );
            }

        if (context.getExecutionDuration() == null)
            {
            log.debug("Duration is empty, using default [{}]", DEFAULT_SESSION_DURATION);
            context.setExecutionDuration(
                DEFAULT_SESSION_DURATION
                );
            }
        return success ;
        }
    
    /**
     * Build the entities from the validated input.
     *  
     */
    public void build(final OfferSetRequestParserContext context)
        {
        log.debug("build(OfferSetRequestParserState)");

        //
        // Start with NO, and set to YES when we have at least one offer.
        IvoaOfferSetResponse.ResultEnum resultEnum = IvoaOfferSetResponse.ResultEnum.NO;

        //
        // If everything is OK.
        if (context.valid())
            {
            //
            // Generate some offers ..
            log.debug("---- ---- ---- ----");
            log.debug("Generating offers ....");
            log.debug("Start intervals [{}]", context.getStartIntervals());
            log.debug("Execution duration [{}]", context.getExecutionDuration());
            
            log.debug("Min cores [{}]",  context.getTotalMinCores());
            log.debug("Max cores [{}]",  context.getTotalMaxCores());
            log.debug("Min memory [{}]", context.getTotalMinMemory());
            log.debug("Max memory [{}]", context.getTotalMaxMemory());
            log.debug("---- ---- ---- ----");

            //
            // Populate our OfferSet ..
            for (Interval startInterval : context.getStartIntervals())
                {
                //
                // Check if the interval is possible.
                // Is the interval start greater than now + context.totalPreparationTime
                //
                
                
                //
                // Generate a list of available resources.
                List<ComputeResourceOffer> computeOffers = computeOfferFactory.generate(
                    startInterval,
                    context.getExecutionDuration(),
                    context.getTotalMinCores(),
                    context.getTotalMinMemory()
                    );
                //
                // Create an ExecutionSession for each offer. 
                for (ComputeResourceOffer computeOffer : computeOffers)
                    {
                    log.debug("OfferBlock [{}]", computeOffer.getStartTime());
                    ExecutionSessionEntity executionSessionEntity = executionSessionFactory.create(
                        context.getOfferSetEntity(),
                        context,
                        computeOffer
                        );
                    log.debug("ExecutionEntity [{}]", executionSessionEntity);

                    
                    
                    //
                    // Build a new ExecutableEntity and add it to our ExecutionSessionEntity.
                    executionSessionEntity.setExecutable(
                        context.getExecutableResult().getBuilder().build(
                            executionSessionEntity
                            )
                        );
                    
                    //
                    // Add our compute resources
                    for (AbstractComputeResourceValidator.Result result : context.getComputeValidatorResults())
                        {
                        result.getBuilder().build(
                            executionSessionEntity,
                            computeOffer
                            );
                        }
                    //
                    // Add our storage resources.
                    for (AbstractStorageResourceValidator.Result result : context.getStorageValidatorResults())
                        {
                        result.build(
                            executionSessionEntity
                            );
                        }
                    //
                    // Add our data resources.
                    for (AbstractDataResourceValidator.Result result : context.getDataResourceValidatorResults())
                        {
                        result.getBuilder().build(
                            executionSessionEntity
                            );
                        }
                    //
                    // Add our volume mounts.
                    for (AbstractVolumeMountValidator.Result result : context.getVolumeValidatorResults())
                        {
                        result.getBuilder().build(
                            executionSessionEntity
                            );
                        }
                    
                    //
                    // Confirm we have at least one result.
                    resultEnum = IvoaOfferSetResponse.ResultEnum.YES;
                    }
                }
            }
        //
        // Set the OfferSet result.
        context.getOfferSetEntity().setResult(
            resultEnum
            );
        }
    }
