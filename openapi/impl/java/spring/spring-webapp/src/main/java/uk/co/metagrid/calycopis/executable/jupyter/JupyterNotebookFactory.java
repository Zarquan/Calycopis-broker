/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.factory.FactoryBase;

/**
 * 
 */
public interface JupyterNotebookFactory
    extends FactoryBase
    {
    public Optional<JupyterNotebookEntity> select(final UUID uuid);

    /**
     * Create a new JupyterNotebook entity with no parent.
     *
     */
    public JupyterNotebookEntity create(final String name, final String notebookurl);

    /**
     * Create and save a new JupyterNotebook entity.
     *
     */
    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String notebookurl);

    /**
     * Create a new JupyterNotebook entity.
     *
     */
    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String notebookurl, boolean save);

    /**
     * Create and save a new JupyterNotebook entity based on a template.
     *
     */
    public JupyterNotebookEntity create(final ExecutionEntity parent, final JupyterNotebookEntity template);

    }
