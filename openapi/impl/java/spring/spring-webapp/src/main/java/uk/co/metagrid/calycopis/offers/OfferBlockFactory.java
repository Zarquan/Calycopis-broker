/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.util.List;

import uk.co.metagrid.calycopis.factory.FactoryBase;
import uk.co.metagrid.calycopis.offerset.OfferSetRequestParser;

/**
 * 
 */
public interface OfferBlockFactory
    extends FactoryBase
    {
    
    public List<OfferBlock> generate(final OfferSetRequestParser context);
    
    }
