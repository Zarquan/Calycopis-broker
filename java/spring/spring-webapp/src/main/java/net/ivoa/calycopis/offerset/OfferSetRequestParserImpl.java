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
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {

    private final OfferBlockFactory            offerBlockFactory;
    private final ExecutionSessionFactory      executionFactory;
    private final SimpleComputeResourceFactory simpleComputeFactory;
    private final SimpleDataResourceFactory    simpleDataFactory;
    private final AmazonS3DataResourceFactory  amazonDataFactory;
    private final JupyterNotebookFactory       jupyterNotebookFactory;

    @Autowired
    public OfferSetRequestParserImpl(
        final OfferBlockFactory            offerBlockFactory,
        final ExecutionSessionFactory      executionFactory,
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
    
    /**
     * Process an OfferSetRequest to populate an OfferSetEntity.
     *
     */
    @Override
    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset)
        {

        
        
        
        }
    
    }
