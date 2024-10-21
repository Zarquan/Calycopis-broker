/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.vospace;

import uk.co.metagrid.calycopis.platfom.ConfigStep;

/**
 * 
 */
public interface VOSpaceConfigStep
extends ConfigStep
    {
    public static enum VOSpaceCreateMode
        {
        CHECK_EXISTS(),
        CREATE_UNIQUE(),
        EXISTS_OR_CREATE();
        }

    public static enum VOSpaceManageMode
        {
        UNMANAGED(),
        CREATE_ONLY(),
        CREATE_AND_RELEASE();
        }
    
    public static enum VOSpaceAccesseMode
        {
        READ_ONLY(),
        READ_WRITE();
        }

    public VOSpacePlatformConfig getPlatformConfig();
        
    }
