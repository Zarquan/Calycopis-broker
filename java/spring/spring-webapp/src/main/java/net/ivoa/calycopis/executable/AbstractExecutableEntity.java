/**
 * 
 */
package net.ivoa.calycopis.executable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.component.ComponentEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;

/**
 * 
 */
@Entity
@Table(
    name = "executables"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class AbstractExecutableEntity
    extends ComponentEntity
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
    protected AbstractExecutableEntity(final ExecutionSessionEntity parent, final String name)
        {
        super(name);
        this.parent = parent;
        }
    
    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionSessionEntity parent;

    @Override
    public ExecutionSessionEntity getParent()
        {
        return this.parent;
        }

    }
