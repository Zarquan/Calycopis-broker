/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.time.Duration;

import org.threeten.extra.Interval;

/**
 * 
 */
public interface OfferBlock
    {
    public long getMinCores();
    public long getMinMemory();
    public Interval getStartTime();
    public Duration getDuration();
    }
