/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import java.net.URI;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.session.ExecutionSession;

/**
 * 
 */
public interface AbstractExecutable
    extends LifecycleComponent
    {
    /**
     * The webapp path for executables.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("executables/"); 
    
    /**
     * Get the parent Session.  
     *
     */
    public ExecutionSession getSession();
    
    }
