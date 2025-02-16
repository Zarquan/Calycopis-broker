/**
 * 
 */
package net.ivoa.calycopis.offerset;

import org.apache.logging.log4j.core.Filter.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.Validator.ResultEnum;
import net.ivoa.calycopis.validator.Validator.ResultSet;
import net.ivoa.calycopis.validator.Validator.ResultSetBean;
import net.ivoa.calycopis.validator.compute.ComputeResourceValidatorFactory;
import net.ivoa.calycopis.validator.data.DataResourceValidatorFactory;
import net.ivoa.calycopis.validator.storage.StorageResourceValidatorFactory;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {

    private final Validator<IvoaAbstractExecutable> executableValidators;
    private final Validator<IvoaAbstractStorageResource> storageValidators;
    private final Validator<IvoaAbstractDataResource> dataValidators;
    private final Validator<IvoaAbstractComputeResource> compValidators;

    @Autowired
    public OfferSetRequestParserImpl(
        final Validator<IvoaAbstractExecutable> executableValidators,
        final Validator<IvoaAbstractStorageResource> storageValidators, 
        final Validator<IvoaAbstractDataResource> dataValidators, 
        final Validator<IvoaAbstractComputeResource> compValidators 
        ){
        super();
        this.executableValidators = executableValidators ;
        this.storageValidators    = storageValidators ;
        this.dataValidators       = dataValidators ;
        this.compValidators       = compValidators ;
        }

    
    @Override
    public void process(final IvoaOfferSetRequest offersetRequest, final OfferSetEntity offersetEntity)
        {
        OfferSetRequestParserState state = new OfferSetRequestParserStateImpl(
            offersetRequest,
            offersetEntity
            );
        //
        // Validate the request.
        validate(
            state
            );
        //
        // Construct the offers.
        // ...
        
        }
    
    /**
     * Validate the request components.
     * 
     */
    public void validate(final OfferSetRequestParserState state)
        {
        final IvoaOfferSetRequest offersetRequest = state.getOriginalOfferSetRequest();
        final IvoaOfferSetRequest offersetResult  = state.getValidatedOfferSetRequest();
        ResultEnum fred = null ;
        //
        // Validate the requested executable.
        if (offersetRequest.getExecutable() != null)
            {
            ResultSet<IvoaAbstractExecutable> result = executableValidators.validate(
                offersetRequest.getExecutable(),
                state
                );
            offersetResult.setExecutable(
                result.getObject()
                );  
            fred = result.getEnum();
            }
        else {
            state.getOfferSetEntity().addWarning(
                    "urn:executable-required",
                    "Executable is required"
                    );
            state.valid(false);
            fred = ResultEnum.FAILED;
            }
        //
        // Validate the requested resources. 
        offersetResult.setResources(
            new IvoaExecutionResourceList()
            );
        if (offersetRequest.getResources() != null)
            {
            //
            // Validate the requested storage resources.
            if (offersetRequest.getResources().getStorage() != null)
                {
                for (IvoaAbstractStorageResource resource : offersetRequest.getResources().getStorage())
                    {
                    storageValidators.validate(
                        resource,
                        state
                        );            
                    }
                }
            //
            // Validate the requested data resources.
            if (offersetRequest.getResources().getData() != null)
                {
                for (IvoaAbstractDataResource resource : offersetRequest.getResources().getData())
                    {
                    dataValidators.validate(
                        resource,
                        state
                        );            
                    }
                }
            //
            // Validate the requested compute resources.
            if (offersetRequest.getResources().getCompute() != null)
                {
                for (IvoaAbstractComputeResource resource : offersetRequest.getResources().getCompute())
                    {
                    compValidators.validate(
                        resource,
                        state
                        );            
                    }
                }
            }
        //
        // If we haven't found a compute resource.
        if (offersetResult.getResources().getCompute().isEmpty())
            {
            // Add a default compute resource.
            }
        
        }
    }
