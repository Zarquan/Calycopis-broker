/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookBean;
import uk.co.metagrid.calycopis.executable.jupyter.JupyterNotebookEntity;
import uk.co.metagrid.calycopis.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
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
    public IvoaAbstractExecutable wrap(final String baseurl, final AbstractExecutableEntity entity)
        {
        log.debug("wrap(String, AbstractExecutableEntity)");
        log.debug("Entity [{}][{}]",
            ((entity != null) ? (entity.getUuid()) : "null-entity"),
            ((entity != null) ? (entity.getClass().getName()) : "null-entity")
            );
        if (entity != null)
            {
            switch(entity)
                {
                case JupyterNotebookEntity jupyter:
                    IvoaAbstractExecutable bean = new JupyterNotebookBean(
                        baseurl,
                        jupyter
                        );
                    log.debug("Bean [{}][{}][{}]",
                            bean.getUuid(),
                            bean.getType(),
                            bean.getClass().getName()
                            );
                    return bean ;
                default:
                    throw new RuntimeException("Unexpected Executable type []");
                }
            }
        else {
            return null ;
            }
        }
    }
