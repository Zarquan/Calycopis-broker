/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import net.ivoa.calycopis.executable.AbstractExecutable;

/**
 *
 */
public interface DockerContainer
    extends AbstractExecutable
    {
    /**
     * The database table name for DockerContainers.
     *
     */
    public static final String TABLE_NAME = "dockercont" ;

    /**
     * The type discriminator for DockerContainers.
     *
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/executables/docker-container-1.0.yaml" ;

    /**
     * The URL path for DockerContainers.
     *
     */
    //public static final String REQUEST_PATH = "/jupyternotebooks/" ;
    public static final String REQUEST_PATH = AbstractExecutable.REQUEST_PATH ;

    /**
     * Get the location of the notebook.
     *
    public String getNotebook();
     */

    }
