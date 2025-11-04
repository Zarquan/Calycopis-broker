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

package net.ivoa.calycopis.datamodel.compute;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;

/**
 * 
 */
@Entity
@Table(
    name = "abstractcomputeresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractComputeResourceEntity
extends LifecycleComponentEntity
implements AbstractComputeResource
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * Automatically adds this resource to the parent SessionEntity.
     * 
     */
    protected AbstractComputeResourceEntity(
        final SessionEntity session,
        final AbstractComputeResourceValidator.Result result,
        final ComputeResourceOffer offer,
        final String name
        ){
        super(
            name
            );

        this.session = session;
        session.setComputeResource(
            this
            );
        
        //
        // Start preparing before the offer is available.
        // TODO Add available time and preparation time to the offer.
        this.prepareDurationSeconds     = result.getPreparationTime();
        this.prepareStartInstantSeconds = offer.getStartTime().getEpochSecond() - result.getPreparationTime(); 

        //
        // Available as soon as the preparation is done.
        // TODO Add available time and preparation time to the offer.
        this.availableDurationSeconds      = offer.getDuration().getSeconds();
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = offer.getStartTime().getEpochSecond();

        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SessionEntity session;

    @Override
    public SessionEntity getSession()
        {
        return this.session;
        }

    
    protected IvoaAbstractComputeResource fillBean(final IvoaAbstractComputeResource bean)
        {
        bean.setUuid(
            this.getUuid()
            );
        bean.setPhase(
            this.getPhase()
            );
        bean.setName(
            this.getName()
            );
        bean.setCreated(
            this.getCreated()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }
    
    }