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

package net.ivoa.calycopis.datamodel.session;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;

/**
 * An Execution Entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "executionsessions"
    )
@DiscriminatorValue(
    value = "uri:execution-session"
    )
public class SessionEntity
    extends ScheduledComponentEntity
    implements Session
    {

    @JoinColumn(name = "offerset", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OfferSetEntity offerset;

    @Override
    public OfferSetEntity getOfferSet()
        {
        return this.offerset;
        }

    /**
     * Protected constructor
     *
     */
    protected SessionEntity()
        {
        super();
        }

    /**
     * Protected constructor, used to create an example for the find method.
     *
     */
    protected SessionEntity(final IvoaExecutionSessionPhase phase)
        {
        super();
        this.phase = phase;
        }
    
    /**
     * Protected constructor with parent.
     *
     */
    public SessionEntity(final OfferSetEntity offerset, final OfferSetRequestParserContext context, final ResourceOffer offerblock)
        {
        super(
            null,
            offerset.getName() + "-" + offerblock.getName()
            );
        this.phase = IvoaExecutionSessionPhase.OFFERED;
        this.offerset = offerset;
        offerset.addExecutionSession(
            this
            );
        this.expires = offerset.getExpires();

        // TODO factor in the compute prepare time.
        // OfferBlock needs to have separate prepare and available times.
        this.availableStartInstantSeconds = offerblock.getStartTime().getEpochSecond();
        this.availableDurationSeconds     = offerblock.getDuration().toSeconds();

        this.prepareDurationSeconds       = context.getTotalPrepareTime();
        this.prepareStartInstantSeconds   = this.availableStartInstantSeconds - this.prepareDurationSeconds;

        }

    @Column(name = "phase")
    @Enumerated(EnumType.STRING)
    private IvoaExecutionSessionPhase phase;
    @Override
    public IvoaExecutionSessionPhase getPhase()
        {
        return this.phase;
        }
    @Override
    public void setPhase(final IvoaExecutionSessionPhase newphase)
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

    @Override
    public IvoaExecutionSessionResponse getIvoaBean(final String baseurl)
        {
        IvoaExecutionSessionResponse bean = new IvoaExecutionSessionResponse();
        bean.setUuid(this.getUuid());
        bean.setName(this.getName());
        bean.setType(Session.TYPE_DISCRIMINATOR);
        bean.setCreated(this.getCreated());
        bean.setExpires(this.getExpires());
        bean.setPhase(this.getPhase());
        bean.setHref(
            baseurl + Session.REQUEST_PATH + this.getUuid()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        bean.setExecutable(
            this.getExecutable().getIvoaBean(
                baseurl
                )
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );

        bean.setCompute(
            this.getComputeResource().getIvoaBean(
                baseurl
                )
            );

        for (AbstractDataResourceEntity resource : this.getDataResources())
            {
            bean.addDataItem(
                resource.getIvoaBean()
                );
            }

        for (AbstractStorageResourceEntity resource : this.getStorageResources())
            {
            bean.addStorageItem(
                resource.getIvoaBean(
                    baseurl
                    )
                );
            }

        bean.setOptions(null);

        return bean;
        }
    }

