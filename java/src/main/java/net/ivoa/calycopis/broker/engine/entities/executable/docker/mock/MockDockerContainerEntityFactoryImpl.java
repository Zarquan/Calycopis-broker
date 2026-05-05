/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 *
 */
@Slf4j
@Component
public class MockDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements MockDockerContainerEntityFactory
    {
    
    private final MockDockerContainerEntityRepository repository;
    
    @Autowired
    public MockDockerContainerEntityFactoryImpl(
        final MockDockerContainerEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractExecutableEntityImpl> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public MockDockerContainerEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        ){
        MockDockerContainerEntityImpl entity = this.repository.save(
            new MockDockerContainerEntityImpl(
                session,
                result
                )
            );
        return entity ;
        }
    }
