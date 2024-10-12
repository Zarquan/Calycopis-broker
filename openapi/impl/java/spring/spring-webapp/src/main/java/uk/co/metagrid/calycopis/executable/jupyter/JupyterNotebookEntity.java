/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.co.metagrid.calycopis.executable.AbstractExecutableEntity;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;

/**
 * 
 */
@Entity
@Table(
    name = "jpnotebooks"
    )
@DiscriminatorValue(
    value="urn:jupyter-notebook"
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
