/**
 * 
 */
package net.ivoa.calycopis.platfom.canfar;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;

/**
 * 
 */
@Slf4j
public class CanfarComputeResourcesEntity
    extends CanfarConfigStepEntity
    implements CanfarComputeResourcesGroup
    {

    private ExecutionSessionEntity execution;
    
    private Long totalcores  = 0L;
    private Long totalmemory = 0L;
    
    protected CanfarComputeResourcesEntity(final CanfarConfigStepEntity parent, final CanfarPlatformConfig platform, final ExecutionSessionEntity execution)
        {
        super(
            parent,
            platform,
            "CANFAR compute resources"
            );
        this.execution = execution;

        // TODO
        // Rejecting because we only support one is done here.
        // Adding messages and failing the request.
        //
        
        // This is soo much part of the main validaton classes.
        // CanfarComputeExecutionEntity is an ExecutionEntity
        // with a validate() method ..

        // Check the list size
        // zero, add a default
        // >1 reject the request
        // get the first compute resource
        // get the list of shapes for this platform 
        // find the smallest shape that matches
        // update the request to match the shape
        // query the blocks
        // for each offer 
        //    find the smallest shape that fits the request,
        //    step up the list to find the largest shape that fits in the block
        //    different routes up the list, cores first, memory first etc. 
        //    
        // So this takes over the request validation and offer steps.
        //
        
        for (SimpleComputeResource compute : this.execution.getComputeResources())
            {
            // TODO
            // Find the best shape(s) to match the request.
            // We have to pick one of these.
            // Update the compute resource offer to match.
            
            this.totalcores  += compute.getMinRequestedCores();
            this.totalmemory += compute.getMinRequestedMemory();

            }
        }
    }
