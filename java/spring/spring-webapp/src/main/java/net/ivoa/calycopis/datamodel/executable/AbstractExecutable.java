/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.session.Session;

/**
 * 
 */
public interface AbstractExecutable
    extends LifecycleComponent
    {
    /**
     * Get the parent Session.  
     *
     */
    public Session getSession();
    
    /**
     * Get an IVOA bean representation.
     *  
    public IvoaAbstractExecutable getIvoaBean(final String baseurl);
     */

    }
