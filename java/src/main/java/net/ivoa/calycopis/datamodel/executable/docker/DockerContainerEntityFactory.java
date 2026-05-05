/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityImpl;

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
