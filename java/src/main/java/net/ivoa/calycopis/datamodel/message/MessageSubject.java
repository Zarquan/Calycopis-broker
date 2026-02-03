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
package net.ivoa.calycopis.datamodel.message;

import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;

/**
 * Public interface for something that has messages about it.
 *   
 */
public interface MessageSubject
    {
    /**
     * Get the List of messages about this thing.
     * 
     */
    public List<MessageEntity> getMessages();

    /**
     * Add a debug message.
     * 
     */
    public void addDebug(final String type, final String template);

    /**
     * Add a debug message.
     * 
     */
    public void addDebug(final String type, final String template, final Map<String, Object> values);

    /**
     * Add an information message.
     * 
     */
    public void addInfo(final String type, final String template);

    /**
     * Add an information message.
     * 
     */
    public void addInfo(final String type, final String template, final Map<String, Object> values);
    
    /**
     * Add a warning message.
     * 
     */
    public void addWarning(final String type, final String template);

    /**
     * Add a warning message.
     * 
     */
    public void addWarning(final String type, final String template, final Map<String, Object> values);

    /**
     * Add an error message.
     * 
     */
    public void addError(final String type, final String template);

    /**
     * Add an error message.
     * 
     */
    public void addError(final String type, final String template, final Map<String, Object> values);

    /**
     * Add a message.
     * 
     */
    public void addMessage(final LevelEnum level, final String type, final String template);

    /**
     * Add a message.
     * 
     */
    public void addMessage(final LevelEnum level, final String type, final String template, final Map<String, Object> values);

    }
