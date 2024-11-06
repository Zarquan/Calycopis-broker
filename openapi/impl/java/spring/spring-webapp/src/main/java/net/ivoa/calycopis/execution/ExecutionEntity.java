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

package net.ivoa.calycopis.execution;

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
import net.ivoa.calycopis.component.ComponentEntity;
import net.ivoa.calycopis.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offerset.OfferSetEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;

/**
 * An Execution Entity.
 *
 */
@Entity
@Table(
    name = Execution.TABLE_NAME
    )
@DiscriminatorValue(
    value = Execution.TYPE_DISCRIMINATOR
    )
public class ExecutionEntity
    extends ComponentEntity
    implements Execution
    {

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OfferSetEntity parent;

    @Override
    public OfferSetEntity getParent()
        {
        return this.parent;
        }

    public void setParent(final OfferSetEntity parent)
        {
        this.parent = parent;
        }

    /**
     * Protected constructor
     *
     */
    protected ExecutionEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public ExecutionEntity(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state)
        {
        super();
        this.state   = state;
        this.parent  = parent;
        this.expires = parent.getExpires();
        this.startinstantsec  = offerblock.getStartTime().getEpochSecond();
//      this.startdurationsec = offerblock.getStartTime().toDuration().getSeconds();
        this.exedurationsec   = context.getDuration().getSeconds();
        
        }

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private IvoaExecutionSessionStatus state;
    @Override
    public IvoaExecutionSessionStatus getState()
        {
        return this.state;
        }
    @Override
    public void setState(final IvoaExecutionSessionStatus state)
        {
        this.state = state;
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
    List<SimpleComputeResourceEntity> computeresources = new ArrayList<SimpleComputeResourceEntity>();

    @Override
    public List<SimpleComputeResourceEntity> getComputeResources()
        {
        return computeresources;
        }
     
    public void addCompute(final SimpleComputeResourceEntity compute)
        {
        computeresources.add(compute);
        compute.setParent(this);
        }
    }

