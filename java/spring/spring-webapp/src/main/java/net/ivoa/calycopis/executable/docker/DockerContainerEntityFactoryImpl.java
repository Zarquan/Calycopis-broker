/**
 *
 */
package net.ivoa.calycopis.executable.docker;

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
public class DockerContainerEntityFactoryImpl
    extends FactoryBaseImpl
    implements DockerContainerEntityFactory
    {

    private final DockerContainerEntityRepository repository;

    @Autowired
    public DockerContainerEntityFactoryImpl(final DockerContainerEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<DockerContainerEntity> select(final UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public DockerContainerEntity create(final String name)
        {
        return this.create(
            null,
            name,
            true
            );
        }

    @Override
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final String name)
        {
        return this.create(
            parent,
            name,
            true
            );
        }

    @Override
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final String name, boolean save)
        {
        log.debug("create(ExecutionEntity, String, boolean) [{}][{}][{}][{}]",
            ((parent!= null) ? parent.getUuid() : "null-template"),
            name,
            save
            );
        DockerContainerEntity created = new DockerContainerEntity(
            parent,
            name
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
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final DockerContainerEntity template)
        {
        log.debug("create(ExecutionEntity, JupyterNotebookEntity) [{}]", (template != null) ? template.getUuid() : "null-template");
        return this.create(
            parent,
            template.getName(),
            true
            );
        }
    }
