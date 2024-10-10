/**
 * 
 */
package uk.co.metagrid.calycopis.jpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * 
 */
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID>
    extends Repository<T, ID>
    {

    }
