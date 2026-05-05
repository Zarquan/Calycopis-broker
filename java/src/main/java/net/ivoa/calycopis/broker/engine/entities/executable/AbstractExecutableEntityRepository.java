/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable;

import org.springframework.stereotype.Repository;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface AbstractExecutableEntityRepository
extends LifecycleComponentEntityRepository<AbstractExecutableEntityImpl>
    {

    }
