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

package net.ivoa.calycopis.functional.processing.component;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.functional.processing.ProcessingRequestEntity;
import net.ivoa.calycopis.functional.processing.RequestProcessingPlatform;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "componentprocessingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class ComponentProcessingRequestEntity
extends ProcessingRequestEntity
implements ComponentProcessingRequest
    {

    protected ComponentProcessingRequestEntity()
        {
        super();
        }

    protected ComponentProcessingRequestEntity(final URI kind, final LifecycleComponentEntity component)
        {
        super(kind);
        this.component = component;
        }

    @JoinColumn(name = "component", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    protected LifecycleComponentEntity component;

    @Override
    public LifecycleComponentEntity getComponent()
        {
        return this.component;
        }

    protected void fail(final RequestProcessingPlatform platform)
        {        
        log.debug("ProcessingRequest [{}][{}] failed", this.getUuid(), this.getClass().getSimpleName());
        platform.getSessionProcessingRequestFactory().createFailSessionRequest(
            this.component.getSession()
            );
        }

    }
