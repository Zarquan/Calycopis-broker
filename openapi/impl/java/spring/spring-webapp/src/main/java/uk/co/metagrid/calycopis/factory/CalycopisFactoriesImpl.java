/**
 * 
 */
package uk.co.metagrid.calycopis.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.data.amazon.AmazonS3DataResourceFactory;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceFactory;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookFactory;
import uk.co.metagrid.calycopis.execution.ExecutionFactory;
import uk.co.metagrid.calycopis.offers.OfferBlockFactory;

/**
 * Factory of factories, simplifying the configuration.
 *  
 */
@Slf4j
@Component
public class CalycopisFactoriesImpl extends FactoryBaseImpl implements CalycopisFactories
    {

    private final ExecutionFactory executionfactory ;
    @Override
    public ExecutionFactory getExecutionFactory()
        {
        return this.executionfactory;
        }

    private final OfferBlockFactory offerblockfactory ;
    @Override
    public OfferBlockFactory getOfferBlockFactory()
        {
        return this.offerblockfactory;
        }
    
    private final JupyterNotebookFactory jpnotebookfactory ;
    @Override
    public JupyterNotebookFactory getJupyterNotebookFactory()
        {
        return this.jpnotebookfactory;
        }

    private final SimpleDataResourceFactory simpledatafactory ;
    @Override
    public SimpleDataResourceFactory getSimpleDataFactory()
        {
        return this.simpledatafactory ;
        }

    private final AmazonS3DataResourceFactory amazondatafactory ;
    @Override
    public AmazonS3DataResourceFactory getAmazonDataFactory()
        {
        return this.amazondatafactory ;
        }

    @Autowired
    public CalycopisFactoriesImpl (
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

    }
