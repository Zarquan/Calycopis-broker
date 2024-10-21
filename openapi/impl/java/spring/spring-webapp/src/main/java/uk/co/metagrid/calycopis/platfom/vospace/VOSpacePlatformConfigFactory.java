/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.vospace;

import uk.co.metagrid.calycopis.factory.FactoryBase;
import uk.co.metagrid.calycopis.platfom.canfar.CanfarPlatformConfig;

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
