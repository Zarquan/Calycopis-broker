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
import net.ivoa.calycopis.validator.ExecutableValidatorFactory ;
import net.ivoa.calycopis.validator.StorageValidatorFactory ;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {

    private final ExecutableValidatorFactory executableValidators;
    private final StorageValidatorFactory    storageValidators;

    @Autowired
    public OfferSetRequestParserImpl(
        final ExecutableValidatorFactory executableValidators,
        final StorageValidatorFactory    storageValidators 
        ){
        super();
        this.executableValidators = executableValidators ;
        this.storageValidators    = storageValidators ;
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
        executableValidators.validate(
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
                    storageValidators.validate(
                        storageResource,
                        state
                        );            
                    }
                }
            }
        }
    }
