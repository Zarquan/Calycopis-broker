/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.canfar;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.platfom.ConfigStepEntity;

/**
 * 
 */
@Slf4j
public class CanfarConfigStepEntity
    extends ConfigStepEntity 
    implements CanfarConfigStep
    {

    protected CanfarConfigStepEntity()
        {
        super();
        }

    // Only the top level ExecutionSession has no parent. 
    protected CanfarConfigStepEntity(final CanfarPlatformConfig platform, final String name)
        {
        this(
            null,
            platform,
            name
            );
        }

    // Everything else should have a parent.
    // Validate, parent should not be null, and parent.platform should not be null.
    protected CanfarConfigStepEntity(final ConfigStepEntity parent, final CanfarPlatformConfig platform, final String name)
        {
        super(
            parent,
            name
            );
        this.platform = platform;
        }
    
    protected CanfarPlatformConfig platform;
    @Override
    public CanfarPlatformConfig getPlatformConfig()
        {
        return this.platform;
        }

    }
