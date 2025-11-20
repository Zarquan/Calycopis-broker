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

package net.ivoa.calycopis.datamodel.volume;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaComponentMetadata;

/**
 *
 */
@Entity
@Table(
    name = "abstractvolumemounts"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractVolumeMountEntity
extends ComponentEntity
implements AbstractVolumeMount
    {
    /**
     * Protected constructor.
     *
     */
    protected AbstractVolumeMountEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * Automatically adds this resource to the parent SessionEntity.
     *
     */
    protected AbstractVolumeMountEntity(
        final SessionEntity session,
        final IvoaComponentMetadata meta
        ){
        super(meta);
        this.session = session;
        session.addVolumeMount(
            this
            );
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SessionEntity session;

    @Override
    public SessionEntity getSession()
        {
        return this.session;
        }

    public abstract IvoaAbstractVolumeMount makeBean(final String baseurl);
    
    protected IvoaAbstractVolumeMount fillBean(final IvoaAbstractVolumeMount bean)
        {
        return bean;
        }
    }
