/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookBean;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookEntity;
import uk.co.metagrid.calycopis.util.FactoryBaseImpl;

/**
 * 
 */
public class AbstractExecutableBeanFactoryImpl
    extends FactoryBaseImpl 
    implements AbstractExecutableBeanFactory
    {

    /**
     * Public constructor.
     * 
     */
    public AbstractExecutableBeanFactoryImpl()
        {
        super();
        }

    @Override
    public IvoaAbstractExecutable wrap(final String baseurl, final ComponentEntity entity)
        {
        switch(entity)
            {
            case JupyterNotebookEntity jupyter:
                return new JupyterNotebookBean(
                    baseurl,
                    jupyter
                    );
            
            default:
                return null;
            }
        }
    }
