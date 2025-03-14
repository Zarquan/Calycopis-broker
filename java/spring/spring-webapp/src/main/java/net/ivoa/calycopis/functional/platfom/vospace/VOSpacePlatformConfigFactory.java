/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.vospace;

import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.functional.platfom.canfar.CanfarPlatformConfig;

/**
 * TODO Move this to a database managed entity later ..
 * 
 */
public interface VOSpacePlatformConfigFactory
    extends FactoryBase
    {
    /**
     * Get the local CANFAR platform configuration.
     * 
     */
    public CanfarPlatformConfig local();
    
    }
