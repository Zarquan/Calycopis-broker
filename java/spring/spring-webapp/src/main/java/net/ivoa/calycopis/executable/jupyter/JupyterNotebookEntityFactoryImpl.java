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
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;

/**
 * 
 */
@Slf4j
@Component
public class JupyterNotebookEntityFactoryImpl
    extends FactoryBaseImpl
    implements JupyterNotebookEntityFactory
    {
    
    private final JupyterNotebookEntityRepository repository;

    @Autowired
    public JupyterNotebookEntityFactoryImpl(final JupyterNotebookEntityRepository repository)
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
    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final IvoaJupyterNotebook template)
        {
        log.debug("create(ExecutionEntity, JupyterNotebookEntity) [{}][{}]", parent, template);
        return this.create(
            parent,
            template.getName(),
            template.getLocation(),
            true
            );
        }

    public JupyterNotebookEntity create(final ExecutionSessionEntity parent, final String name, final String location, boolean save)
        {
        log.debug("create(ExecutionEntity, String, String, boolean) [{}][{}][{}][{}]",
            parent,
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
    }
