/**
 * 
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import jakarta.validation.executable.ExecutableValidator;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceBean;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntityFactory;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offers.OfferBlockFactory;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequestSchedule;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.validator.compute.ComputeResourceValidatorFactory;
import net.ivoa.calycopis.validator.data.DataResourceValidatorFactory;
import net.ivoa.calycopis.validator.executable.ExecutableValidator.Result;
import net.ivoa.calycopis.validator.executable.ExecutableValidatorFactory;
import net.ivoa.calycopis.validator.storage.StorageResourceValidatorFactory;

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
    private final OfferBlockFactory offerBlockFactory;

    // TODO Replace this with a builder ..
    private final ExecutionSessionEntityFactory      executionSessionFactory;

    /**
     * Executable Validators.
     * 
     */
    private final ExecutableValidatorFactory executableValidators;

    /**
     * Storage resource Validators.
     * 
     */
    private final StorageResourceValidatorFactory storageValidators;

    /**
     * Data resource Validators.
     * 
     */
    private final DataResourceValidatorFactory dataValidators;

    /**
     * Compute resource Validators.
     * 
     */
    private final ComputeResourceValidatorFactory computeValidators;

    @Autowired
    public OfferSetRequestParserImpl(
        final OfferBlockFactory offerBlockFactory, 
        final ExecutableValidatorFactory executableValidators,
        final StorageResourceValidatorFactory storageValidators, 
        final DataResourceValidatorFactory dataValidators, 
        final ComputeResourceValidatorFactory computeValidators,
        final ExecutionSessionEntityFactory executionSessionFactory
        ){
        super();
        this.offerBlockFactory    = offerBlockFactory ;
        this.executableValidators = executableValidators ;
        this.storageValidators    = storageValidators ;
        this.dataValidators       = dataValidators ;
        this.computeValidators    = computeValidators ;
        this.executionSessionFactory = executionSessionFactory;
        }
    
    @Override
    public void process(final IvoaOfferSetRequest offersetRequest, final OfferSetEntity offersetEntity)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        OfferSetRequestParserState state = new OfferSetRequestParserStateImpl(
            this,
            offersetRequest,
            offersetEntity
            );
        //
        // Validate the request.
        validate(
            state
            );
        // Exit if something was rejected.
        
        //
        // Construct the offers.
        // ...
        
        }
    
    /**
     * Validate the request components.
     * 
     */
    public void validate(final OfferSetRequestParserState state)
        {
        log.debug("validate(OfferSetRequestParserState)");
        final IvoaOfferSetRequest offersetRequest = state.getOriginalOfferSetRequest();
        final IvoaOfferSetRequest offersetResult  = state.getValidatedOfferSetRequest();
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
                        state
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
                        state
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
                        state
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
        if (state.getComputeValidatorResults().isEmpty())
            {
            log.debug("Adding a default compute resource");
            IvoaSimpleComputeResource compute = new IvoaSimpleComputeResource(
                SimpleComputeResourceBean.TYPE_DISCRIMINATOR 
                );
            computeValidators.validate(
                compute,
                state
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
                state
                );
            // TODO Check the result ?
            }
        else {
            log.error("Offerset request has no executable");
            state.getOfferSetEntity().addWarning(
                "urn:executable-required",
                "Description of the executable is required"
                );
            state.valid(false);
            }
        
        //
        // Validate the schedule.
        validate(
            offersetRequest.getSchedule(),
            state
            );
        
//
// Move this part to a separate platforms specific validator.
//
        
        //
        // Fail if we have found too many compute resources,
        // This is specific to the CANMFAR platforms.
        // Other platforms may be able to support more than one compute resources.
        //if (offersetResult.getResources().getCompute().size() > 1)
        if (state.getComputeValidatorResults().size() > 1)
            {
            log.warn("Found more than one compute resources");
            state.getOfferSetEntity().addWarning(
                "urn:not-supported-message",
                "Multiple compute resources not supported"
                );
            state.valid(false);
            }
        
        // Exit if errors ..
        
        build(state);
        }

    
