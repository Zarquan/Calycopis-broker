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
package net.ivoa.calycopis.datamodel.resource.volume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.resource.volume.simple.SimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.datamodel.resource.volume.simple.SimpleVolumeMountValidator;
import net.ivoa.calycopis.functional.validator.ValidatorFactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaAbstractVolumeMount;

/**
 * A factory for compute resource validators.
 *
 */
@Slf4j
@Component
public class AbstractVolumeMountValidatorFactoryImpl
    extends ValidatorFactoryBaseImpl<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
    implements AbstractVolumeMountValidatorFactory
    {

    /**
     * Public constructor, creates hard coded list of validators.
     * TODO Make this configurable.
     *
     */
    @Autowired
    public AbstractVolumeMountValidatorFactoryImpl(final SimpleVolumeMountEntityFactory simpleVolumeMountEntityFactory)
        {
        super();
        this.validators.add(
            new SimpleVolumeMountValidator(
                simpleVolumeMountEntityFactory
                )
            );
        }

    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractVolumeMount resource
        ){
        unknown(
            context,
            resource.getType(),
            context.makeVolumeValidatorResultKey(
                resource
                )
            );
        }
    }

