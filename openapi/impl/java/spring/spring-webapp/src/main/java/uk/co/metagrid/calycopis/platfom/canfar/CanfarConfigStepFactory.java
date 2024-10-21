/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.canfar;

import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.platfom.ConfigStepEntity;
import uk.co.metagrid.calycopis.platfom.ConfigStepFactory;

/**
 * 
 */
public interface CanfarConfigStepFactory
extends ConfigStepFactory
    {
    /**
     * Generate a top level ConfigStep for an Execution session.
     * 
     */
    public ConfigStepEntity generate(final ExecutionEntity execution);
    
    /**
     * Generate a ConfigStep for an Execution session.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final ExecutionEntity execution);

    }