//
// TOD Move this part to a separate schedule validator.
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
    public void validate(final IvoaOfferSetRequestSchedule schedule, final OfferSetRequestParserState state)
        {
        log.debug("validate(IvoaExecutionSessionRequestSchedule)");
        if (schedule != null)
            {
            IvoaScheduleRequestBlock requested = schedule.getRequested();
            if (requested != null)
                {
                String durationstr = requested.getDuration();
                if (durationstr != null)
                    {
                    try {
                        Duration durationval = Duration.parse(
                            durationstr
                            );
                        log.debug("Duration [{}][{}]", durationstr, durationval);
                        state.setExecutionDuration(
                            durationval
                            );
                        }
                    catch (Exception ouch)
                        {
                        state.getOfferSetEntity().addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse duration [${string}][${message}]",
                            Map.of(
                                "value",
                                durationstr,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        state.valid(false);
                        }
                    }

                List<String> startstrlist = requested.getStart();
                if (startstrlist != null)
                    {
                    for (String startstr : startstrlist)
                        {
                        try {
                            Interval startint = Interval.parse(
                                startstr
                                );
                            log.debug("Interval [{}][{}]", startstr, startint);
                            state.addStartInterval(
                                startint
                                );
                            }
                        catch (Exception ouch)
                            {
                            state.getOfferSetEntity().addWarning(
                                "urn:input-syntax-fail",
                                "Unable to parse interval [${string}][${message}]",
                                Map.of(
                                    "value",
                                    startstr,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            state.valid(false);
                            }
                        }
                    }
                }
            }

        if (state.getStartIntervals().isEmpty())
            {
            Interval defaultint = Interval.of(
                Instant.now(),
                DEFAULT_START_DURATION
                );
            log.debug("Interval list is empty, adding default [{}]", defaultint);
            state.addStartInterval(
                defaultint
                );
            }

        if (state.getExecutionDuration() == null)
            {
            log.debug("Duration is empty, using default [{}]", DEFAULT_SESSION_DURATION);
            state.setExecutionDuration(
                DEFAULT_SESSION_DURATION
                );
            }
        }
    
    /**
     * Build the entities from the validated input.
     *  
     */
    public void build(final OfferSetRequestParserState state)
        {
        log.debug("build(final OfferSetRequestParserState)");

        //
        // Start with NO, and set to YES when we have at least one offer.
        IvoaOfferSetResponse.ResultEnum result = IvoaOfferSetResponse.ResultEnum.NO;

        //
        // If everything is OK.
        if (state.valid())
            {
            //
            // Generate some offers ..
            log.debug("---- ---- ---- ----");
            log.debug("Generating offers ....");
            log.debug("Start intervals [{}]", state.getStartIntervals());
            log.debug("Execution duration [{}]", state.getExecutionDuration());
            
            log.debug("Min cores [{}]",  state.getTotalMinCores());
            log.debug("Max cores [{}]",  state.getTotalMaxCores());
            log.debug("Min memory [{}]", state.getTotalMinMemory());
            log.debug("Max memory [{}]", state.getTotalMaxMemory());
            log.debug("---- ---- ---- ----");

            //
            // Populate our OfferSet ..
            for (Interval startInterval : state.getStartIntervals())
                {
                //
                // Generate a list of available blocks.
                List<OfferBlock> offerblocks = offerBlockFactory.generate(
                    startInterval,
                    state.getExecutionDuration(),
                    state.getTotalMinCores(),
                    state.getTotalMinMemory()
                    );
                //
                // Create an ExecutionSession for each block. 
                for (OfferBlock offerblock : offerblocks)
                    {
                    log.debug("OfferBlock [{}]", offerblock.getStartTime());
                    ExecutionSessionEntity executionSessionEntity = executionSessionFactory.create(
                        offerblock,
                        state.getOfferSetEntity(),
                        state
                        );
                    log.debug("ExecutionEntity [{}]", executionSessionEntity.getUuid());

                    Result executableResult = state.getExecutable();
                    
                    
                    


                    /*
                     * 
                    log.debug("Executable [{}][{}]", state.getExecutable().getObject().getName(), state.getExecutable().getObject().getClass().getName());
                    
                    for (DataResourceValidator.Result dataResult : state.getDataResourceValidatorResults())
                        {
                        log.debug("Data result [{}]", dataResult);
                        }
                    
                    for (ComputeResourceValidator.Result computeResult : state.getComputeValidatorResults())
                        {
                        log.debug("Compute result [{}]", computeResult);
                        }
                     * 
                     */

                    
                    /*
                     * TODO Add the builder references ...
                    execution.setExecutable(
                        jupyterNotebookFactory.create(
                            execution,
                            ((JupyterNotebookEntity) this.executable)
                            )
                        );
                      *
                      */

                    /*
                     * We only support a single compute resource.
                     * TODO Add the builder references ...
                     *
                    ComputeResourceValidator.Result firstResult = compValidatorResultList.getFirst();
                    
                    IvoaAbstractComputeResource firstResource = firstResult.getObject();
                    if (firstResource instanceof IvoaSimpleComputeResource)
                        {
                        execution.addComputeResource(
                            simpleComputeFactory.create(
                                execution,
                                ((IvoaSimpleComputeResource) firstResource),
                                offerblock.getCores(),
                                offerblock.getCores(),
                                offerblock.getMemory(),
                                offerblock.getMemory()
                                )
                            );
                        }
                     *
                     */
                    
                    //
                    // Confirm we have at least one result.
                    result = IvoaOfferSetResponse.ResultEnum.YES;
                    }
                }
            }
        //
        // Set the OfferSet result.
        state.getOfferSetEntity().setResult(
            result
            );
        }
    }
