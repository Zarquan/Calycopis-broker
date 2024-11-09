/**
 * 
 */
package net.ivoa.calycopis.platfom.vospace;

/**
 * Public interface for a user directory in CANFAR.
 * Implemented as a directory in VOSpace.
 *  
 */
public interface VOspaceUserDirectoryConfig
    extends VOSpaceConfigStep
    {

    public String getPath();
    
    }
