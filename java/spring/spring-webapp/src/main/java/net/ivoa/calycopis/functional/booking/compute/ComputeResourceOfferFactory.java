/**
 * 
 */
package net.ivoa.calycopis.functional.booking.compute;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.functional.factory.FactoryBase;

/**
 * Public interface for a ComputeResourceOffer factory. 
 *  
 */
public interface ComputeResourceOfferFactory
    extends FactoryBase
    {
    /**
     * Generate a list of offers.
     *  
     */
    public List<ComputeResourceOffer> generate(final Interval requeststart, final Duration requestduration, final Long requestcores, final Long requestmemory);

    }
