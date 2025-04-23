/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import net.ivoa.calycopis.datamodel.component.Component;
import net.ivoa.calycopis.datamodel.session.ExecutionSession;
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
     * Get an IVOA bean representation.
     *  
     */
    public IvoaAbstractExecutable getIvoaBean(final String baseurl);

    }
