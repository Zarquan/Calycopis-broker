/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityImpl;

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
