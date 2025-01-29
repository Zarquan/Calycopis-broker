/**
 * 
 */
package net.ivoa.calycopis.executable;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebook;
import net.ivoa.calycopis.executable.jupyter.JupyterNotebookEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebookStub;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.util.ListWrapper;

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
                    /*
                     * This returns the class name rather than the type field.
                     * executable:
                     *   type: "JupyterNotebookBean"
                     * 
                    IvoaAbstractExecutable bean = (IvoaJupyterNotebook) new JupyterNotebookBean(
                        baseurl,
                        jupyter
                        );
                     *
                     * This doesn't work either.
                     * executable:
                     *   type: "AbstractExecutableBeanFactoryImpl$1"
                     *   
                    IvoaJupyterNotebook bean = new IvoaJupyterNotebook(
                        JupyterNotebook.TYPE_DISCRIMINATOR
                        ){
                        @Override
                        public UUID getUuid()
                            {
                            return entity.getUuid();
                            }

                        @Override
                        public String getHref()
                            {
                            return baseurl + JupyterNotebook.REQUEST_PATH + entity.getUuid();
                            }

                        @Override
                        public String getType()
                            {
                            log.debug("getType() [{}]", JupyterNotebook.TYPE_DISCRIMINATOR);
                            return JupyterNotebook.TYPE_DISCRIMINATOR;
                            }

                        @Override
                        public String getNotebook()
                            {
                            return jupyter.getNotebook();
                            }
                        };
                     */
                    /*
                     * This works, but the message list needs to be wrapped.
                     * executable:
                     *   type: "urn:jupyter-notebook-0.1"
                     * 
                     */
                    IvoaJupyterNotebookStub bean = new IvoaJupyterNotebookStub(
                        JupyterNotebook.TYPE_DISCRIMINATOR
                        );
                    bean.uuid(entity.getUuid());
                    bean.href(baseurl + JupyterNotebook.REQUEST_PATH + entity.getUuid());
                    bean.messages(
                        listwrap(
                            entity,
                            bean
                            )
                        );
                    bean.location(jupyter.getNotebook());
                    
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

    /**
     * Wrap a List of JPA MessageEntity(s) as a List of IvoaMessageItems.
     * 
     */
    protected List<IvoaMessageItem> listwrap(final AbstractExecutableEntity entity, final IvoaAbstractExecutable bean)
        {
        return new ListWrapper<IvoaMessageItem, MessageEntity>(
            entity.getMessages()
            ){
            public IvoaMessageItem wrap(final MessageEntity inner)
                {
                return new MessageItemBean(
                    inner
                    );
                }
            };
        }
    }
