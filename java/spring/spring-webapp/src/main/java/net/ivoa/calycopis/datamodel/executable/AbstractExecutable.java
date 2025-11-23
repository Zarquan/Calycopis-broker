/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import java.net.URI;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSession;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSession;

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
    public AbstractExecutionSession getSession();
    
    }
