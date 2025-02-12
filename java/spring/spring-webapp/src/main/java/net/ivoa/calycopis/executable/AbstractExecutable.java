/**
 * 
 */
package net.ivoa.calycopis.executable;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;

/**
 * 
 */
public interface AbstractExecutable
    extends Component
    {
    /**
     * Get the parent ExecutionEntity.  
     *
     */
    public ExecutionSessionEntity getParent();

    }
