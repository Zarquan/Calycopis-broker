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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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
import net.ivoa.calycopis.datamodel.component.ScheduledComponentEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.resource.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
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
public class ExecutionSessionEntity
    extends ScheduledComponentEntity
    implements ExecutionSession
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
    protected ExecutionSessionEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public ExecutionSessionEntity(final OfferSetEntity offerset, final OfferSetRequestParserContext context, final ResourceOffer offerblock)
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
        this.availableStartInstantSeconds = offerblock.getStartTime().getEpochSecond();
        this.availableDurationSeconds     = offerblock.getDuration().toSeconds();
        this.prepareDurationSeconds       = context.getMaxPreparationDuration().toSeconds();
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
    public void setPhase(final IvoaExecutionSessionPhase phase)
        {
        this.phase = phase;
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

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractComputeResourceEntity> computeresources = new ArrayList<AbstractComputeResourceEntity>();

    @Override
    public List<AbstractComputeResourceEntity> getComputeResources()
        {
        return computeresources;
        }

    public void addComputeResource(final AbstractComputeResourceEntity resource)
        {
        computeresources.add(
            resource
            );
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
        bean.setType(ExecutionSession.TYPE_DISCRIMINATOR);
        bean.setCreated(this.getCreated());
        bean.setExpires(this.getExpires());
        bean.setPhase(this.getPhase());
        bean.setHref(
            baseurl + ExecutionSession.REQUEST_PATH + this.getUuid()
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

        bean.setResources(new IvoaExecutionResourceList());
        for (AbstractComputeResourceEntity resource : this.getComputeResources())
            {
            bean.getResources().addComputeItem(
                resource.getIvoaBean(
                    baseurl
                    )
                );
            }

        for (AbstractDataResourceEntity resource : this.getDataResources())
            {
            bean.getResources().addDataItem(
                resource.getIvoaBean()
                );
            }

        for (AbstractStorageResourceEntity resource : this.getStorageResources())
            {
            bean.getResources().addStorageItem(
                resource.getIvoaBean(
                    baseurl
                    )
                );
            }


        bean.setOptions(null);


        return bean;
        }
    }

