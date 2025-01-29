/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.execution.ExecutionEntity;

/**
 *
 */
@Entity
@Table(
    name = DockerContainer.TABLE_NAME
    )
@DiscriminatorValue(
    value = DockerContainer.TYPE_DISCRIMINATOR
    )
public class DockerContainerEntity
    extends AbstractExecutableEntity
    implements DockerContainer
    {

    protected DockerContainerEntity()
        {
        super();
        }

    protected DockerContainerEntity(final ExecutionEntity parent, final String name)
        {
        super(parent, name);
        }
    }
