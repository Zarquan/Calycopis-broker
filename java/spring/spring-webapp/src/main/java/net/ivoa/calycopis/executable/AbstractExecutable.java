/**
 * 
 */
package net.ivoa.calycopis.executable;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.execution.ExecutionEntity;

/**
 * 
 */
public interface AbstractExecutable
    extends Component
    {
    /**
     * The database table name for AbstractExecutables.
     * 
     */
    public static final String TABLE_NAME = "executables" ;

    /**
     * The URL path for AbstractExecutables.
     *
     */
    public static final String REQUEST_PATH = "/executables/" ;

    /**
     * Get the parent ExecutionEntity.  
     *
     */
    public ExecutionEntity getParent();

    }
