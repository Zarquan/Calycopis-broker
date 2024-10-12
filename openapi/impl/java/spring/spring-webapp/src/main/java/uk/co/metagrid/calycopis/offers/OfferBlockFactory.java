/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.util.List;

import uk.co.metagrid.calycopis.processing.NewProcessingContext;
import uk.co.metagrid.calycopis.util.FactoryBase;

/**
 * 
 */
public interface OfferBlockFactory
    extends FactoryBase
    {
    
    public List<OfferBlock> gererate(final NewProcessingContext context);
    
    }
