/**
 * 
 */
package uk.co.metagrid.calycopis.executable;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import uk.co.metagrid.calycopis.factory.FactoryBase;

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
    public IvoaAbstractExecutable wrap(final String baseurl, final AbstractExecutableEntity entity);
    
    }
