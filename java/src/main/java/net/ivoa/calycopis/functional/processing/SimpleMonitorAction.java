package net.ivoa.calycopis.functional.processing;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

@Slf4j
public class SimpleMonitorAction extends SimpleDelayAction
    {

    int count ;

    public SimpleMonitorAction(final LifecycleComponentEntity component, int delay)
        {
        this(
            component,
            delay,
            4
            );
        }
    
    public SimpleMonitorAction(final LifecycleComponentEntity component, int delay, int count)
        {
        super(
            component,
            delay
            );
        this.count = count;
        }
    
    @Override
    public void preProcess(final LifecycleComponentEntity component)
        {
        log.debug(
            "Pre-processing [{}][{}] count [{}]",
            componentUuid,
            componentClass
            );
        super.preProcess(
            component
            );
        }

    @Override
    public void postProcess(final LifecycleComponentEntity component)
        {
        if (count-- <= 0)
            {
            component.setPhase(
                IvoaLifecyclePhase.RELEASING
                );
            }
        super.postProcess(
            component
            );
        }
    }
