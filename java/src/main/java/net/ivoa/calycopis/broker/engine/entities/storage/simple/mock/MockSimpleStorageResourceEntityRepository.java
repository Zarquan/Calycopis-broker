/*
 *
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.broker.engine.entities.storage.simple.mock;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for MockSimpleStorageResourceEntity.
 *
 */
public interface MockSimpleStorageResourceEntityRepository
extends LifecycleComponentEntityRepository<MockSimpleStorageResourceEntityImpl>
    {
    }
