/**
 * 
 */
package uk.co.metagrid.calycopis.platfom.canfar;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.platfom.ConfigStepEntity;
import uk.co.metagrid.calycopis.platfom.vospace.VOSpaceConfigStep;
import uk.co.metagrid.calycopis.platfom.vospace.VOSpaceFileImportEntity;
import uk.co.metagrid.calycopis.platfom.vospace.VOSpaceUserDirectoryEntity;

/**
 * 
 */
@Slf4j
public class CanfarExecutionSessionEntity
    extends CanfarConfigStepEntity
    implements CanfarExecutionSession
    {
    public static Duration DEFAULT_SESSION_HOME_LIFETIME ;
            
    private ExecutionEntity execution;

    // Details of the CANFAR service.
    
    private String username;
    private String userhome;

    private String sessionname;
    private String sessionhome;

    protected CanfarExecutionSessionEntity()
        {
        super();
        }

    protected CanfarExecutionSessionEntity(final CanfarPlatformConfig platform, final ExecutionEntity execution, final String username)
        {
        super(
            platform,
            "CANFAR session"
            );
        this.execution = execution;

        this.username = username;
        this.userhome = "/home/" + username ;

        this.sessionname = this.uuid.toString();
        this.sessionhome = this.userhome + "/sesisons/" + this.sessionname ;

// we are mixing up validation steps with the actual processing steps
// this should be just the processing steps, handling fixed values from the execution         
        
        // Check our user home.
        // userhome = new CanfarUserHomeEntity()
        // Is this a live query or an attribute of our platform config ?

        // Create our session home.
        // TODO mode
        // TODO permissions
        // TODO fail if it already exists.
        this.addChild(
            new VOSpaceUserDirectoryEntity(
                ((ConfigStepEntity) this),
                this.platform.getVospacePlatform(),
                "Create session home for [" + this.uuid + "]",
                this.sessionhome,
                VOSpaceConfigStep.VOSpaceCreateMode.CREATE_UNIQUE,
                VOSpaceConfigStep.VOSpaceAccesseMode.READ_WRITE,
                VOSpaceConfigStep.VOSpaceManageMode.CREATE_AND_RELEASE,
                DEFAULT_SESSION_HOME_LIFETIME
                )
            );

        // Download our data
        // sessiondata = new CanfarDataResourceGroup(sessionhome)

        // ** Specific to notebook
        // Download our notebook
        // notebook = new CanfarFileDownload(sessionhome, notebook.source, notebook.name)
        this.addChild(
            new VOSpaceFileImportEntity(
                this,
                this.platform.getVospacePlatform(),
                "Notebook import for [" + this.uuid + "]",
                "notebnook.source",
                this.sessionhome,
                VOSpaceConfigStep.VOSpaceCreateMode.CREATE_UNIQUE,
                VOSpaceConfigStep.VOSpaceAccesseMode.READ_WRITE,
                VOSpaceConfigStep.VOSpaceManageMode.CREATE_AND_RELEASE,
                Duration.ofMinutes(2L)
                )
            );

        // validation
        // Check our compute resources
        // compute new CanfarComputeResourceGroup()
        // list the compute available to the user
        // get /v0/context
        //

        // validation
        // Check our container image
        // image = new CanfarContainerImage(container)
        // list the images available to the user
        // get /v0/image
        // Add an optional download step ?
        // Import time depends on repository and cached data
        //
        
        // ** Specific to container 
        // Launch our container
        // waits until needed (T - 30sec)
        // container = new CanfarSessionLaunch(container, cores, memory)
        // Launch time depends on repository and cached data
        
        // Specific to notebook
        // Update our endpoint URL
        // execution.executable.endpoint = 
        
        // Update our execution status
        // execution.state = RUNNING 

        // Wait until end time.

        // Update our execution status
        // execution.state = COMPLETED

        // Initiate the release sequence ..

        }
    }
