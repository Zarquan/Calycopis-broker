/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.resource.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.simple.SimpleStorageResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;

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
    public ConfigStepEntity generate(final ExecutionSessionEntity execution);
    
    /**
     * Generate a ConfigStep for an Execution session.
     * 
     */
    public ConfigStepEntity generate(final ConfigStepEntity parent, final ExecutionSessionEntity execution);

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
