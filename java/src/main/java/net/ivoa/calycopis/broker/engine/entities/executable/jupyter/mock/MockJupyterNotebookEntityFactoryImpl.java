/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.JupyterNotebookEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;

/**
 *
 */
@Slf4j
public class MockJupyterNotebookEntityFactoryImpl
extends JupyterNotebookEntityFactoryImpl
implements MockJupyterNotebookEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockJupyterNotebookEntityFactoryImpl(
        final AbstractExecutableEntityRepository repository
        ){
        super(repository);
        }

    @Override
    public MockJupyterNotebookEntityImpl create(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        ){
        MockJupyterNotebookEntityImpl entity = this.repository.save(
            new MockJupyterNotebookEntityImpl(
                session,
                result
                )
            );
        return entity;
        }
    }
