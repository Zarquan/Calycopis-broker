/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic.canfar;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.platfomattic.vospace.VOSpacePlatformConfig;

/**
 * 
 */
@Slf4j
public class CanfarPlatformConfigImpl
implements CanfarPlatformConfig
    {

    public CanfarPlatformConfigImpl(final String name, final VOSpacePlatformConfig vospaceconfig)
        {
        this.vospaceconfig = vospaceconfig ;
        }
    
    private String name;
    @Override
    public String getName()
        {
        return this.name;
        }

    private VOSpacePlatformConfig vospaceconfig;
    @Override
    public VOSpacePlatformConfig getVospacePlatform()
        {
        return this.vospaceconfig;
        }
    }
