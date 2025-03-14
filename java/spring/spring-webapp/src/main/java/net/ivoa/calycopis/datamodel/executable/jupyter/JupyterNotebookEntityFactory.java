/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

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
    public Optional<JupyterNotebookEntity> select(final UUID uuid);

    /**
     * Create and save a new JupyterNotebookEntity.
     *
     */
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final IvoaJupyterNotebook template);

    }
