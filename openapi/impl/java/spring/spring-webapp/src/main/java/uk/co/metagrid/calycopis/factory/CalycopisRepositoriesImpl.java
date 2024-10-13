/**
 * 
 */
package uk.co.metagrid.calycopis.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.calycopis.execution.ExecutionRepository;
import uk.co.metagrid.calycopis.offerset.OfferSetRepository;
import uk.co.metagrid.calycopis.storage.simple.SimpleStorageResourceRepository;

/**
 * 
 */
@Slf4j
@Component
public class CalycopisRepositoriesImpl
    extends FactoryBaseImpl
    implements CalycopisRepositories
    {

    private final ExecutionRepository executionrepository;
    @Override
    public ExecutionRepository getExecutionRepository()
        {
        return this.executionrepository;
        }

    private final OfferSetRepository offersetrepository;
    @Override
    public OfferSetRepository getOfferSetRepository()
        {
        return this.offersetrepository;
        }

    private final SimpleStorageResourceRepository simplestoragerepository;
    @Override
    public SimpleStorageResourceRepository getSimpleStorageResourceRepository()
        {
        return this.simplestoragerepository;
        }

    @Autowired
    public CalycopisRepositoriesImpl(
        final ExecutionRepository executionrepository,
        final OfferSetRepository offersetrepository,
        final SimpleStorageResourceRepository simplestoragerepository
        ){
        this.executionrepository = executionrepository;
        this.offersetrepository  = offersetrepository;
        this.simplestoragerepository = simplestoragerepository;
        }
    
    }
