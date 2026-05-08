/**
 * 
 */
package net.ivoa.calycopis.broker.engine.entities.executable;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityImpl;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractExecutable;

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
public abstract class AbstractExecutableEntityImpl
extends LifecycleComponentEntityImpl
implements AbstractExecutable
    {
    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected AbstractExecutableEntityImpl()
        {
        super();
        }
    
    /**
     * Protected constructor used by derived classes.
     * 
     */
    protected AbstractExecutableEntityImpl(
        final SimpleExecutionSessionEntityImpl session,
        final AbstractExecutableValidator.Result result
        ){
        super(
            result.getMeta()
            );

        this.session = session;
        this.session.setExecutable(
            this
            );

        //
        // Start preparing when the session starts preparing.
        this.prepareDurationSeconds     = result.getPrepareDuration();
        this.prepareStartInstantSeconds = session.getPrepareStartInstantSeconds();

        //
        // Available as soon as the preparation is done.
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;
        this.availableDurationSeconds      = 0L;

        //
        // Hard coded 1s release duration.
        // Start releasing as soon as session availability ends.
        this.releaseDurationSeconds     = result.getReleaseDuration(); ; 
        this.releaseStartInstantSeconds = session.getReleaseStartInstantSeconds();         

        }
    
    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntityImpl session;
    @Override
    public SimpleExecutionSessionEntityImpl getSession()
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

    }
