/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;

/**
 *
 */
public interface DockerContainerEntityFactory
    extends FactoryBase
    {
    /**
     * Find a DockerContainerEntity based on UUID.
     * 
     */
    public Optional<DockerContainerEntity> select(final UUID uuid);

    /**
     * Create and save a new DockerContainerEntity based on a template.
     *
     */
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final IvoaDockerContainer template);

    }
