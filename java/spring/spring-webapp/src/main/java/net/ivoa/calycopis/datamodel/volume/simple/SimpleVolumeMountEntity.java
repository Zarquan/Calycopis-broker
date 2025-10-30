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

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResource;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaSimpleVolumeMount;
import net.ivoa.calycopis.openapi.model.IvoaSimpleVolumeMount.ModeEnum;

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
        final SessionEntity session,
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
     *
     */
    public SimpleVolumeMountEntity(
        final SessionEntity session,
        final AbstractVolumeMountValidator.Result result,
        final IvoaSimpleVolumeMount validated
        ){
        super(
            session,
            validated.getName()
            );
        
        // TODO Add the fields ...
        }

    @Override
    public IvoaAbstractVolumeMount getIvoaBean(String baseurl)
        {
        // TODO Auto-generated method stub
        return null;
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



    }

