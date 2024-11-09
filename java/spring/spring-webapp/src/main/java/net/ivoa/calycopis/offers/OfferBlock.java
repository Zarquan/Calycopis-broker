/**
 * 
 */
package net.ivoa.calycopis.offers;

import java.time.Duration;
import java.time.Instant;

/**
 * 
 */
public interface OfferBlock
    {
    
    public Long getCores();
    public Long getMemory();
    public Instant getStartTime();
    public Duration getDuration();
    }
