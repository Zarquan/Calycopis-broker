/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.execution.TestExecutionStepEntityFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
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

    private final TestExecutionStepEntityFactory factory;
    
    @Autowired
    public DockerContainerEntityFactoryImpl(
        final DockerContainerEntityRepository repository,
        final TestExecutionStepEntityFactory factory        
        ){
        super();
        this.repository = repository;
        this.factory = factory;
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
        DockerContainerEntity result = this.repository.save(
            new DockerContainerEntity(
                parent,
                template
                )
            );
        result.configure(factory);
        return result ;
        }
    }
