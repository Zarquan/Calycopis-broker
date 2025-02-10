/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import net.ivoa.calycopis.executable.AbstractExecutable;

/**
 * 
 */
public interface JupyterNotebook
    extends AbstractExecutable
    {
    /**
     * Get the location of the notebook.
     *
     */
    public String getLocation();
    
    }
