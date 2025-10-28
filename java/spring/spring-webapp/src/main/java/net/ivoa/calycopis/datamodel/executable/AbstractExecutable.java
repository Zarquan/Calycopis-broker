/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.session.ExecutionSession;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;

/**
 * 
 */
public interface AbstractExecutable
    extends LifecycleComponent
    {
    /**
     * Get the parent ExecutionSession.  
     *
     */
    public ExecutionSession getSession();
    
    /**
     * Get an IVOA bean representation.
     *  
     */
    public IvoaAbstractExecutable getIvoaBean(final String baseurl);

    }
