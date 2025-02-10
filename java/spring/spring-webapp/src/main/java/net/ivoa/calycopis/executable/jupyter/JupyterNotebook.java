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
     * The type discriminator for JupyterNotebooks.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/executables/jupyter-notebook-1.0.yaml" ;

    /**
     * The URL path for JupyterNotebooks.
     *
     */
    //public static final String REQUEST_PATH = "/jupyternotebooks/" ;
    public static final String REQUEST_PATH = AbstractExecutable.REQUEST_PATH ;

    /**
     * Get the location of the notebook.
     *
     */
    public String getLocation();
    
    }
