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

package net.ivoa.calycopis.functional.processing.data;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.functional.processing.ProcessingRequestEntity;

/**
 *
 */
@Slf4j
@Entity
@Table(
    name = "dataprocessingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class DataResourceProcessingRequestEntity
extends ProcessingRequestEntity
implements DataResourceProcessingRequest
    {

    protected DataResourceProcessingRequestEntity()
        {
        super();
        }

    protected DataResourceProcessingRequestEntity(final URI kind, final AbstractDataResourceEntity data)
        {
        super(kind);
        this.data = data;
        }

    @JoinColumn(name = "data", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AbstractDataResourceEntity data;

    @Override
    public AbstractDataResourceEntity getDataResource()
        {
        return this.data;
        }
    }

