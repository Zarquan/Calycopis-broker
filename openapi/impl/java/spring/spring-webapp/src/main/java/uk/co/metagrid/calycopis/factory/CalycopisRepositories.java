/**
 * 
 */
package uk.co.metagrid.calycopis.factory;

import uk.co.metagrid.calycopis.execution.ExecutionRepository;
import uk.co.metagrid.calycopis.offerset.OfferSetRepository;
import uk.co.metagrid.calycopis.storage.simple.SimpleStorageResourceRepository;

/**
 * 
 */
public interface CalycopisRepositories extends FactoryBase
    {

    public ExecutionRepository getExecutionRepository();

    public OfferSetRepository getOfferSetRepository();

    public SimpleStorageResourceRepository getSimpleStorageResourceRepository();

    }
