/**
 * 
 */
package net.ivoa.calycopis.platfom.canfar;

import net.ivoa.calycopis.factory.FactoryBase;

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
