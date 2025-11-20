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

package net.ivoa.calycopis.datamodel.component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.message.MessageEntity;
import net.ivoa.calycopis.datamodel.message.MessageItemBean;
import net.ivoa.calycopis.datamodel.message.MessageSubject;
import net.ivoa.calycopis.functional.planning.AbstractPlanningStepEntity;
import net.ivoa.calycopis.functional.planning.PlanningStep;
import net.ivoa.calycopis.functional.planning.PlanningStepSequence;
import net.ivoa.calycopis.openapi.model.IvoaComponentMetadata;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * JPA Entity for a Component
 * https://www.javatpoint.com/hibernate-table-per-hierarchy-using-annotation-tutorial-example
 *
 */
@Slf4j
@Entity
@Table(name = "components")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ComponentEntity
    implements Component, MessageSubject
    {

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     *
    @Deprecated
    protected ComponentEntity(final String name)
        {
        this(
            name,
            null,
            OffsetDateTime.now()
            );
        }
     */

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name, final String description)
        {
        this(
            name,
            description,
            OffsetDateTime.now()
            );
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final IvoaComponentMetadata meta)
        {
        this(
            meta.getName(),
            meta.getDescription(),
            OffsetDateTime.now()
            );
        }
    
    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name, final String description, final OffsetDateTime created)
        {
        this.name = name;
        this.created = created;
        this.description = description;
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

    @Column(name = "description")
    private String description;
    @Override
    public String getDescription()
        {
        return this.description;
        }

    @Column(name = "created")
    private OffsetDateTime created;
    @Override
    public OffsetDateTime getCreated()
        {
        return this.created;
        }

    @Column(name = "modified")
    private OffsetDateTime modified;
    @Override
    public OffsetDateTime getModified()
        {
        return this.modified;
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

    @Override
    public void addDebug(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.DEBUG,
            type,
            template,
            Collections.emptyMap()
            );
        }

    @Override
    public void addDebug(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.DEBUG,
            type,
            template,
            values
            );
        }

    @Override
    public void addInfo(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            Collections.emptyMap()
            );
        }

    @Override
    public void addInfo(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            values
            );
        }
    
    @Override
    public void addWarning(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            Collections.emptyMap()
            );
        }

    @Override
    public void addWarning(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            values
            );
        }

    @Override
    public void addError(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            Collections.emptyMap()
            );
        }

    @Override
    public void addError(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            values
            );
        }
    
    @Override
    public void addMessage(final LevelEnum level, final String type, final String template)
        {
        this.addMessage(
            level,
            type,
            template,
            Collections.emptyMap()
            );
        }

    @Override
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
    
    /**
     * Wrap a List of JPA MessageEntity(s) as a List of IvoaMessageItems.
     * 
     */
    public List<IvoaMessageItem> getMessageBeans()
        {
        return new ListWrapper<IvoaMessageItem, MessageEntity>(
            this.getMessages()
            ){
            public IvoaMessageItem wrap(final MessageEntity inner)
                {
                return new MessageItemBean(
                    inner
                    );
                }
            };
        }
    
    /**
     * A SequenceList of PlanningSteps. 
     * 
     */
    public abstract class PlanningStepSequenceImpl
    implements PlanningStepSequence
        {
        
        public PlanningStepSequenceImpl()
            {
            super();
            }

        public void addStep(final AbstractPlanningStepEntity step)
            {
            log.debug("Add step [{}]", step);
            if (this.getFirst() == null)
                {
                this.setFirst(step);
                }
            if (this.getLast() != null)
                {
                this.getLast().setNext(
                    step
                    );
                }
            step.setPrev(
                this.getLast()
                );
            this.setLast(step);
            }
        
        public abstract AbstractPlanningStepEntity getFirst();
        public abstract void setFirst(final AbstractPlanningStepEntity step);

        public abstract AbstractPlanningStepEntity getLast();
        public abstract void setLast(final AbstractPlanningStepEntity step);

        public Iterable<PlanningStep> forwards()
            {
            return new Iterable<PlanningStep>()
                {
                @Override
                public Iterator<PlanningStep> iterator()
                    {
                    return new Iterator<PlanningStep>()
                        {
                        private PlanningStep step = getFirst();
                        
                        @Override
                        public boolean hasNext()
                            {
                            return (step != null);
                            }

                        @Override
                        public PlanningStep next()
                            {
                            PlanningStep temp = step;
                            if (step != null)
                                {
                                step = step.getNext();
                                }
                            return temp;
                            }
                        } ;
                    }
                };
            }

        public Iterable<PlanningStep> backwards()
            {
            return new Iterable<PlanningStep>()
                {
                @Override
                public Iterator<PlanningStep> iterator()
                    {
                    return new Iterator<PlanningStep>()
                        {
                        private PlanningStep step = getLast();
                        
                        @Override
                        public boolean hasNext()
                            {
                            return (step != null);
                            }

                        @Override
                        public PlanningStep next()
                            {
                            PlanningStep temp = step;
                            if (step != null)
                                {
                                step = step.getPrev();
                                }
                            return temp;
                            }
                        } ;
                    }
                };
            }
        }

    @JoinColumn(name = "preparefirst", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity prepareFirst;

    @JoinColumn(name = "preparelast", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity prepareLast;
        
    public PlanningStepSequenceImpl getPrepareList()
        {
        return new PlanningStepSequenceImpl()
            {
            @Override
            public AbstractPlanningStepEntity getFirst()
                {
                return prepareFirst;
                }

            @Override
            public void setFirst(final AbstractPlanningStepEntity step)
                {
                prepareFirst = step;
                }

            @Override
            public AbstractPlanningStepEntity getLast()
                {
                return prepareLast;
                }

            @Override
            public void setLast(final AbstractPlanningStepEntity step)
                {
                prepareLast = step;
                }
            };
        }
    
    @JoinColumn(name = "releasefirst", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity releaseFirst;

    @JoinColumn(name = "releaselast", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractPlanningStepEntity releaseLast;
    
    public PlanningStepSequenceImpl getReleaseList()
        {
        return new PlanningStepSequenceImpl()
            {
            @Override
            public AbstractPlanningStepEntity getFirst()
                {
                return releaseFirst;
                }

            @Override
            public void setFirst(final AbstractPlanningStepEntity step)
                {
                releaseFirst = step;
                }

            @Override
            public AbstractPlanningStepEntity getLast()
                {
                return releaseLast;
                }

            @Override
            public void setLast(final AbstractPlanningStepEntity step)
                {
                releaseLast = step;
                }
            };
        }

    protected IvoaComponentMetadata fillBean(final IvoaComponentMetadata bean)
        {
        bean.setUuid(
            this.getUuid()
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
        return bean ;
        }
    }
