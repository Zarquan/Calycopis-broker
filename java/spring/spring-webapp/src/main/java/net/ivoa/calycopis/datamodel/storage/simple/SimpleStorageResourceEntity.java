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

package net.ivoa.calycopis.datamodel.storage.simple;

import java.net.URI;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSession;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A SimpleStorageResource Entity.
 *
 */
@Entity
@Table(
    name = "simplestorageresources"
    )
@DiscriminatorValue(
    value="uri:simple-storage-resource"
    )
public class SimpleStorageResourceEntity
    extends AbstractStorageResourceEntity
    implements SimpleStorageResource
    {
    @Override
    public URI getKind()
        {
        return SimpleExecutionSession.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleStorageResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent and validator result.
     *
     */
    protected SimpleStorageResourceEntity(
        final AbstractExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        this(
            session,
            result,
            (IvoaSimpleStorageResource)result.getObject()
            );
        }
    
    /**
     * Protected constructor with parent and validator result.
     * TODO validated can be replaced by Result.getObject()
     * TODO No need to pass validated.getMeta() separately.
     *
     */
    protected SimpleStorageResourceEntity(
        final AbstractExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result,
        final IvoaSimpleStorageResource validated
        ){
        super(
            session,
            result,
            validated.getMeta()
            );

        // TODO Add the storage fields ...
        
        }

    @Override
    public IvoaAbstractStorageResource makeBean(URIBuilder uribuilder)
        {
        return fillBean(
            new IvoaSimpleStorageResource().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }

    protected IvoaSimpleStorageResource fillBean(final IvoaSimpleStorageResource bean)
        {
        super.fillBean(bean);
        //
        // Add the storage properties.
        //
        return bean;
        }
    }

