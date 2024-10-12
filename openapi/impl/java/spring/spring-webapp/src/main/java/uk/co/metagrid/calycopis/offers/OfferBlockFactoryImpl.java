/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.ambleck.model.FactoryBase;
import uk.co.metagrid.calycopis.processing.NewProcessingContext;

/**
 * 
 */
@Slf4j
@Component
public class OfferBlockFactoryImpl
    extends FactoryBase
    implements OfferBlockFactory
    {

    @Override
    public List<OfferBlock> gererate(final NewProcessingContext context)
        {
        List<OfferBlock> blocks = new ArrayList<OfferBlock>();

        for (Interval requestedint : context.getStartIntervals())
            {
            blocks.add(
                new OfferBolckImpl(
                    requestedint,
                    context.getDuration(),
                    context.getMinCores(),
                    context.getMinMemory()
                    ) 
                );
            }
        return blocks;
        }
    }
