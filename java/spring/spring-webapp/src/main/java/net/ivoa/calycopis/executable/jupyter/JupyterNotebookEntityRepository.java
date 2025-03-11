/**
 * 
 */
package net.ivoa.calycopis.executable.jupyter;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 */
@Repository
public interface JupyterNotebookEntityRepository
    extends JpaRepository<JupyterNotebookEntity, UUID>
    {

    }
