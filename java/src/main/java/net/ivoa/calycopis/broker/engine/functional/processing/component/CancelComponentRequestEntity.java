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

package net.ivoa.calycopis.broker.engine.functional.processing.component;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityImpl;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "cancelcomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class CancelComponentRequestEntity
extends ComponentProcessingRequestEntityImpl
implements ComponentProcessingRequest
    {

    protected CancelComponentRequestEntity()
        {
        super();
        }

    protected CancelComponentRequestEntity(final LifecycleComponentEntityImpl component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final ProcessingRequestFactory processing, final Platform platform)
        {
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ComponentProcessingAction action)
        {
        // TODO Auto-generated method stub
        }
    }
