/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker.podman;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface PodmanDockerContainerEntityRepository
    extends JpaRepository<PodmanDockerContainerEntity, UUID>
    {

    }
