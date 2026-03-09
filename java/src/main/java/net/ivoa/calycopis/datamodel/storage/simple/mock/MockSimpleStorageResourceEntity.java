/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
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

package net.ivoa.calycopis.datamodel.storage.simple.mock;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.SimpleDelayAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "mocksimplestorageresources"
    )
@DiscriminatorValue(
    value="uri:mock-simple-storage-resource"
    )
public class MockSimpleStorageResourceEntity
    extends SimpleStorageResourceEntity
    implements MockSimpleStorageResource
    {

    /**
     * 
     */
    public MockSimpleStorageResourceEntity()
        {
        super();
        }

    /**
     *
     */
    public MockSimpleStorageResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        return new SimpleDelayAction(
            this,
            IvoaLifecyclePhase.PREPARING,
            IvoaLifecyclePhase.AVAILABLE,
            30_000
            );
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        return new SimpleDelayAction(
            this,
            30_000
            );
        }
    
    @Override
    public ProcessingAction getReleaseAction(final Platform platform, final ComponentProcessingRequest request)
        {
        return new SimpleDelayAction(
            this,
            IvoaLifecyclePhase.RELEASING,
            IvoaLifecyclePhase.COMPLETED,
            30_000
            );
        }
    }
