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
package net.ivoa.calycopis.validator.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;

/**
 * A factory for storage resource validators.
 * 
 */
@Component
public class StorageResourceValidatorFactoryImpl
    extends FactoryBaseImpl
    implements StorageResourceValidatorFactory
    {
    /**
     * Our list of StorageValidators.
     * TODO Make this configurable ...
     * 
     */
    private List<StorageResourceValidator> validators = new ArrayList<StorageResourceValidator>();
        {
        validators.add(
            new SimpleStorageResourceValidator()
            );
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserState state
        ){
        ResultEnum result = null ; 
        //
        // Try each of the validators in our list.
        for (StorageResourceValidator validator : validators)
            {
            result = validator.validate(
                requested,
                state
                );
            if (result != ResultEnum.CONTINUE)
                {
                break ; 
                }
            }
        //
        // If we didn't find a matching validator, add a warning and fail the validation.
        if (result == ResultEnum.CONTINUE)
            {
            state.getOfferSetEntity().addWarning(
                "urn:unknown-resource-type",
                "Unknown resource type [${type}][${class}]",
                Map.of(
                    "type",
                    requested.getType(),
                    "class",
                    requested.getClass().getName()
                    )
                );
            state.valid(false);
            result = ResultEnum.FAILED;
            }
        return result;
        }
    }
