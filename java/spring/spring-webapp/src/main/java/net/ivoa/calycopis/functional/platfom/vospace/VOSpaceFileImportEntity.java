/**
 * 
 */
package net.ivoa.calycopis.functional.platfom.vospace;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.platfom.ConfigStepEntity;

/**
 * 
 */
@Slf4j
public class VOSpaceFileImportEntity
    extends VOSpaceConfigEntity
    implements VOSpaceFileImportConfig
    {
    /**
     * The default time needed to download a file.
     * Set to 10 seconds.
     * 
     */
    public static final Duration DEFAULT_ALLOCATE_DURATION = Duration.ofSeconds(10L);

    /**
     * The default time needed to release a file.
     * Set to 1 second.
     * 
     */
    public static final Duration DEFAULT_RELEASE_DURATION = Duration.ofSeconds(1L);

    protected VOSpaceFileImportEntity()
        {
        super();
        }

    public VOSpaceFileImportEntity(final ConfigStepEntity parent, final VOSpacePlatformConfig platform, final String name, final String datasource, final String vospacepath, final VOSpaceCreateMode create, final VOSpaceAccesseMode access, final VOSpaceManageMode manage, final Duration lifetime)
        {
        super(
            parent,
            platform,
            name
            );
        this.synchronousAllocate  = true;
        // Depends on file size if we can get it ..
        // User supplied for complex things ?
        // Download a Git repo could take time.
        // If it fails to complete in time, we need to report this to the user.
        this.thisallocateduration = DEFAULT_ALLOCATE_DURATION ;
        
        this.datasource  = datasource;
        this.vospacepath = vospacepath;

        }

    /**
     * The the external data source to download.
     * 
     */
    private String datasource ;
    @Override
    public String getDataSource()
        {
        return this.datasource ;
        }
    
    /**
     * The path within the VOSpace filesystem.
     * 
     */
    private String vospacepath;
    @Override
    public String getVOSpacePath()
        {
        return this.vospacepath;
        }

    public void allocateBeforeChildren()
        {
        log.debug("allocateBeforeChildren()");
        // Wait until needed.
        // Import the data, async polling
        // sync allocate => call next
        }
    
    }
