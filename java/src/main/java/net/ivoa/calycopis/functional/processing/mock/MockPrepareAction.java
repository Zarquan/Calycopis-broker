package net.ivoa.calycopis.functional.processing.mock;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

public class MockPrepareAction extends MockDelayAction
    {

    public MockPrepareAction(final LifecycleComponentEntityImpl component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.PREPARING,
            IvoaLifecyclePhase.AVAILABLE,
            delay
            );
        }
    }
