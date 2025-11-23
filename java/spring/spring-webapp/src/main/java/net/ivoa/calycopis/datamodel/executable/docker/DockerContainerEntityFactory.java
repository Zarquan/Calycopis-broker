/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.UUID;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;

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
     * Create a new DockerContainerEntity based on a validation result.
     *
     */
    public DockerContainerEntity create(
        final AbstractExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        );

    }
