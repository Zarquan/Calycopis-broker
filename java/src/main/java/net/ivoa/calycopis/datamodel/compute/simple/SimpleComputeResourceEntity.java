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

package net.ivoa.calycopis.datamodel.compute.simple;

import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A Simple compute resource.
 *
 */
@Slf4j
@Entity
@Table(
    name = "simplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:simple-compute-resources"
    )
public class SimpleComputeResourceEntity
    extends AbstractComputeResourceEntity
    implements SimpleComputeResource
    {
    @Override
    public URI getKind()
        {
        return SimpleComputeResource.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with session, validation result, and offer.
     *
     */
    public SimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        this(
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
    public SimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer,
        final IvoaSimpleComputeResource validated
        ){
        super(
            session,
            result,
            offer,
            validated.getMeta()
            );
        
        if (validated.getCores() != null)
            {
            this.minrequestedcores = validated.getCores().getMin();
            this.maxrequestedcores = validated.getCores().getMax();
            }

        this.minofferedcores   = offer.getCores();
        this.maxofferedcores   = offer.getCores();

        if (validated.getMemory() != null)
            {
            this.minrequestedmemory = validated.getMemory().getMin();
            this.maxrequestedmemory = validated.getMemory().getMax();
            }

        this.minofferedmemory = offer.getMemory();
        this.maxofferedmemory = offer.getMemory();

        //
        // Add our volumes.
        /*
        for (IvoaSimpleComputeVolume volume : template.getVolumes())
            {
            this.volumes.add(
                new SimpleComputeVolumeEntity(
                    this,
                    volume
                    )
                );                
            }
         * 
         */
        }

    @Column(name="minrequestedcores")
    private Long minrequestedcores;
    @Override
    public Long getMinRequestedCores()
        {
        return this.minrequestedcores;
        }

    @Column(name="maxrequestedcores")
    private Long maxrequestedcores;
    @Override
    public Long getMaxRequestedCores()
        {
        return this.maxrequestedcores;
        }
    
    @Column(name="minofferedcores")
    private Long minofferedcores;
    @Override
    public Long getMinOfferedCores()
        {
        return this.minofferedcores;
        }

    @Column(name="maxofferedcores")
    private Long maxofferedcores;
    @Override
    public Long getMaxOfferedCores()
        {
        return this.maxofferedcores;
        }
    
    @Column(name="minrequestedmemory")
    private Long minrequestedmemory;
    @Override
    public Long getMinRequestedMemory()
        {
        return this.minrequestedmemory;
        }

    @Column(name="maxrequestedmemory")
    private Long maxrequestedmemory;
    @Override
    public Long getMaxRequestedMemory()
        {
        return this.maxrequestedmemory;
        }
    
    @Column(name="minofferedmemory")
    private Long minofferedmemory;
    @Override
    public Long getMinOfferedMemory()
        {
        return this.minofferedmemory;
        }

    @Column(name="maxofferedmemory")
    private Long maxofferedmemory;
    @Override
    public Long getMaxOfferedMemory()
        {
        return this.maxofferedmemory;
        }

    /*
     * TODO
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<SimpleComputeVolumeEntity> volumes = new ArrayList<SimpleComputeVolumeEntity>();
    @Override
    public List<SimpleComputeVolume> getVolumes()
        {
        return new ListWrapper<SimpleComputeVolume, SimpleComputeVolumeEntity>(
            volumes
            ){
            public SimpleComputeVolume wrap(final SimpleComputeVolumeEntity inner)
                {
                return (SimpleComputeVolume) inner ;
                }
            };
        }
     * 
     */

    @Override
    public IvoaSimpleComputeResource makeBean(final URIBuilder builder)
        {
        return fillBean(
            new IvoaSimpleComputeResource().meta(
                this.makeMeta(
                    builder
                    )
                )               
            );
        }

    public IvoaSimpleComputeResource fillBean(final IvoaSimpleComputeResource bean)
        {
        super.fillBean(bean);
        
        IvoaSimpleComputeCores coresbean = new IvoaSimpleComputeCores();
        coresbean.setMin(minofferedcores);
        coresbean.setMax(maxofferedcores);
        bean.setCores(coresbean);
        
        IvoaSimpleComputeMemory memorybean = new IvoaSimpleComputeMemory();
        memorybean.setMin(minofferedcores);
        memorybean.setMax(maxofferedcores);
        bean.setMemory(memorybean);
        
        return bean;
        }

    // TODO Move this to a Canfar Skaha specific class.
    @Column(name="skahasessionid")
    private String skahasessionid;
    @Override
    public String getSkahaSessionID()
        {
        return this.skahasessionid;
        }

    
    // TODO Move this to a test specific class.
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

            int count = SimpleComputeResourceEntity.this.preparecounter ;
            
            @Override
            public boolean process()
                {
                log.debug(
                    "Preparing [{}][{}] count [{}]",
                    SimpleComputeResourceEntity.this.getUuid(),
                    SimpleComputeResourceEntity.this.getClass().getSimpleName(),
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
                        SimpleComputeResourceEntity.this.getUuid(),
                        SimpleComputeResourceEntity.this.getClass().getSimpleName()
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
                if (component instanceof SimpleComputeResourceEntity)
                    {
                    return postProcess(
                        (SimpleComputeResourceEntity) component
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
                
            public boolean postProcess(final SimpleComputeResourceEntity component)
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

