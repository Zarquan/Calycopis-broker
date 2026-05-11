/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 *
 */
@Slf4j
public class MockDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements MockDockerContainerEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockDockerContainerEntityFactoryImpl(
        final AbstractExecutableEntityRepository repository
        ){
        super(repository);
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
