/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.message.MessageItemBean;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * A serialization bean for DockerContainers.
 * Ideally we would like to extend ComponentBean, but we need to extend
 * IvoaDockerContainer to pick up the serialization annotations.
 * This doesn't work, the serialization mechanism skips the type URI
 * and replaces it with the Java class name.
 *
 *   executable:
 *     type: "DockerContainerBean"
 *
 */
@Slf4j
@Deprecated
public class DockerContainerBean
    extends IvoaDockerContainer
    {

    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The DockerContainerEntity to wrap.
     *
     */
    private final DockerContainerEntity entity;

    /**
     * Protected constructor.
     *
     */
    public DockerContainerBean(final String baseurl, final DockerContainerEntity entity)
        {
        super(DockerContainer.TYPE_DISCRIMINATOR);
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
        return this.baseurl + DockerContainer.REQUEST_PATH + entity.getUuid();
        }

    @Override
    public String getType()
        {
        log.debug("getType() [{}]", DockerContainer.TYPE_DISCRIMINATOR);
        return DockerContainer.TYPE_DISCRIMINATOR;
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
