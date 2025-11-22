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

package net.ivoa.calycopis.datamodel.volume.simple;

import java.net.URI;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResource;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.openapi.model.IvoaSimpleVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaSimpleVolumeMount.ModeEnum;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A SimpleVolumeMount Entity.
 *
 */
@Entity
@Table(
    name = "simplevolumemounts"
    )
@DiscriminatorValue(
    value="uri:simple-volume-mount"
    )
public class SimpleVolumeMountEntity
    extends AbstractVolumeMountEntity
    implements SimpleVolumeMount
    {
    @Override
    public URI getKind()
        {
        return SimpleVolumeMount.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleVolumeMountEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent and validator result.
     *
     */
    public SimpleVolumeMountEntity(
        final ExecutionSessionEntity session,
        final AbstractVolumeMountValidator.Result result
        ){
        this(
            session,
            result,
            (IvoaSimpleVolumeMount)result.getObject()
            );
        }
    /**
     * Protected constructor with parent and validator result.
     * TODO validated can be replaced by Result.getObject()
     * TODO No need to pass validated.getMeta() separately.
     *
     */
    public SimpleVolumeMountEntity(
        final ExecutionSessionEntity session,
        final AbstractVolumeMountValidator.Result result,
        final IvoaSimpleVolumeMount validated
        ){
        super(
            session,
            validated.getMeta()
            );
        
        // TODO Add the fields ...
        }

    private ModeEnum mode;
    @Override
    public ModeEnum getMode()
        {
        return this.mode;
        }

    private String path;
    @Override
    public String getPath()
        {
        return this.path;
        }

    @Override
    public List<AbstractDataResource> getDataResources()
        {
        // TODO Auto-generated method stub
        return null;
        }

    @Override
    public IvoaSimpleVolumeMount makeBean(final URIBuilder uribuilder)
        {
        return this.fillBean(
            new IvoaSimpleVolumeMount().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }
    
    protected IvoaSimpleVolumeMount fillBean(final IvoaSimpleVolumeMount bean)
        {
        super.fillBean(
            bean
            );
        return bean;
        }
    }

