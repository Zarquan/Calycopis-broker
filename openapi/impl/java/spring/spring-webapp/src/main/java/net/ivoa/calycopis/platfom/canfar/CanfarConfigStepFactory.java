/**
 * 
 */
package net.ivoa.calycopis.platfom.canfar;

import net.ivoa.calycopis.execution.ExecutionEntity;
import net.ivoa.calycopis.platfom.ConfigStepEntity;
import net.ivoa.calycopis.platfom.ConfigStepFactory;

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
