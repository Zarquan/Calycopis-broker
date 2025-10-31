/**
 *
 */
package net.ivoa.calycopis.datamodel.executable;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface AbstractExecutableEntityRepository
    extends JpaRepository<AbstractExecutableEntity, UUID>
    {

    }
