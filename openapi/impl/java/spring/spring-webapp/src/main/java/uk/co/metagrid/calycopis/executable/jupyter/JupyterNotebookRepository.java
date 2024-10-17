/**
 * 
 */
package uk.co.metagrid.calycopis.executable.jupyter;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 */
@Repository
public interface JupyterNotebookRepository
    extends JpaRepository<JupyterNotebookEntity, UUID>
    {

    }
