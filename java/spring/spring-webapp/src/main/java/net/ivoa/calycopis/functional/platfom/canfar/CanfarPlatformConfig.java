/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.canfar;

import net.ivoa.calycopis.functional.platfom.vospace.VOSpacePlatformConfig;

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
