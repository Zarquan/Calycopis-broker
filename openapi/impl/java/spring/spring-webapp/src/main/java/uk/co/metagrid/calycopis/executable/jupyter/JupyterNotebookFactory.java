/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.util.FactoryBase;

/**
 * 
 */
public interface JupyterNotebookFactory
    extends FactoryBase
    {
    public Optional<JupyterNotebookEntity> select(final UUID uuid);
    
    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String notebookurl);

    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String notebookurl, boolean save);

    }
