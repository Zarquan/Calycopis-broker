/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.vospace;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.platfom.ConfigStep;
import uk.co.metagrid.calycopis.platfom.ConfigStepEntity;

/**
 * 
 */
@Slf4j
public class VOSpaceConfigEntity
    extends ConfigStepEntity 
    implements VOSpaceConfigStep
    {

    protected VOSpaceConfigEntity()
        {
        super();
        }

    protected VOSpaceConfigEntity(final ConfigStepEntity parent, final VOSpacePlatformConfig platform, final String name)
        {
        super(
            parent,
            name
            );
        this.platform = platform;
        this.init();
        }

    protected void init()
        {
        log.debug("init()");
        }
    
    protected VOSpacePlatformConfig platform;
    @Override
    public VOSpacePlatformConfig getPlatformConfig()
        {
        return this.platform;
        }

    }