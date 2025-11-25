/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import java.util.UUID;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;

/**
 * 
 */
public interface JupyterNotebookEntityFactory
    extends FactoryBase
    {
    /**
     * Select a JupyterNotebookEntity based on UUID.
     * 
     */
    public JupyterNotebookEntity select(final UUID uuid);

    /**
     * Create and save a new JupyterNotebookEntity.
     *
     */
    public JupyterNotebookEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        );

    }
