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
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;

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
    public DockerContainerEntity create(final ExecutionSessionEntity parent, final IvoaDockerContainer template)
        {
        return this.repository.save(
            new DockerContainerEntity(
                parent,
                template
                )
            );
        }
    }
