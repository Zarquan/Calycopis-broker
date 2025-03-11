/**
 * 
 */
package net.ivoa.calycopis.platfom.vospace;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.platfom.ConfigStepEntity;

/**
 * Public interface for a StorageVolume mount in CANFAR.
 * Implemented as a directory in VOSpace.
 * 
 */
@Slf4j
public class VOSpaceUserDirectoryEntity
    extends VOSpaceConfigEntity
    implements VOspaceUserDirectoryConfig
    {

    /**
     * The default lifetime after the Execution completes.
     * Set to 1 minute.
     * 
     */
    public static final Duration DEFAULT_LIFETIME = Duration.ofMinutes(1L);

    /**
     * The default time needed to allocate a directory.
     * Set to 1 second.
     * 
     */
    public static final Duration DEFAULT_ALLOCATE_DURATION = Duration.ofSeconds(1L);

    /**
     * The default time needed to release a directory.
     * Set to 1 second.
     * 
     */
    public static final Duration DEFAULT_RELEASE_DURATION = Duration.ofSeconds(1L);

    /**
     * The directory path within the VOSpace filesystem.
     * 
     */
    private String canfarpath ;
    @Override
    public String getPath()
        {
        return this.canfarpath;
        }

    /**
     * Protected constructor.
     * 
     */
    protected VOSpaceUserDirectoryEntity()
        {
        super();
        }
    
    /**
     * Protected constructor.
     * 
     */
    public VOSpaceUserDirectoryEntity(final ConfigStepEntity parent, final VOSpacePlatformConfig platform, final String name, final String path, final VOSpaceCreateMode create, final VOSpaceAccesseMode access, final VOSpaceManageMode manage, final Duration lifetime)
        {
        super(
            parent,
            platform,
            name
            );
        this.synchronousAllocate  = true;
        this.thisallocateduration = DEFAULT_ALLOCATE_DURATION ;

        this.canfarpath = path;
        
        }

    @Override
    public void allocateBeforeChildren()
        {
        log.debug("allocateBeforeChildren()");
        //
        // Wait until needed ...
        // now > (execution.start - getAllocateDuration())
        //
        // Call VOSpace to create the directory.
        try {
            log.debug("VOSpace creating directory [{}])", this.canfarpath);
            Thread.sleep(DEFAULT_ALLOCATE_DURATION);
            log.debug("VOSpace created directory [{}])", this.canfarpath);
            }
        catch (InterruptedException ouch)
            {
            log.error("Interrupted [{}]", ouch.getMessage());
            }
        }

    @Override
    public void releaseAfterChildren()
        {
        log.debug("releaseAfterChildren()");
        //
        // Wait for the lifetime ...
        // now > (execution.endtime + lifetime)
        //
        //
        // Call VOSpace to release the directory.
        try {
            log.debug("VOSpace deleting directory [{}]", this.canfarpath);
            Thread.sleep(DEFAULT_RELEASE_DURATION);
            log.debug("VOSpace deleted directory [{}]", this.canfarpath);
            }
        catch (InterruptedException ouch)
            {
            log.error("Interrupted [{}]", ouch.getMessage());
            }
        }
    }
