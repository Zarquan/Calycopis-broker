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
 *
 */

package net.ivoa.calycopis.datamodel.executable.docker.mock;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "mockdockercontainers"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class MockDockerContainerEntity
    extends DockerContainerEntity
    implements MockDockerContainer
    {

    /**
     * 
     */
    public MockDockerContainerEntity()
        {
        super();
        }

    /**
     *
     */
    public MockDockerContainerEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    @Column(name="preparecounter")
    private int preparecounter;

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        return new ProcessingAction()
            {
            int count = MockDockerContainerEntity.this.preparecounter ;

            @Override
            public boolean process()
                {
                log.debug(
                    "** Preparing [{}][{}] count [{}]",
                    MockDockerContainerEntity.this.getUuid(),
                    MockDockerContainerEntity.this.getClass().getSimpleName(),
                    count
                    );
                count++;
                try {
                    Thread.sleep(1000);
                    }
                catch (InterruptedException e)
                    {
                    log.error(
                        "Interrupted while preparing [{}][{}]",
                        MockDockerContainerEntity.this.getUuid(),
                        MockDockerContainerEntity.this.getClass().getSimpleName()
                        );
                    }
                return true ;
                }

            @Override
            public UUID getRequestUuid()
                {
                return request.getUuid();
                }

            @Override
            public IvoaLifecyclePhase getNextPhase()
                {
                if (count < 4)
                    {
                    return IvoaLifecyclePhase.PREPARING ;
                    }
                else {
                    return IvoaLifecyclePhase.AVAILABLE ;
                    }
                }

            @Override
            public boolean postProcess(final LifecycleComponentEntity component)
                {
                if (component instanceof MockDockerContainerEntity)
                    {
                    ((MockDockerContainerEntity) component).preparecounter = this.count ;
                    return true ;
                    }
                else {
                    log.error(
                        "Unexpected component type [{}] post processing [{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid()
                        );
                    return false ;
                    }
                }
            };
        }
    }
