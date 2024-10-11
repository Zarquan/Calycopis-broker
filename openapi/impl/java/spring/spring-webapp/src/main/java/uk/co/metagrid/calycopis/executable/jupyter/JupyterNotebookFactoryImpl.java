/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.util.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Component
public class JupyterNotebookFactoryImpl
    extends FactoryBaseImpl
    implements JupyterNotebookFactory
    {
    private final JupyterNotebookRepository repository;

    @Autowired
    public JupyterNotebookFactoryImpl(final JupyterNotebookRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<JupyterNotebookEntity> select(final UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String notebookurl)
        {
        return this.create(
            parent,
            name,
            notebookurl,
            true
            );
        }

    @Override
    public JupyterNotebookEntity create(final ExecutionEntity parent, final String name, final String location, boolean save)
        {
        JupyterNotebookEntity created = new JupyterNotebookEntity(
            parent,
            name,
            location
            );
        log.debug("created [{}]", created.getUuid());
        if (save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }
    }
