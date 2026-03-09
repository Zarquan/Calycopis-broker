package net.ivoa.calycopis.functional.processing;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

public class SimpleReleaseAction extends SimpleDelayAction
    {

    public SimpleReleaseAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.RELEASING,
            IvoaLifecyclePhase.COMPLETED,
            delay
            );
        }
    }
