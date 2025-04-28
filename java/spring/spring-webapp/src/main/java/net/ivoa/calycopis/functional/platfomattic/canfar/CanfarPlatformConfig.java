/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic.canfar;

import net.ivoa.calycopis.functional.platfomattic.vospace.VOSpacePlatformConfig;

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
