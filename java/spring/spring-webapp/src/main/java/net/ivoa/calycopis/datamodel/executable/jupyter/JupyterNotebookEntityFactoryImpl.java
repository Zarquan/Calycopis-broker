/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
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
    public JupyterNotebookEntity create(final ExecutionSessionEntity session, final IvoaJupyterNotebook template)
        {
        return this.repository.save(
            new JupyterNotebookEntity(
                session,
                template
                )
            );
        }
    }
