/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * 
 */
public interface JupyterNotebookEntityFactory
    extends FactoryBase
    {
    public Optional<JupyterNotebookEntity> select(final UUID uuid);

    /**
     * Create and save a new JupyterNotebook entity based on a template.
     *
     */
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final IvoaJupyterNotebook template);

    }
