/**
 * 
 */
package uk.co.metagrid.calycopis.factory;

import uk.co.metagrid.calycopis.data.amazon.AmazonS3DataResourceFactory;
import uk.co.metagrid.calycopis.data.simple.SimpleDataResourceFactory;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookFactory;
import uk.co.metagrid.calycopis.execution.ExecutionFactory;
import uk.co.metagrid.calycopis.offers.OfferBlockFactory;

/**
 * 
 */
public interface CalycopisFactories
    extends FactoryBase
    {

    public ExecutionFactory getExecutionFactory();

    public OfferBlockFactory getOfferBlockFactory();
    
    public JupyterNotebookFactory getJupyterNotebookFactory();

    public SimpleDataResourceFactory getSimpleDataFactory();

    public AmazonS3DataResourceFactory getAmazonDataFactory();
    
    }
