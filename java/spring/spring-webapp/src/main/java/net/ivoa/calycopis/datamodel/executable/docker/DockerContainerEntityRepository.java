/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface DockerContainerEntityRepository
    extends JpaRepository<DockerContainerEntity, UUID>
    {

    }
