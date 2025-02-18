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

import java.util.UUID;

/**
 * Base class for Validatior implementations.
 * Provides a set of tools.
 *  
 */
public class ValidatorTools
    {

    /**
     * Null-safe String trim.
     *
     */
    public static String trim(String string)
        {
        if (string != null)
            {
            string = string.trim();
            }
        return string;
        }

    /**
     * Null-safe UUID toString.
     * 
     */
    public String string(final UUID uuid)
        {
        if (null != uuid)
            {
            return uuid.toString();
            }
        else {
            return null ;
            }
        }
    
    }
