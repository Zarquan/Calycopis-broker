/**
 * 
 */
package net.ivoa.calycopis.platfom.canfar;

import net.ivoa.calycopis.platfom.vospace.VOSpacePlatformConfig;

/**
 * 
 */
public interface CanfarPlatformConfig
    {

    /**
     *  Get the platform name.
     *  
     */
    public String getName();

    /**
     *  Get the associated VOSPace platform.
     *  
     */
    public VOSpacePlatformConfig getVospacePlatform();

    }
