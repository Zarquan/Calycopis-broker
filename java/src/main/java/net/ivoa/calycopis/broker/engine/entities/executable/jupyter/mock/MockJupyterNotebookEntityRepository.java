/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for PodmanJupyterNotebookEntity.
 *
 */
@Repository
public interface MockJupyterNotebookEntityRepository
    extends JpaRepository<MockJupyterNotebookEntityImpl, UUID>
    {

    }
