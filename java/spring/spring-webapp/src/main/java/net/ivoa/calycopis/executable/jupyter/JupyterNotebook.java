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
     * The database table name for JupyterNotebooks.
     * 
     */
    public static final String TABLE_NAME = "jpnotebooks" ;

    /**
     * The type discriminator for JupyterNotebooks.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "urn:jupyter-notebook-0.1" ;

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
    public String getNotebook();
    
    }
