/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import uk.co.metagrid.calycopis.component.Component;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.util.ListWrapper;

/**
 * Unfortunately, our Beans can't extend this class because they need to extend
 * the generated Ivoa bean class to get the serialization annotations.
 * 
 */
public class AbstractExecutableBean
    extends IvoaAbstractExecutable
    {

    public String getRequestPath()
        {
        return Component.REQUEST_PATH ;
        }
    
    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The ComponentEntity to wrap.
     *
     */
    private final ComponentEntity entity;
    
    /**
     * Protected constructor.
     *
     */
    public AbstractExecutableBean(final String baseurl, final ComponentEntity entity)
        {
        super();
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
        return this.baseurl + this.getRequestPath() + entity.getUuid();
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
    }
