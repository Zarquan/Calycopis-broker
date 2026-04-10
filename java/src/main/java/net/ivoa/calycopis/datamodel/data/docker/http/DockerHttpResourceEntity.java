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

package net.ivoa.calycopis.datamodel.data.docker.http;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator.Result;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockerhttpdataresources"
    )
public class DockerHttpResourceEntity
extends SimpleDataResourceEntity
implements DockerHttpResource
    {

    /**
     * 
     */
    public DockerHttpResourceEntity()
        {
        super();
        }

    /**
     * 
     */
    public DockerHttpResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final Result result
        ){
        super(
            session,
            storage,
            result
            );
        }

    /**
     * 
     */
    public DockerHttpResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final Result result,
        final IvoaSimpleDataResource validated
        ){
        super(
            session,
            storage,
            result,
            validated
            );
        }

    @Override
    public ProcessingAction getPrepareAction(Platform platform, ComponentProcessingRequest request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    @Override
    public ProcessingAction getReleaseAction(Platform platform, ComponentProcessingRequest request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
