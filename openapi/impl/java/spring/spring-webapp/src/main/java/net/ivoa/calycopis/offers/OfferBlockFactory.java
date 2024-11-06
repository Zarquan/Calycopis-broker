/**
 * 
 */
package net.ivoa.calycopis.offers;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.factory.FactoryBase;

/**
 * 
 */
public interface OfferBlockFactory
    extends FactoryBase
    {
    
    public List<OfferBlock> generate(final Interval requeststart, final Duration requestduration, final Long requestcores, final Long requestmemory);

    }
