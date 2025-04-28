/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic.vospace;

import net.ivoa.calycopis.functional.platfomattic.ConfigStep;

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
