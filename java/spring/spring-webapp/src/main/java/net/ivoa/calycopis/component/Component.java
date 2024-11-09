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

package net.ivoa.calycopis.component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import net.ivoa.calycopis.message.MessageEntity;

/**
 * Public interface for a Component
 * 
 */
public interface Component
    {
    /**
     * The URL path for Components.
     *
     */
    public static final String REQUEST_PATH = "/component/" ;

    /**
     * Get the Component UUID.
     *
     */
    public UUID getUuid();

    /**
     * Get the Component name.
     *
     */
    public String getName();
    
    /**
     * Get the Component creation date.
     *
     */
    public OffsetDateTime getCreated();

    /**
     * Get a list of messages for this Component.
     *
     */
    public List<MessageEntity> getMessages();

    }
