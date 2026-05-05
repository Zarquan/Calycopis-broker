/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker;

import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 *
 */
public interface DockerContainerEntityFactory
extends AbstractExecutableEntityFactory
    {

    /**
     * Create a new DockerContainerEntity based on a validator result.
     *
     */
    public DockerContainerEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        );

    }
