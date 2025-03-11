/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBase;

/**
 *
 */
public interface DockerContainerEntityFactory
    extends FactoryBase
    {
    public Optional<DockerContainerEntity> select(final UUID uuid);

    /**
     * Create a new DockerContainer entity with no parent.
     *
     */
    public DockerContainerEntity create(final String name);

    /**
     * Create and save a new DockerContainer entity.
     *
     */
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final String name);

    /**
     * Create a new DockerContainer entity.
     *
     */
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final String name, boolean save);

    /**
     * Create and save a new DockerContainer entity based on a template.
     *
     */
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final DockerContainerEntity template);

    }
