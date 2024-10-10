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

    protected JupyterNotebookEntity(final ExecutionEntity parent, final String name, final String notebookurl)
        {
        super(parent, name);
        this.notebookurl = notebookurl;
        }

    private String notebookurl;
    @Override
    public String getNotebookUrl()
        {
        return this.notebookurl;
        }

    }
