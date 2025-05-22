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
package net.ivoa.calycopis.datamodel.offerset;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse.ResultEnum;

@Slf4j
@Entity
@Table(
    name = "offersets"
    )
@DiscriminatorValue(
    value = "uri:offerset"
    )
public class OfferSetEntity
extends ComponentEntity
    implements OfferSet
    {

    protected OfferSetEntity()
        {
        super();
        }

    protected OfferSetEntity(final String name, final String description, final OffsetDateTime created, final OffsetDateTime expires)
        {
        super(
            name,
            description,
            created
            );
        this.expires = expires;
        }

    @Column(name = "expires")
    private OffsetDateTime expires;

    @Override
    public OffsetDateTime getExpires()
        {
        return this.expires;
        }

    @Column(name = "result")
    private ResultEnum result;

    public ResultEnum getResult()
        {
        return this.result;
        }

    public void setResult(final ResultEnum result)
        {
        this.result = result;
        }

    /**
     * 
     * Hibernate JPA OneToMany relationship.
     * https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
     * https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/
     * 
     */
    @OneToMany(
        mappedBy = "offerset",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<ExecutionSessionEntity> executions = new ArrayList<ExecutionSessionEntity>();

    @Override
    public List<ExecutionSessionEntity> getOffers()
        {
        return executions ;
        }
 
    public void addExecutionSession(final ExecutionSessionEntity execution)
        {
        executions.add(execution);
        }
    }
