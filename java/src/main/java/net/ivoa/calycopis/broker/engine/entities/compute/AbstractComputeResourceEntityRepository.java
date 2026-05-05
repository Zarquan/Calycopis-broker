/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.compute;

import org.springframework.stereotype.Repository;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface AbstractComputeResourceEntityRepository
extends LifecycleComponentEntityRepository<AbstractComputeResourceEntityImpl>
    {

    }
