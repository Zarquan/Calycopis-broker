/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker.podman;

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
public class PodmanDockerContainerEntityFactoryImpl
    extends FactoryBaseImpl
    implements PodmanDockerContainerEntityFactory
    {

    private final PodmanDockerContainerEntityRepository repository;

    private final TestExecutionStepEntityFactory factory;
    
    @Autowired
    public PodmanDockerContainerEntityFactoryImpl(
        final PodmanDockerContainerEntityRepository repository,
        final TestExecutionStepEntityFactory factory        
        ){
        super();
        this.repository = repository;
        this.factory = factory;
        }

    @Override
    public PodmanDockerContainerEntity select(final UUID uuid)
        {
        Optional<PodmanDockerContainerEntity> optional = this.repository.findById(
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
    public PodmanDockerContainerEntity create(final ExecutionSessionEntity session, final IvoaDockerContainer template)
        {
        PodmanDockerContainerEntity result = this.repository.save(
            new PodmanDockerContainerEntity(
                session,
                template
                )
            );
        result.configure(factory);
        return result ;
        }
    }
