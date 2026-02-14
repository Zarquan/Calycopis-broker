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

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;

/**
 * Public interface for VolumeMount validators and results.
 *
 */
public interface AbstractVolumeMountValidator
extends Validator<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
    {

    /**
     * Public interface for a validator result.
     *
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
        {
        /**
         * Build an entity based on a validation result.
         *
         */
        public AbstractVolumeMountEntity build(final SimpleExecutionSessionEntity session);
        }

    /**
     * Validate a component.
     *
     */
    public void validate(
        final IvoaAbstractVolumeMount requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Simple Bean implementation of a VolumeMountValidator result.
     *
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
    implements Result
        {
        /**
         * Public constructor.
         *
         */
        public ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor.
         *
         */
        public ResultBean(
            final ResultEnum result,
            final IvoaAbstractVolumeMount object
            ){
            super(
                result,
                object
                );
            }

        @Override
        // Here because we need to create Results with just a status and no entity
        public AbstractVolumeMountEntity build(SimpleExecutionSessionEntity session)
            {
            return null;
            }
        }
    }
