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
package net.ivoa.calycopis.validator.data;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;

/**
 * A validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleDataResourceValidator
implements DataResourceValidator
    {

    @Override
    public ResultEnum validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleDataResource simple:
                return validate(
                    simple,
                    state
                    );
            default:
                return ResultEnum.CONTINUE;
            }
        }

    /**
     * Validate a simple data resource.
     *
     */
    public ResultEnum validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        
        //
        // Check the data location.
        //
        
        //
        // Check for a storage location.
        //
        
        return ResultEnum.CONTINUE;
        }

    }
