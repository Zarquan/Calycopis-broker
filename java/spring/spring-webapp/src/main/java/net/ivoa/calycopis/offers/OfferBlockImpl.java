/**
 * 
 */
package net.ivoa.calycopis.offers;

import java.time.Duration;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

/**
 * Data object to hold the resources for an offer.
 * 
 */
@Slf4j
public class OfferBlockImpl
    implements OfferBlock
    {

    public OfferBlockImpl(final Instant starttime, final Duration duration, final Long cpucores, final Long cpumemory)
        {
        log.debug("OfferBlockImpl(...)");
        log.debug("values [{}][{}][{}][{}]", starttime, duration, cpucores, cpumemory);
        this.starttime = starttime;
        this.duration  = duration;
        this.cpucores  = cpucores;
        this.cpumemory = cpumemory;
        }

    private final Long cpucores;
    @Override
    public Long getCores()
        {
        return this.cpucores;
        }

    private final Long cpumemory;
    @Override
    public Long getMemory()
        {
        return this.cpumemory;
        }

    private final Instant starttime;
    @Override
    public Instant getStartTime()
        {
        return this.starttime;
        }

    private final Duration duration;
    @Override
    public Duration getDuration()
        {
        return this.duration;
        }
    }
