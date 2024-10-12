/**
 * 
 */
package uk.co.metagrid.calycopis.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.data.amazon.AmazonS3DataResourceFactory;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceFactory;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookFactory;
import uk.co.metagrid.calycopis.execution.ExecutionFactory;
import uk.co.metagrid.calycopis.offers.OfferBlockFactory;
import uk.co.metagrid.calycopis.util.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Component
public class NewProcessingContextFactoryImpl
    extends FactoryBaseImpl
    implements NewProcessingContextFactory
    {
    private final ExecutionFactory executionfactory ;
    public ExecutionFactory getExecutionFactory()
        {
        return this.executionfactory;
        }

    private final OfferBlockFactory offerblockfactory ;
    public OfferBlockFactory getOfferBlockFactor()
        {
        return this.offerblockfactory;
        }
    
    private final JupyterNotebookFactory jpnotebookfactory ;
    public JupyterNotebookFactory getJupyterNotebookFactory()
        {
        return this.jpnotebookfactory;
        }

    private final SimpleDataResourceFactory simpledatafactory ;
    public SimpleDataResourceFactory getSimpleDataFactory()
        {
        return this.simpledatafactory ;
        }

    private final AmazonS3DataResourceFactory amazondatafactory ;
    public AmazonS3DataResourceFactory getAmazonDataFactory()
        {
        return this.amazondatafactory ;
        }

    @Autowired
    public NewProcessingContextFactoryImpl(
        final ExecutionFactory executionfactory,
        final OfferBlockFactory offerblockfactory, 
        final JupyterNotebookFactory jpnotebookfactory,
        final SimpleDataResourceFactory simpledatafactory,
        final AmazonS3DataResourceFactory amazondatafactory
        ){
        this.executionfactory  = executionfactory;
        this.offerblockfactory = offerblockfactory;
        this.jpnotebookfactory = jpnotebookfactory;
        this.simpledatafactory = simpledatafactory;
        this.amazondatafactory = amazondatafactory;
        }
    
    @Override
    public NewProcessingContext create()
        {
        return new NewProcessingContextImpl(this);
        }
    }
