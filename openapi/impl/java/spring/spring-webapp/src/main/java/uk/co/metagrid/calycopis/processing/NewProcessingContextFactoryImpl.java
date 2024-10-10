/**
 * 
 */
package uk.co.metagrid.calycopis.processing;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.util.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Component
public class NewProcessingContextFactoryImpl
    extends FactoryBaseImpl
    implements NewProcessingContextFactory
    {

    public NewProcessingContextFactoryImpl()
        {
        super();
        }
    
    @Override
    public NewProcessingContext create()
        {
        return new NewProcessingContextImpl();
        }
    }
