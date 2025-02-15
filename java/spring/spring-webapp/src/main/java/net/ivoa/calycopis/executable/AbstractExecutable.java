/**
 * 
 */
package net.ivoa.calycopis.executable;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.execution.ExecutionSession;

/**
 * 
 */
public interface AbstractExecutable
    extends Component
    {
    /**
     * Get the parent ExecutionSession.  
     *
     */
    public ExecutionSession getParent();

    }
