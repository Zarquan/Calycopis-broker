/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.jupyter.podman;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Slf4j
@Component
public class PodmanJupyterNotebookEntityFactoryImpl
    extends FactoryBaseImpl
    implements PodmanJupyterNotebookEntityFactory
    {

    private final PodmanJupyterNotebookEntityRepository repository;

    @Autowired
    public PodmanJupyterNotebookEntityFactoryImpl(
        final PodmanJupyterNotebookEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public PodmanJupyterNotebookEntity select(final UUID uuid)
        {
        Optional<PodmanJupyterNotebookEntity> optional = this.repository.findById(
            uuid
            );
        if (optional.isPresent())
            {
            return optional.get();
            }
        else {
            return null;
            }
        }

    @Override
    public PodmanJupyterNotebookEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        PodmanJupyterNotebookEntity entity = this.repository.save(
            new PodmanJupyterNotebookEntity(
                session,
                result
                )
            );
        //entity.configure(factory);
        return entity;
        }
    }
