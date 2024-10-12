/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import uk.co.metagrid.calycopis.component.ComponentEntity;
import uk.co.metagrid.calycopis.util.FactoryBase;

/**
 * 
 */
@Component
public interface AbstractExecutableBeanFactory
    extends FactoryBase
    {

    /**
     * Wrap a ComponentEntity in an IvoaAbstractComponent.
     *  
     */
    public IvoaAbstractExecutable wrap(final String baseurl, final ComponentEntity entity);
    
    }
