/**
 * 
 */
package net.ivoa.calycopis.offerset;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 * 
 */
public interface OfferSetRequestParser
    extends FactoryBase
    {
    /**
     * Process an OfferSetRequest to populate an OfferSetEntity.
     *
     */
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset);

    }
