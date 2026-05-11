package net.ivoa.calycopis.broker.engine.functional.processing.mock;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

public class MockReleaseAction extends MockDelayAction
    {

    public MockReleaseAction(final LifecycleComponentEntityImpl component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.RELEASING,
            IvoaLifecyclePhase.COMPLETED,
            delay
            );
        }
    }
