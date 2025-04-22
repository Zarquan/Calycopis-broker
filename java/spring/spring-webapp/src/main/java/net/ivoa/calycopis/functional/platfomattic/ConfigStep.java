/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.component.Component;

/**
 * A ConfigStep represents a step in a chain of things that need to be done to prepare for an Execution session.
 * 
 * We need to create an initial chain of steps in order to calculate the preparation time.
 * The sum of the preparation times is used to calculate the minimum start time of the Execution session offers.
 *
 * Then we generate a ConfigStep chain for each of the Execution session offers, which are persisted in the database.
 * The database steps are triggered by the callback methods at the appropriate time.
 * 
 */
public interface ConfigStep
extends Component
    {
    /**
     * The URL path for ConfigSteps.
     *
     */
    public static final String REQUEST_PATH = "/configsteps/" ;

    /**
     * A state value to represent where this step is in the time line.
     *
     */
    public ConfigStepState getState();

    /**
     * Get an estimate for how long this step will take to allocate.
     *
     */
    public Duration getAllocateDuration();

    /**
     * Get an estimate for how long this step will take to release.
     *
     */
    public Duration getReleaseDuration();

    /**
     * Start the allocation process for this step.
     *
     */
    public void doAllocation();

    /**
     * Complete the allocation process for this step.
     *
     */
    public void allocateDone();
    
    /**
     * Start the release process for this step.
     *
     */
    public void doRelease();

    /**
     * Complete the release process for this step.
     *
     */
    public void releaseDone();
    
    /**
     * Get the parent step.
     *
     */
    public ConfigStep getParentConfigStep();

    /**
     * Get the next step in the allocation chain.
     *
     */
    public ConfigStep getNextAllocateStep();

    /**
     * Get the next step in the release chain.
     *
     */
    public ConfigStep getNextReleaseStep();
    
    }
