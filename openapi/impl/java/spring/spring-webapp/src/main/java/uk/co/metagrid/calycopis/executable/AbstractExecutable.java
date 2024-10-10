/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import uk.co.metagrid.calycopis.component.Component;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;

/**
 * 
 */
public interface AbstractExecutable
    extends Component
    {

    public ExecutionEntity getParent();

    }
