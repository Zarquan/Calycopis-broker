/**
 * 
 */
package net.ivoa.calycopis.offerset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceFactory;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceFactory;
import net.ivoa.calycopis.data.simple.SimpleDataResourceFactory;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookFactory;
import net.ivoa.calycopis.execution.ExecutionSessionFactory;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlockFactory;

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
    private final ExecutionSessionFactory             executionFactory;
    private final SimpleComputeResourceFactory simpleComputeFactory;
    private final SimpleDataResourceFactory    simpleDataFactory;
    private final AmazonS3DataResourceFactory  amazonDataFactory;
    private final JupyterNotebookFactory       jupyterNotebookFactory;

    @Autowired
    public OfferSetRequestParserFactoryImpl(
        final OfferBlockFactory            offerBlockFactory,
        final ExecutionSessionFactory             executionFactory,
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
