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
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data.skao.mock;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.skao.SkaoDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;

/**
 * 
 */
@Entity
@Table(
    name = "mockskaodataresources"
    )
public class MockSkaoDataResourceEntity
    extends SkaoDataResourceEntity
    implements MockSkaoDataResource
    {

    /**
     * 
     */
    public MockSkaoDataResourceEntity()
        {
        super();
        }

    /**
     *
     */
    public MockSkaoDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        super(
            session,
            storage,
            result
            );
        }
    }
