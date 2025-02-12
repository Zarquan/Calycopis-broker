/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;

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
    public JupyterNotebookEntity create(final String name, final String notebookurl)
        {
        return this.create(
            null,
            name,
            notebookurl,
            true
            );
        }

    @Override
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final String name, final String notebookurl)
        {
        return this.create(
            parent,
            name,
            notebookurl,
            true
            );
        }

    @Override
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final String name, final String location, boolean save)
        {
        log.debug("create(ExecutionEntity, String, String, boolean) [{}][{}][{}][{}]",
            ((parent!= null) ? parent.getUuid() : "null-template"),
            name,
            location,
            save
            );
        JupyterNotebookEntity created = new JupyterNotebookEntity(
            parent,
            name,
            location
            );
        log.debug("created [{}]", created.getUuid());
        if ((parent != null) && save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }
    
    @Override
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final JupyterNotebookEntity template)
        {
        log.debug("create(ExecutionEntity, JupyterNotebookEntity) [{}]", (template != null) ? template.getUuid() : "null-template");
        return this.create(
            parent,
            template.getName(),
            template.getLocation(),
            true
            );
        }
    }
