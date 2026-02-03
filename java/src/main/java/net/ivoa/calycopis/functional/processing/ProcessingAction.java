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

package net.ivoa.calycopis.functional.processing;

import java.util.UUID;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaLifecyclePhase;

/**
 * An external action to be performed outside of any transaction context.
 * 
 */
public interface ProcessingAction
    {
    public static final ProcessingAction NO_ACTION = null ;

    /**
     * Get the UUID of the original ActionRequest.
     *  
     */
    public UUID getRequestUuid();
    
    /**
     * Perform the action, outside any transaction context.
     * 
     */
    public boolean process();

    /**
     * Get the next phase after this action is performed.
     * 
     */
    public IvoaLifecyclePhase getNextPhase();

    /**
     * Prepare the Action, performed in a Transaction before processing.
     * TODO Move this to a ComponentProcessingAction.
     * 
    boolean preProcess(final LifecycleComponentEntity component);
     */

    /**
     * Update the Component, performed in a Transaction after processing.
     * TODO Move this to a ComponentProcessingAction.
     *
     */
    boolean postProcess(final LifecycleComponentEntity component);
    
    }
