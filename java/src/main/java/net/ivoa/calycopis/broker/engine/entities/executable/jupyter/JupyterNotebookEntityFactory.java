/**
 * 
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter;

import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 * 
 */
public interface JupyterNotebookEntityFactory
extends AbstractExecutableEntityFactory
    {

    /**
     * Create and save a new JupyterNotebookEntity.
     *
     */
    public JupyterNotebookEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        );

    }
