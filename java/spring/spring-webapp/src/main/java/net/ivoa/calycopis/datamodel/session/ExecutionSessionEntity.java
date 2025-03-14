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

import java.time.Duration;
import java.time.Instant;
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
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionPhase;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;

/**
 * An Execution Entity.
 *
 */
@Entity
@Table(
    name = "sessions"
    )
@DiscriminatorValue(
    value = "uri:execution-session"
    )
public class ExecutionSessionEntity
    extends ComponentEntity
    implements ExecutionSession
    {

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OfferSetEntity parent;

    @Override
    public OfferSetEntity getParent()
        {
        return this.parent;
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
    public ExecutionSessionEntity(final OfferSetEntity parent, final OfferSetRequestParserContext state, final ResourceOffer offerblock)
        {
        super("no name");
        this.phase = IvoaExecutionSessionPhase.OFFERED;
        this.parent = parent;
        parent.addExecutionSession(
            this
            );
        this.expires = parent.getExpires();
        this.startinstantsec  = offerblock.getStartTime().getEpochSecond();
//      this.startdurationsec = offerblock.getStartTime().toDuration().getSeconds();
        this.exedurationsec   = state.getExecutionDuration().getSeconds();
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
    
    @Column(name = "startinstantsec")
    private long startinstantsec;
    @Override
    public long getStartInstantSeconds()
        {
        return this.startinstantsec;
        }
    @Override
    public Instant getStartInstant()
        {
        return Instant.ofEpochSecond(
            startinstantsec
            );
        }
  
/*
 * 
    @Column(name = "startdurationsec")
    private long startdurationsec;
    @Override
    public long getStartDurationSeconds()
        {
        return this.startdurationsec;
        }
    @Override
    public Duration getStartDuration()
        {
        return Duration.ofSeconds(
            startdurationsec
            );
        }
    @Override
    public Interval getStartInterval()
        {
        return Interval.of(
            getStartInstant(),
            getStartDuration()
            );
        }
 *     
 */

    @Column(name = "exedurationsec")
    private long exedurationsec;
    @Override
    public long getExeDurationSeconds()
        {
        return this.exedurationsec;
        }
    @Override
    public Duration getExeDuration()
        {
        return Duration.ofSeconds(
            exedurationsec
            );
        }

    @Column(name = "expires")
    private OffsetDateTime expires;
    @Override
    public OffsetDateTime getExpires()
        {
        return this.expires;
        }

    @OneToOne(
        mappedBy = "parent",
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
        mappedBy = "parent",
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
        mappedBy = "parent",
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
            mappedBy = "parent",
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

    @Override
    public IvoaExecutionSessionResponse getIvoaBean(final String baseurl)
        {
        return new ExecutionSessionResponseBean(
            baseurl,
            this
            );
        }
    }

