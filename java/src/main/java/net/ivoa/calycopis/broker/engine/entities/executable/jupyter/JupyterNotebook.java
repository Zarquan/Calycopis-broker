/**
 * 
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter;

import java.net.URI;

import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutable;

/**
 * 
 */
public interface JupyterNotebook
    extends AbstractExecutable
    {
    /**
     * The OpenAPI type identifier.
     * 
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/jupyter-notebook-1.0") ;
    
    /**
     * Get the location of the notebook.
     *
     */
    public String getLocation();
    
    }
