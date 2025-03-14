/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.canfar;

import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.ConfigStepEntity;
import net.ivoa.calycopis.functional.platfom.ConfigStepFactory;

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
