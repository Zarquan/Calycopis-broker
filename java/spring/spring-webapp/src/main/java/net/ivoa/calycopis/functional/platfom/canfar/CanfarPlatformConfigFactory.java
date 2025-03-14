/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.canfar;

import net.ivoa.calycopis.functional.factory.FactoryBase;

/**
 * TODO Move this to a database managed entity later ..
 * 
 */
public interface CanfarPlatformConfigFactory
    extends FactoryBase
    {
    /**
     * Get the local CANFAR platform configuration.
     * 
     */
    public CanfarPlatformConfig local();
    
    }
