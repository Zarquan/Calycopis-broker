/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.canfar;

import uk.co.metagrid.calycopis.platfom.vospace.VOSpacePlatformConfig;

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
