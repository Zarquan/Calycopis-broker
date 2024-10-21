/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.canfar;

import uk.co.metagrid.calycopis.factory.FactoryBase;

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
