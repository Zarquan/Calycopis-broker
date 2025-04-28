/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;

/**
 * 
 */
@Slf4j
public class ConfigStepEntity
    extends ComponentEntity
    implements ConfigStep
    {
    
    protected ConfigStepEntity()
        {
        super();
        }

    protected ConfigStepEntity(final ConfigStepEntity parent, final String name)
        {
        super(name);
        this.parentconfigstep = parent;
        }

    private ConfigStepState state;
    @Override
    public ConfigStepState getState()
        {
        return this.state;
        }

    private ConfigStepEntity parentconfigstep;
    @Override
    public ConfigStepEntity getParentConfigStep()
        {
        return this.parentconfigstep;
        }
    protected void setParentConfigStep(final ConfigStepEntity parent)
        {
        this.parentconfigstep = parent;
        }

    private ConfigStepEntity firstchild;
    private ConfigStepEntity lastchild;
    
    protected void addChild(final ConfigStepEntity newchild)
        {
        newchild.setParentConfigStep(this);
        if (this.firstchild == null)
            {
            this.firstchild = newchild;
            }
        if (this.lastchild != null)
            {
            this.lastchild.setNextAllocateStep(newchild);
            }
        newchild.setNextReleaseStep(this.lastchild);
        this.lastchild = newchild;

        this.childallocateduration = this.childallocateduration.plus(
            newchild.getAllocateDuration()
            );
        }

    /**
     * A flag to indicate if the allocation process is synchronous.
     *  
     */
    protected boolean synchronousAllocate = true;

    /**
     * The allocation Duration for *this* step.
     * 
     */
    protected Duration thisallocateduration  = Duration.ZERO;

    /**
     * The sum of allocation Duration for our children.
     * 
     */
    protected Duration childallocateduration = Duration.ZERO;

    @Override
    public Duration getAllocateDuration()
        {
        return this.getAllocateDuration(false);
        }

    /**
     * Get the total allocation Duration.
     * Recalculate the allocation Duration for our children.
     * 
     */
    public Duration getAllocateDuration(boolean recalculate)
        {
        log.error("getAllocateDuration({})", recalculate);
        if (recalculate)
            {
            this.childallocateduration = Duration.ZERO;
            ConfigStepEntity step = this.firstchild;  
            while (step != null)
                {
                this.childallocateduration = this.childallocateduration.plus(
                    step.getAllocateDuration(true)
                    );
                step = step.getNextAllocateStep();
                }
            }
        return this.thisallocateduration.plus(
            childallocateduration
            );
        }
    
    private ConfigStepEntity nextallocatestep;
    @Override
    public ConfigStepEntity getNextAllocateStep()
        {
        return this.nextallocatestep;
        }
    public void setNextAllocateStep(final ConfigStepEntity next)
        {
        this.nextallocatestep = next;
        }
    
    @Override
    public void doAllocation()
        {
        log.error("doAllocation()");
        this.state = ConfigStepState.ALLOCATING; 
        this.allocateBeforeChildren();
        this.allocateChildren();
        this.allocateAfterChildren();
        if (this.synchronousAllocate)
            {
            this.allocateDone();
            }
        }

    public void allocateBeforeChildren()
        {
        log.debug("allocateBeforeChildren()");
        }
    
    public void allocateChildren()
        {
        log.debug("allocateChildren()");
        if (this.firstchild != null)
            {
            this.firstchild.doAllocation();
            }
        else {
            log.debug("no children");
            }
        }

    public void allocateAfterChildren()
        {
        log.debug("allocateAfterChildren()");
        }
    
    @Override
    public void allocateDone()
        {
        log.error("allocationDone()");
        this.state = ConfigStepState.READY; 
        if (this.nextallocatestep != null)
            {
            this.nextallocatestep.doAllocation();
            }
        else if (this.parentconfigstep != null)
            {
            this.parentconfigstep.allocateDone();
            }
        else {
            log.error("End of allocation chain - no next, no parent");
            }
        }
    
    /**
     * A flag to indicate if the release process is synchronous. 
     *  
     */
    protected boolean synchronousRelease = true;

    private Duration thisreleaseduration  = Duration.ZERO;;
    private Duration childreleaseduration = Duration.ZERO;;
    @Override
    public Duration getReleaseDuration()
        {
        return this.getReleaseDuration(false);
        }

    /**
     * Get the total release Duration.
     * Recalculate the release Duration for our children.
     * 
     */
    public Duration getReleaseDuration(boolean recalculate)
        {
        log.error("getReleaseDuration({})", recalculate);
        if (recalculate)
            {
            this.childreleaseduration = Duration.ZERO;
            ConfigStepEntity step = this.firstchild;  
            while (step != null)
                {
                this.childreleaseduration = this.childreleaseduration.plus(
                    step.getReleaseDuration(true)
                    );
                step = step.getNextAllocateStep();
                }
            }
        return this.thisreleaseduration.plus(
            childreleaseduration
            );
        }

    private ConfigStepEntity nextreleasestep;
    @Override
    public ConfigStepEntity getNextReleaseStep()
        {
        return this.nextreleasestep;
        }
    public void setNextReleaseStep(final ConfigStepEntity next)
        {
        this.nextreleasestep = next;
        }

    @Override
    public void doRelease()
        {
        log.error("doRelease()");
        this.state = ConfigStepState.RELEASING; 
        this.releaseBeforeChildren();
        this.releaseChildren();
        this.releaseAfterChildren();
        if (this.synchronousRelease)
            {
            this.releaseDone();
            }
        }

    @Override
    public void releaseDone()
        {
        log.error("releaseDone()");
        this.state = ConfigStepState.COMPLETED; 
        if (this.nextreleasestep != null)
            {
            this.nextreleasestep.doRelease();
            }
        else if (this.parentconfigstep != null)
            {
            this.parentconfigstep.releaseDone();
            }
        else {
            log.error("End of release chain - no next, no parent");
            }
        }

    public void releaseBeforeChildren()
        {
        log.debug("releaseBeforeChildren()");
        }
    
    public void releaseChildren()
        {
        log.debug("releaseChildren()");
        if (this.lastchild != null)
            {
            this.lastchild.doRelease();
            }
        }

    public void releaseAfterChildren()
        {
        log.debug("releaseAfterChildren()");
        }

    }
