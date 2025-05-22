/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.UUID;

import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;
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
    public DockerContainerEntity select(final UUID uuid);

    /**
     * Create a new DockerContainerEntity based on a template.
     *
     */
    public DockerContainerEntity create(final ExecutionSessionEntity session, final IvoaDockerContainer template);

    }
