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

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.Component;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;

/**
 *
 */
@Table(
    name = "volumemounts"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public interface AbstractVolumeMount
    extends Component
    {
    /**
     * Get the parent ExecutionSession.
     *
     */
    public ExecutionSessionEntity getSession();

    /**
     * Get an IVOA bean representation.
     *
     */
    public IvoaAbstractVolumeMount getIvoaBean(final String baseurl);

    }
