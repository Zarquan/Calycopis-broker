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
package net.ivoa.calycopis.functional.validator;

import java.util.Map;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;

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
     * Non empty String.
     * https://english.stackexchange.com/questions/102771/not-empty-set-in-one-word
     * @return Either the trimmed String or null if the trimmed String is empty. 
     * 
     */
    public static String notEmpty(String string)
        {
        if (string != null)
            {
            string = string.trim();
            if (string.isEmpty())
                {
                return null;
                }
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

    /**
     * Test value to trigger a failure.
     * 
     */
    public static final String BAD_VALUE_TRIGGER = "bad-value-trigger";

    /**
     * Check for a bad value.
     * 
     */
    public boolean badValueCheck(
        final String requested,
        final OfferSetRequestParserContext context
        ){
        return badValueCheck(
            requested,
            BAD_VALUE_TRIGGER,
            context
            );
        }

    /**
     * Check for a bad value.
     * TODO Add the location of the bad value.
     * 
     */
    public boolean badValueCheck(
        final String requested,
        final String trigger,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;

        String value = notEmpty(
            requested
            );

        if (trigger.equals(value))
            {
            context.getOfferSetEntity().addWarning(
                "urn:test-trigger-failed",
                "Bad value detected [${value}]",
                Map.of(
                    "value",
                    value
                    )
                );
            success = false ;
            }
        
        return success ;
        }
    }
