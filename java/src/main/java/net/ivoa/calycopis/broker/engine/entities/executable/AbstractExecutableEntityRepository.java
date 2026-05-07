/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
public interface AbstractExecutableEntityRepository
extends LifecycleComponentEntityRepository<AbstractExecutableEntityImpl>
    {

    }
