/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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
 *
 */

package net.ivoa.calycopis.datamodel.storage.docker;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntity;
import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockervolumemountstorage"
    )
@DiscriminatorValue(
    value="uri:docker-volume-storage"
    )
public class DockerVolumeMountStorageEntity
extends SimpleStorageResourceEntity
implements DockerVolumeMountStorage
    {

    /**
     * 
     */
    public DockerVolumeMountStorageEntity()
        {
        super();
        }

    /**
     * 
     */
    public DockerVolumeMountStorageEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    // The volume identifier from the Docker API.
    private String volumeIdent;
    @Override
    public String getMountPath()
        {
        return this.volumeIdent;
        }

    @Override
    public ProcessingAction getPrepareAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        // TODO Create a ProcessingAction that calls the Docker API and creates a new volume.
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(IvoaLifecyclePhase.AVAILABLE);
                }
            };
        }

    @Override
    public ProcessingAction getMonitorAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        // TODO Create a ProcessingAction that calls the Docker API to monitor the volume status.
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(IvoaLifecyclePhase.COMPLETED);
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        // TODO Create a ProcessingAction that calls the Docker API to delete the volume.
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(IvoaLifecyclePhase.COMPLETED);
                }
            };
        }
    }
