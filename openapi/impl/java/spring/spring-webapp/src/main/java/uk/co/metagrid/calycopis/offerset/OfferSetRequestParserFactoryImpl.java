/**
 * 
 */
package uk.co.metagrid.calycopis.offerset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceFactory;
import uk.co.metagrid.calycopis.data.amazon.AmazonS3DataResourceFactory;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceFactory;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookFactory;
import uk.co.metagrid.calycopis.execution.ExecutionFactory;
import uk.co.metagrid.calycopis.factory.FactoryBaseImpl;
import uk.co.metagrid.calycopis.offers.OfferBlockFactory;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserFactoryImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParserFactory
    {

    private final OfferBlockFactory            offerBlockFactory;
    private final ExecutionFactory             executionFactory;
    private final SimpleComputeResourceFactory simpleComputeFactory;
    private final SimpleDataResourceFactory    simpleDataFactory;
    private final AmazonS3DataResourceFactory  amazonDataFactory;
    private final JupyterNotebookFactory       jupyterNotebookFactory;

    @Autowired
    public OfferSetRequestParserFactoryImpl(
        final OfferBlockFactory            offerBlockFactory,
        final ExecutionFactory             executionFactory,
        final SimpleComputeResourceFactory simpleComputeFactory,
        final SimpleDataResourceFactory    simpleDataFactory,
        final AmazonS3DataResourceFactory  amazonDataFactory,
        final JupyterNotebookFactory       jupyterNotebookFactory
        ){
        super();
        this.offerBlockFactory      = offerBlockFactory;
        this.executionFactory       = executionFactory;
        this.simpleComputeFactory   = simpleComputeFactory;
        this.simpleDataFactory      = simpleDataFactory;
        this.amazonDataFactory      = amazonDataFactory;
        this.jupyterNotebookFactory = jupyterNotebookFactory;
        }
    
    @Override
    public OfferSetRequestParser create()
        {
        return new OfferSetRequestParserImpl(
            this.offerBlockFactory,
            this.executionFactory,
            this.simpleComputeFactory,
            this.simpleDataFactory,
            this.amazonDataFactory,
            this.jupyterNotebookFactory
            );
        }
    }
