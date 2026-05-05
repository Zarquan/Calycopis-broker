package net.ivoa.calycopis.functional.processing.mock;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityImpl;
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
