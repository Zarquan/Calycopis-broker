/**
 * 
 */
package net.ivoa.calycopis.executable;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;

/**
 * 
 */
@Component
public interface AbstractExecutableBeanFactory
    extends FactoryBase
    {

    /**
     * Wrap a AbstractExecutableEntity as an IvoaAbstractExecutable.
     *  
     */
    public IvoaAbstractExecutable wrap(final String baseurl, final AbstractExecutableEntity entity);
    
    }
