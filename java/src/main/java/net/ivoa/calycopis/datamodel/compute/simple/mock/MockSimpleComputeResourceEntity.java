/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.datamodel.compute.simple.mock;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 * A Simple compute resource.
 *
 */
@Slf4j
@Entity
@Table(
    name = "mocksimplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:mock-simple-compute-resources"
    )
public class MockSimpleComputeResourceEntity
extends SimpleComputeResourceEntity
implements MockSimpleComputeResource
    {

    /**
     * Protected constructor
     *
     */
    protected MockSimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with session, validation result, and offer.
     *
     */
    public MockSimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final MockSimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        super(
            session,
            result,
            offer,
            (IvoaSimpleComputeResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with session, template and offer.
     * TODO validated can be replaced by Result.getObject()
     * 
     */
    public MockSimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer,
        final IvoaSimpleComputeResource validated
        ){
        super(
            session,
            result,
            offer,
            validated
            );
        }

    @Column(name="preparecounter")
    private int preparecounter;
    public int getPrepareCounter()
        {
        return this.preparecounter;
        }

    @Override
    public ProcessingAction getPrepareAction(final ComponentProcessingRequest request)
        {
        return new ProcessingAction()
            {

            int count = MockSimpleComputeResourceEntity.this.preparecounter ;
            
            @Override
            public boolean process()
                {
                log.debug(
                    "Preparing [{}][{}] count [{}]",
                    MockSimpleComputeResourceEntity.this.getUuid(),
                    MockSimpleComputeResourceEntity.this.getClass().getSimpleName(),
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
                        MockSimpleComputeResourceEntity.this.getUuid(),
                        MockSimpleComputeResourceEntity.this.getClass().getSimpleName()
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
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof MockSimpleComputeResourceEntity)
                    {
                    return postProcess(
                        (MockSimpleComputeResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected component type [{}] post processing [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    return false ;
                    }
                }
                
            public boolean postProcess(final MockSimpleComputeResourceEntity component)
                {
                log.debug(
                    "Post processing resource [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                component.preparecounter = this.count ;
                return true ;
                }
            };
        }
    }
