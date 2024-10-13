/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.util.ListWrapper;

/**
 * A serialization bean for JupyterNotebooks.
 * Ideally we would like to extend ComponentBean, but we need to extend  
 * IvoaJupyterNotebook to pick up the serialization annotations.
 * 
 */
@Slf4j
public class JupyterNotebookBean
    extends IvoaJupyterNotebook
    {

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
        super(JupyterNotebook.TYPE_DISCRIMINATOR);
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
        log.debug("getType() [{}]", JupyterNotebook.TYPE_DISCRIMINATOR);
        return JupyterNotebook.TYPE_DISCRIMINATOR;
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
    public String getNotebook()
        {
        return this.entity.getNotebook();
        }
    }
