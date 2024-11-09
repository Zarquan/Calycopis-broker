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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import net.ivoa.calycopis.message.MessageEntity;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;

/**
 * JPA Entity for a Component
 * https://www.javatpoint.com/hibernate-table-per-hierarchy-using-annotation-tutorial-example
 *
 */
@Entity
@Table(name = "components")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ComponentEntity
    implements Component
    {

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity()
        {
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name)
        {
        this(
            name,
            OffsetDateTime.now()
            );
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name, final OffsetDateTime created)
        {
        this.name = name;
        this.created = created;
        }

    @Id
    @GeneratedValue
    protected UUID uuid;

    public UUID getUuid()
        {
        return this.uuid ;
        }

    @Column(name = "name")
    private String name;
    @Override
    public String getName()
        {
        return this.name;
        }

    @Column(name = "created")
    private OffsetDateTime created;
    @Override
    public OffsetDateTime getCreated()
        {
        return this.created;
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    private List<MessageEntity> messages = new ArrayList<MessageEntity>();

    @Override
    public List<MessageEntity> getMessages()
        {
        return messages ;
        }

    public void addDebug(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.DEBUG,
            type,
            template,
            Collections.emptyMap()
            );
        }

    public void addDebug(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.DEBUG,
            type,
            template,
            values
            );
        }

    public void addInfo(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            Collections.emptyMap()
            );
        }

    public void addInfo(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            values
            );
        }
    
    public void addWarning(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            Collections.emptyMap()
            );
        }

    public void addWarning(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            values
            );
        }

    public void addError(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            Collections.emptyMap()
            );
        }

    public void addError(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            values
            );
        }
    
    public void addMessage(final LevelEnum level, final String type, final String template)
        {
        this.addMessage(
            level,
            type,
            template,
            Collections.emptyMap()
            );
        }
    
    public void addMessage(final LevelEnum level, final String type, final String template, final Map<String, Object> values)
        {
        MessageEntity message = new MessageEntity(
            this,
            level,
            type,
            template,
            values
            );
        messages.add(
            message
            );
        }

    @Override
    public boolean equals(Object object)
        {
        if (null != object)
            {
            if (this == object)
                {
                return true;
                }
            if (object.getClass().equals(this.getClass()))
                {
                if (this.uuid != null)
                    {
                    return this.uuid.equals(
                        ((ComponentEntity) object).getUuid()
                        );
                    }
                }
            }
        return false ;
        }
    }
