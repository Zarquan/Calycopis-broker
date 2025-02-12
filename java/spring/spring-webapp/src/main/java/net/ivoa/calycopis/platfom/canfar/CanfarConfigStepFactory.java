/**
 * 
 */
package net.ivoa.calycopis.platfom.canfar;

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
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
    public ConfigStepEntity generate(final ExecutionSessionEntity execution);
    
    /**
     * Generate a ConfigStep for an Execution session.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final ExecutionSessionEntity execution);

    }
