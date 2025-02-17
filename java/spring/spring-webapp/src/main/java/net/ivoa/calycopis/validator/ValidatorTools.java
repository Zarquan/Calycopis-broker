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
package net.ivoa.calycopis.validator;

import net.ivoa.calycopis.validator.Validator.Result;
import net.ivoa.calycopis.validator.Validator.ResultBean;
import net.ivoa.calycopis.validator.Validator.ResultEnum;

/**
 * Base class for Validatior implementations.
 * Provides a set of tools.
 *  
 */
public class ValidatorTools<ObjectType, EntityType>
    {

    /**
     * Trim a String value, skipping it if it is null.
     *
     */
    public static String trimString(String string)
        {
        if (string != null)
            {
            string = string.trim();
            }
        return string;
        }

    /**
     * Create an ACCEPTED result.
     * 
     */
    public Result<ObjectType, EntityType> acceptResult(
        final ObjectType resultObject
        ){
        return wrapResult(
            ResultEnum.ACCEPTED,
            resultObject
            );
        }

    /**
     * Create a CONTINUE result.
     * 
     */
    public Result<ObjectType, EntityType> continueResult()
        {
        return wrapResult(
            ResultEnum.CONTINUE,
            null
            );
        }

    /**
     * Create a FAILED result.
     * 
     */
    public Result<ObjectType, EntityType> failResult()
        {
        return wrapResult(
            ResultEnum.FAILED,
            null
            );
        }
    
    /**
     * Wrap a result.
     * 
     */
    public Result<ObjectType, EntityType> wrapResult(
        final ResultEnum resultEnum,
        final ObjectType resultObject
        ){
        return new ResultBean<ObjectType, EntityType>(
            resultEnum,
            resultObject
            );
        }
    }
