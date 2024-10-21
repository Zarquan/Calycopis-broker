/**
 * 
 */
package uk.co.metagrid.calycopis.platfom;

import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceEntity;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceEntity;
import uk.co.metagrid.calycopis.executable.AbstractExecutableEntity;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.factory.FactoryBase;
import uk.co.metagrid.calycopis.storage.simple.SimpleStorageResourceEntity;

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
