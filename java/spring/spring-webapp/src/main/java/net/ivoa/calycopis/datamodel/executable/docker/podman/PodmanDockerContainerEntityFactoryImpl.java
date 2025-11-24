/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker.podman;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.planning.TestExecutionStepEntityFactory;

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
    public PodmanDockerContainerEntity create(
        final AbstractExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        PodmanDockerContainerEntity entity = this.repository.save(
            new PodmanDockerContainerEntity(
                session,
                result
                )
            );
        entity.configure(factory);
        entity.schedule();

        return entity ;
        }
    }
