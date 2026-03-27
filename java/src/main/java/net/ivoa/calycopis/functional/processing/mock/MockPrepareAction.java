package net.ivoa.calycopis.functional.processing.mock;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

public class MockPrepareAction extends MockDelayAction
    {

    public MockPrepareAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.PREPARING,
            IvoaLifecyclePhase.AVAILABLE,
            delay
            );
        }
    }
