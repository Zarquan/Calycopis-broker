/**
 * 
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.spring.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.spring.model.IvoaRequestedScheduleBlock;
import net.ivoa.calycopis.spring.model.IvoaRequestedScheduleItem;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

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
    private final SimpleExecutionSessionEntityFactory executionSessionFactory;

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
        final SimpleExecutionSessionEntityFactory executionSessionFactory
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
        // Register all the resources and assign UUIDs.
        context.registerResources();
        //
        // Validate the request.
        validate(
            context
            );
        //
        // Process the request.
        process(
            context
            );
        }
    
    /**
     * Validate the request components.
     * TODO Move this into an OfferSetValidator.
     * 
     */
    public OfferSetRequestParserContext validate(final OfferSetRequestParserContext context)
        {
        log.debug("validate(OfferSetRequestParserState)");
        final IvoaOfferSetRequest offersetRequest = context.getOriginalOfferSetRequest();
        //
        // Validate the requested resources.
        log.debug("Validating the requested resources");

        //
        // Validate the requested storage resources.
        log.debug("Validating the requested storage resources");
        if (offersetRequest.getStorage() != null)
            {
            for (IvoaAbstractStorageResource resource : offersetRequest.getStorage())
                {
                storageValidators.validate(
                    resource,
                    context
                    );
                }
            }
        //
        // Validate the requested data resources.
        log.debug("Validating the requested data resources");
        if (offersetRequest.getData() != null)
            {
            for (IvoaAbstractDataResource resource : offersetRequest.getData())
                {
                dataValidators.validate(
                    resource,
                    context
                    );
                }
            }
        //
        // Validate the requested volume mounts.
        log.debug("Validating the requested volume mounts");
        if (offersetRequest.getVolumes() != null)
            {
            for (IvoaAbstractVolumeMount resource : offersetRequest.getVolumes())
                {
                volumeValidators.validate(
                    resource,
                    context
                    );
                }
            }

        //
        // Validate the requested compute resource.
        log.debug("Validating the requested compute resources");
        IvoaAbstractComputeResource computeResource = offersetRequest.getCompute();
        if (computeResource == null)
            {
            computeResource = new IvoaSimpleComputeResource()
                .kind(SimpleComputeResource.TYPE_DISCRIMINATOR)
                .meta(
                    new IvoaComponentMetadata().name(
                        "Default compute resource"
                        )
                    );            
            }
        computeValidators.validate(
            computeResource,
            context
            );

        //
        // Validate the requested executable.
        log.debug("Validating the requested executable");
        IvoaAbstractExecutable executableResource = offersetRequest.getExecutable();
        if (executableResource != null)
            {
            executableValidators.validate(
                executableResource,
                context
                );
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
        // Calculate the preparation time.
        context.calculateTotalPrepareTime();
        
        //
        // Validate the schedule.
        validate(
            offersetRequest.getSchedule(),
            context
            );
        
        return context;
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
    public static final Duration DEFAULT_START_INTERVAL_DURATION = Duration.ofHours(2);

    /**
     * Validate the requested Schedule.
     *
     */
    public boolean validate(final IvoaRequestedScheduleBlock schedule, final OfferSetRequestParserContext context)
        {
        boolean success = true ;

        log.debug("validate(IvoaRequestedScheduleBlock)");

        //
        // Calculate the earliest start time.
        Instant earliestStartTime = Instant.now().plusSeconds(
            context.getTotalPrepareTime()
            );
        
        log.debug("Total prepare time [{}]",  context.getTotalPrepareTime());
        log.debug("Earliest start time [{}]", earliestStartTime);
        
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
                
                String startString = requested.getStart();
                if (startString != null)
                    {
                    try {
                        log.debug("Interval String [{}]", startString);
                        Interval startinterval = Interval.parse(
                            startString
                            );

                        log.debug("Interval value [{}]", startinterval);
                        if (startinterval.startsBefore(earliestStartTime))
                            {
                            log.warn("Start interval starts before earliest start time [{}][{}]", startinterval.getStart(), earliestStartTime);
                            // TODO Add a message ..
                            // TODO Fail the request ..
                            }
                        else {
                            context.setStartInterval(
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

        if (context.getStartInterval() == null)
            {
            Interval defaultStartInterval = Interval.of(
                earliestStartTime,
                DEFAULT_START_INTERVAL_DURATION
                );
            log.debug("Null start interval, using default [{}]", defaultStartInterval);
            context.setStartInterval(
                defaultStartInterval
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
    public OfferSetRequestParserContext process(final OfferSetRequestParserContext context)
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
            log.debug("Execution start [{}]", context.getStartInterval());
            log.debug("Execution duration [{}]", context.getExecutionDuration());
            
            log.debug("Min cores [{}]",  context.getTotalMinCores());
            log.debug("Max cores [{}]",  context.getTotalMaxCores());
            log.debug("Min memory [{}]", context.getTotalMinMemory());
            log.debug("Max memory [{}]", context.getTotalMaxMemory());
            log.debug("---- ---- ---- ----");

            //
            // Generate a list of offers for our criteria.
            List<ComputeResourceOffer> computeOffers = computeOfferFactory.generate(
                context.getStartInterval(),
                context.getExecutionDuration(),
                context.getTotalMinCores(),
                context.getTotalMinMemory()
                );
            //
            // Create an ExecutionSession for each offer. 
            for (ComputeResourceOffer computeOffer : computeOffers)
                {
                log.debug("OfferBlock [{}]", computeOffer.getStartTime());
                SimpleExecutionSessionEntity executionSessionEntity = executionSessionFactory.create(
                    context.getOfferSetEntity(),
                    context,
                    computeOffer
                    );
                log.debug("ExecutionEntity [{}]", executionSessionEntity);
                
                //
                // Build a new ExecutableEntity and add it to our SessionEntity.
                executionSessionEntity.setExecutable(
                    context.getExecutableResult().build(
                        executionSessionEntity
                        )
                    );
                
                //
                // Add our compute resources
                for (AbstractComputeResourceValidator.Result result : context.getComputeValidatorResults())
                    {
                    result.build(
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
                    result.build(
                        executionSessionEntity
                        );
                    }
                //
                // Add our volume mounts.
                for (AbstractVolumeMountValidator.Result result : context.getVolumeValidatorResults())
                    {
                    result.build(
                        executionSessionEntity
                        );
                    }
                
                //
                // Confirm we have at least one result.
                resultEnum = IvoaOfferSetResponse.ResultEnum.YES;
                }
            }
        //
        // Set the OfferSet result.
        context.getOfferSetEntity().setResult(
            resultEnum
            );

        return context;
        }
    }
