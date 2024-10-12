/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.time.Duration;

import org.threeten.extra.Interval;

/**
 * 
 */
public class OfferBolckImpl
    implements OfferBlock
    {

    public OfferBolckImpl(final Interval starttime, final Duration duration, long mincores, long minmemory)
        {
        this.starttime = starttime;
        this.duration  = duration;
        this.mincores  = mincores;
        this.minmemory = minmemory;
        }

    private long mincores;
    @Override
    public long getMinCores()
        {
        return this.mincores;
        }

    private long minmemory;
    @Override
    public long getMinMemory()
        {
        return this.minmemory;
        }

    private Interval starttime;
    @Override
    public Interval getStartTime()
        {
        return this.starttime;
        }

    private Duration duration;
    @Override
    public Duration getDuration()
        {
        return this.duration;
        }
    }
