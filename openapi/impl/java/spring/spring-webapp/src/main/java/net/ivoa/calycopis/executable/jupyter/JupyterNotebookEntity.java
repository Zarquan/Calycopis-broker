/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.execution.ExecutionEntity;

/**
 * 
 */
@Entity
@Table(
    name = JupyterNotebook.TABLE_NAME
    )
@DiscriminatorValue(
    value = JupyterNotebook.TYPE_DISCRIMINATOR
    )
public class JupyterNotebookEntity
    extends AbstractExecutableEntity
    implements JupyterNotebook
    {

    protected JupyterNotebookEntity()
        {
        super();
        }

    protected JupyterNotebookEntity(final ExecutionEntity parent, final String name, final String location)
        {
        super(parent, name);
        this.location = location;
        }

    private String location;
    @Override
    public String getNotebook()
        {
        return this.location;
        }

    }
