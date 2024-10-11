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

    private JupyterNotebookFactory notebookfactory ;
    public JupyterNotebookFactory getNotebookFactory()
        {
        return this.notebookfactory;
        }

    private SimpleDataResourceFactory simpledatafactory ;
    public SimpleDataResourceFactory getSimpleDataFactory()
        {
        return this.simpledatafactory ;
        }

    private AmazonS3DataResourceFactory s3datafactory ;
    public AmazonS3DataResourceFactory getS3DataFactory()
        {
        return this.s3datafactory ;
        }

    @Autowired
    public NewProcessingContextFactoryImpl(
        final JupyterNotebookFactory notebookfactory,
        final SimpleDataResourceFactory simpledatafactory,
        final AmazonS3DataResourceFactory s3datafactory
        ){
        this.notebookfactory = notebookfactory;
        this.simpledatafactory = simpledatafactory;
        this.s3datafactory = s3datafactory;
        }
    
    public NewProcessingContextFactoryImpl()
        {
        super();
        }
    
    @Override
    public NewProcessingContext create()
        {
        return new NewProcessingContextImpl(this);
        }
    }
