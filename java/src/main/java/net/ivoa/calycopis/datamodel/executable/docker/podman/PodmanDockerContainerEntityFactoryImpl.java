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
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

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
    
    @Autowired
    public PodmanDockerContainerEntityFactoryImpl(
        final PodmanDockerContainerEntityRepository repository
        ){
        super();
        this.repository = repository;
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
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        PodmanDockerContainerEntity entity = this.repository.save(
            new PodmanDockerContainerEntity(
                session,
                result
                )
            );

        return entity ;
        }
    }
