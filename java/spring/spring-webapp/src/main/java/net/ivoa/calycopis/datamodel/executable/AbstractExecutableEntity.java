/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaComponentMetadata;

/**
 * 
 */
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
        final SessionEntity session,
        final AbstractExecutableValidator.Result result,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );

        this.session = session;
        session.setExecutable(
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
    private SessionEntity session;

    @Override
    public SessionEntity getSession()
        {
        return this.session;
        }

    public IvoaAbstractExecutable makeBean()
        {
        return this.makeBean(null);
        }

    public abstract IvoaAbstractExecutable makeBean(final String baseurl);

    protected IvoaAbstractExecutable fillBean(final IvoaAbstractExecutable bean)
        {
        bean.setPhase(
            this.getPhase()
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }
    }
