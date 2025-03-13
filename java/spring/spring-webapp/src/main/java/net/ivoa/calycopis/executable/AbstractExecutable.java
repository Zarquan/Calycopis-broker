/**
 * 
 */
package net.ivoa.calycopis.executable;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.execution.ExecutionSession;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;

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
    
    /**
     * Get an Ivoa bean representation.
     *  
     */
    public IvoaAbstractExecutable getIvoaBean(final String baseurl);

    }
