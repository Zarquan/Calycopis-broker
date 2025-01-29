/**
 *
 */
package net.ivoa.calycopis.executable.docker;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface DockerContainerRepository
    extends JpaRepository<DockerContainerEntity, UUID>
    {

    }
