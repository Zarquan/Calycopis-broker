/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractexecutables"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractExecutableEntity
extends LifecycleComponentEntity
    implements AbstractExecutable
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractExecutableEntity()
        {
        super();
        }
    
    /**
     * Protected constructor.
     * 
     */
    protected AbstractExecutableEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );

        this.session = session;
        this.session.setExecutable(
            this
            );

        //
        // Start preparing when the session starts preparing.
        this.prepareDurationSeconds     = result.getPreparationTime();
        this.prepareStartInstantSeconds = session.getPrepareStartInstantSeconds();

        //
        // Available as soon as the preparation is done.
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;
        this.availableDurationSeconds      = 0L;

        //
        // Hard coded 1s release duration.
        // Start releasing as soon as session availability ends.
        this.releaseDurationSeconds = 1L ; 
        this.releaseStartInstantSeconds = session.getReleaseStartInstantSeconds();         

        }
    
    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    public abstract IvoaAbstractExecutable makeBean(final URIBuilder builder);

    protected IvoaAbstractExecutable fillBean(final IvoaAbstractExecutable bean)
        {
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            this.getPhase()
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractExecutable.WEBAPP_PATH;
        }

    // TODO Move this to a test specific class.
    @Column(name="preparecounter")
    private int preparecounter;
    public int getPrepareCounter()
        {
        return this.preparecounter;
        }

    // Generic prepare action - move to the real Entities later.
    @Override
    public ProcessingAction getPrepareAction(final ComponentProcessingRequest request)
        {
        return new ProcessingAction()
            {

            int count = AbstractExecutableEntity.this.preparecounter ;
            
            @Override
            public boolean process()
                {
                log.debug(
                    "** Preparing [{}][{}] count [{}]",
                    AbstractExecutableEntity.this.getUuid(),
                    AbstractExecutableEntity.this.getClass().getSimpleName(),
                    count
                    );
    
                count++;
                try {
                    Thread.sleep(1000);
                    }
                catch (InterruptedException e)
                    {
                    log.error(
                        "Interrupted while preparing [{}][{}]",
                        AbstractExecutableEntity.this.getUuid(),
                        AbstractExecutableEntity.this.getClass().getSimpleName()
                        );
                    }
    
                return true ;
                }
    
            @Override
            public UUID getRequestUuid()
                {
                return request.getUuid();
                }
    
            @Override
            public IvoaLifecyclePhase getNextPhase()
                {
                if (count < 4)
                    {
                    return IvoaLifecyclePhase.PREPARING ;
                    }
                else {
                    return IvoaLifecyclePhase.AVAILABLE ;
                    }
                }
            
            @Override
            public boolean postProcess(final LifecycleComponentEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof AbstractExecutableEntity)
                    {
                    return postProcess(
                        (AbstractExecutableEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected component type [{}] post processing [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    return false ;
                    }
                }
                
            public boolean postProcess(final AbstractExecutableEntity component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                component.preparecounter = this.count ;
                return true ;
                }
            };
        }
    }
