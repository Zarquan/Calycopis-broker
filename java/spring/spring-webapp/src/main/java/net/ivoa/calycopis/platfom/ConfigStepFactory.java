/**
 * 
 */
package net.ivoa.calycopis.platfom;

import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.execution.ExecutionEntity;
import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.storage.simple.SimpleStorageResourceEntity;

/**
 * 
 */
public interface ConfigStepFactory
extends FactoryBase
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

    /**
     * Generate a ConfigStep for an Executable.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final AbstractExecutableEntity executable);

    /**
     * Generate a ConfigStep for a data resource.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final SimpleDataResourceEntity resource);

    /**
     * Generate a ConfigStep for a compute resource.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final SimpleComputeResourceEntity resource);

    /**
     * Generate a ConfigStep for a storage resource.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final SimpleStorageResourceEntity resource);

    }
