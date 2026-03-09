package net.ivoa.calycopis.functional.processing;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

public class SimplePrepareAction extends SimpleDelayAction
    {

    public SimplePrepareAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.PREPARING,
            IvoaLifecyclePhase.AVAILABLE,
            delay
            );
        }
    }
