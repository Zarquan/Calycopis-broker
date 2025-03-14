/**
 * 
 */
package net.ivoa.calycopis.datamodel.offerset;

import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 * 
 */
public interface OfferSetRequestParser
    extends FactoryBase
    {
    /**
     * Process an OfferSetRequest and populate an OfferSetEntity.
     *
     */
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset);

    }
