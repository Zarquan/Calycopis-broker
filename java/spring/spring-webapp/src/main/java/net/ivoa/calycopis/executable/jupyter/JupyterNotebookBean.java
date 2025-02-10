/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.executable.AbstractExecutable;
import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A serialization bean for JupyterNotebooks.
 * Ideally we would like to extend ComponentBean, but we need to extend  
 * IvoaJupyterNotebook to pick up the serialization annotations.
 * This doesn't work, the serialization mechanism skips the type URI
 * and replaces it with the Java class name.
 * 
 *   executable:
 *     type: "JupyterNotebookBean"
 * 
 */
@Slf4j
@Deprecated
public class JupyterNotebookBean
    extends IvoaJupyterNotebook
    {
    /**
     * The type discriminator for Jupyter notebooks.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "https://www.purl.org/ivoa.net/EB/schema/types/executables/jupyter-notebook-1.0.yaml" ;

    /**
     * The URL path for Jupyter notebooks.
     *
     */
    public static final String REQUEST_PATH = AbstractExecutable.REQUEST_PATH ;

    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The JupyterNotebookEntity to wrap.
     *
     */
    private final JupyterNotebookEntity entity;
    
    /**
     * Protected constructor.
     *
     */
    public JupyterNotebookBean(final String baseurl, final JupyterNotebookEntity entity)
        {
        super(TYPE_DISCRIMINATOR);
        this.baseurl = baseurl;
        this.entity  = entity;
        }

    @Override
    public UUID getUuid()
        {
        return entity.getUuid();
        }

    @Override
    public String getHref()
        {
        return this.baseurl + JupyterNotebook.REQUEST_PATH + entity.getUuid();
        }

    @Override
    public String getType()
        {
        log.debug("getType() [{}]", TYPE_DISCRIMINATOR);
        return TYPE_DISCRIMINATOR;
        }
    
    @Override
    public String getName()
        {
        return entity.getName();
        }
    
    @Override
    public OffsetDateTime getCreated()
        {
        return this.entity.getCreated();
        }
    
    @Override
    public List<@Valid IvoaMessageItem> getMessages()
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

    @Override
    public String getLocation()
        {
        return this.entity.getLocation();
        }
    }
