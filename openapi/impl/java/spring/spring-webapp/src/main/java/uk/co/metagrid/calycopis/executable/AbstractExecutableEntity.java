/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;

/**
 * 
 */
public class AbstractExecutableEntity
    extends ComponentEntity
    implements AbstractExecutable
    {

    protected AbstractExecutableEntity()
        {
        super();
        }
    
    protected AbstractExecutableEntity(final ExecutionEntity parent, final String name)
        {
        super(name);
        this.parent = parent;
        }
    
    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionEntity parent;

    @Override
    public ExecutionEntity getParent()
        {
        return this.parent;
        }

    }
