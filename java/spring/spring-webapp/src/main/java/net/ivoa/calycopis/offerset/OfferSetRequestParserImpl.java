/**
 * 
 */
package net.ivoa.calycopis.offerset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceFactory;
import net.ivoa.calycopis.data.amazon.AmazonS3DataResourceFactory;
import net.ivoa.calycopis.data.simple.SimpleDataResourceFactory;
import net.ivoa.calycopis.execution.ExecutionSessionFactory;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlockFactory;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.validator.ExecutableValidator;
import net.ivoa.calycopis.validator.StorageValidator;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {
    
    private OfferBlockFactory            offerBlockFactory;
    private ExecutionSessionFactory      executionFactory;
    private SimpleComputeResourceFactory simpleComputeFactory;
    private SimpleDataResourceFactory    simpleDataFactory;
    private AmazonS3DataResourceFactory  amazonDataFactory;

    private final ExecutableValidator executableValidator ;
    private final StorageValidator storageValidator ;

    @Autowired
    public OfferSetRequestParserImpl(
        final ExecutableValidator executableValidator,
        final StorageValidator    storageValidator 
        ){
        super();
        this.executableValidator = executableValidator ;
        this.storageValidator = storageValidator ;
        }

    @Override
    public void process(final IvoaOfferSetRequest offersetRequest, final OfferSetEntity offersetEntity)
        {
        OfferSetRequestParserState state = new OfferSetRequestParserStateImpl(
            offersetRequest,
            offersetEntity
            ); 
        //
        // Validate the requested executable.
        executableValidator.validate(
            offersetRequest.getExecutable(),
            state
            );            
        //
        // Validate the requested resources. 
        if (offersetRequest.getResources() != null)
            {
            if (offersetRequest.getResources().getStorage() != null)
                {
                for (IvoaAbstractStorageResource storageResource : offersetRequest.getResources().getStorage())
                    {
                    storageValidator.validate(
                        storageResource,
                        state
                        );            
                    }
                }
            }
        }
    }
