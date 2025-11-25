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

package net.ivoa.calycopis.datamodel.session.simple;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutionSession;
import net.ivoa.calycopis.openapi.model.IvoaAbstractOption;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSession;
import net.ivoa.calycopis.openapi.model.IvoaSimpleExecutionSessionPhase;
import net.ivoa.calycopis.openapi.model.IvoaSimpleSessionConnector;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * An execution session Entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "simpleexecutionsessions"
    )
@DiscriminatorValue(
    value = "uri:simple-execution-session"
    )
public class SimpleExecutionSessionEntity
    extends AbstractExecutionSessionEntity
    implements SimpleExecutionSession
    {
    
    @Override
    public URI getKind()
        {
        return SimpleExecutionSession.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleExecutionSessionEntity()
        {
        super();
        }

    /**
     * Protected constructor, used to create an example for the find method.
     *
     */
    protected SimpleExecutionSessionEntity(final IvoaSimpleExecutionSessionPhase phase)
        {
        super();
        this.phase = phase;
        }
    
    /**
     * Protected constructor with parent.
     *
     */
    public SimpleExecutionSessionEntity(
        final OfferSetEntity offerset,
        final OfferSetRequestParserContext context,
        final ResourceOffer offerblock
        ){
        super(
            offerset,
            offerset.getName() + "-" + offerblock.getName()
            );
        this.phase = IvoaSimpleExecutionSessionPhase.OFFERED;
        this.expires = offerset.getExpires();
        }

    @Column(name = "phase")
    @Enumerated(EnumType.STRING)
    private IvoaSimpleExecutionSessionPhase phase = IvoaSimpleExecutionSessionPhase.INITIAL;
    @Override
    public IvoaSimpleExecutionSessionPhase getPhase()
        {
        return this.phase;
        }
    @Override
    public void setPhase(final IvoaSimpleExecutionSessionPhase newphase)
        {
        // TODO This is where we need to have the phase transition checking.
        this.phase = newphase;
        }
    
    @Column(name = "expires")
    private OffsetDateTime expires;
    @Override
    public OffsetDateTime getExpires()
        {
        return this.expires;
        }

    @OneToOne(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    private AbstractExecutableEntity executable;
    @Override
    public AbstractExecutableEntity getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final AbstractExecutableEntity executable)
        {
        this.executable = executable;
        }

    @OneToOne(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    private AbstractComputeResourceEntity computer;

    @Override
    public AbstractComputeResourceEntity getComputeResource()
        {
        return computer;
        }
    public void setComputeResource(final AbstractComputeResourceEntity computer)
        {
        this.computer = computer;
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractDataResourceEntity> dataresources = new ArrayList<AbstractDataResourceEntity>();

    @Override
    public List<AbstractDataResourceEntity> getDataResources()
        {
        return dataresources;
        }

    public void addDataResource(final AbstractDataResourceEntity resource)
        {
        dataresources.add(
            resource
            );
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractStorageResourceEntity> storageresources = new ArrayList<AbstractStorageResourceEntity>();

    @Override
    public List<AbstractStorageResourceEntity> getStorageResources()
        {
        return storageresources;
        }

    public void addStorageResource(final AbstractStorageResourceEntity resource)
        {
        storageresources.add(
            resource
            );
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractVolumeMountEntity> volumeMounts = new ArrayList<AbstractVolumeMountEntity>();

    @Override
    public List<AbstractVolumeMountEntity> getVolumeMounts()
        {
        return volumeMounts;
        }

    public void addVolumeMount(final AbstractVolumeMountEntity volume)
        {
        volumeMounts.add(
            volume
            );
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<SimpleSessionConnectorEntity> connectors = new ArrayList<SimpleSessionConnectorEntity>();

    @Override
    public List<SimpleSessionConnectorEntity> getConnectors()
        {
        return connectors;
        }

    @Override
    public void addConnector(final SimpleSessionConnectorEntity connector)
        {
        connectors.add(
            connector
            );
        }

    @Override
    public void addConnector(String type, String protocol, String location)
        {
        this.addConnector(
            new SimpleSessionConnectorEntity(
                this,
                type,
                protocol,
                location
                )
            );
        }

    protected List<@Valid IvoaAbstractOption> getOptions()
        {
        return null;
        }
    
    public IvoaSimpleExecutionSession makeBean(final URIBuilder uribuilder)
        {
        return this.fillBean(
            uribuilder,
            new IvoaSimpleExecutionSession().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }

    public IvoaSimpleExecutionSession fillBean(
        final URIBuilder uribuilder,
        final IvoaSimpleExecutionSession bean
        ){
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            this.getPhase()
            );
        bean.setExpires(
            this.getExpires()
            );
        bean.setExecutable(
            this.getExecutable().makeBean(
                uribuilder
                )
            );
        bean.setCompute(
            this.getComputeResource().makeBean(
                uribuilder
                )
            );

        for (AbstractDataResourceEntity resource : this.getDataResources())
            {
            bean.addDataItem(
                resource.makeBean(
                    uribuilder
                    )
                );
            }

        for (AbstractStorageResourceEntity resource : this.getStorageResources())
            {
            bean.addStorageItem(
                resource.makeBean(
                    uribuilder
                    )
                );
            }

        for (SimpleSessionConnectorEntity connector : this.getConnectors())
            {
            IvoaSimpleSessionConnector accessor = new IvoaSimpleSessionConnector();
            accessor.setKind(connector.getType());
            accessor.setProtocol(connector.getProtocol());
            accessor.setLocation(connector.getLocation());
            bean.addConnectorsItem(
                accessor
                );
            }
        
        return bean;
        }
    }

