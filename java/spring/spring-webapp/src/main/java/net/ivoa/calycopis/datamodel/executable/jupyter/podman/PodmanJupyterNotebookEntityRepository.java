/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.jupyter.podman;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for PodmanJupyterNotebookEntity.
 *
 */
@Repository
public interface PodmanJupyterNotebookEntityRepository
    extends JpaRepository<PodmanJupyterNotebookEntity, UUID>
    {

    }
