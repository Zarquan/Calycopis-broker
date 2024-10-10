/**
 * 
 */
package uk.co.metagrid.calycopis.processing;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.ambleck.model.ProcessingContext.ScheduleItem;
import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceEntity;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceEntity;
import uk.co.metagrid.calycopis.executable.AbstractExecutable;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;

/**
 * 
 */
public interface NewProcessingContext
    {
    public boolean valid();
    public void valid(boolean value);
    public void fail();

    public IvoaOfferSetRequest getOfferSetRequest();
    public OfferSetEntity getOfferSetEntity();

    public void addDataResource(final IvoaAbstractDataResource data);
    public List<SimpleDataResourceEntity> getDataResourceList();
    public SimpleDataResourceEntity findDataResource(final String key);

    public void addComputeResource(final IvoaAbstractComputeResource comp);
    public List<SimpleComputeResourceEntity> getComputeResourceList();
    public SimpleComputeResourceEntity findComputeResource(final String key);

    public void addExecutable(final IvoaAbstractExecutable executable);
    public AbstractExecutable getExecutable();

    // This is a total over all the compute resources.
    public long getMinCores();
    //public int getMaxCores();
    public void addMinCores(long delta);
    //public void addMaxCores(int delta);

    // This is a total over all the compute resources.
    public long getMinMemory();
    //public int getMaxMemory();
    public void addMinMemory(long delta);
    //public void addMaxMemory(int delta);

    public interface ScheduleItem
        {
        public Interval getStartTime();
        public Duration getDuration();
        }

    public ScheduleItem getPreparationTime();
    public void setPreparationTime(final Interval starttime, final Duration duration);

    public ScheduleItem getExecutionTime();
    public void setExecutionTime(final Interval starttime, final Duration duration);

    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset);

    }
