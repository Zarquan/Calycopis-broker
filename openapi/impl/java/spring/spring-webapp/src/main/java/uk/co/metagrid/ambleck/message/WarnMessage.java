/*
 *
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

package uk.co.metagrid.ambleck.message;

import java.util.Map;

import uk.co.metagrid.ambleck.model.MessageItem;

public class WarnMessage extends MessageBase
    {
    public static final String DEFAULT_TYPE_URL = "https://example.org/message-types/warn" ;
    public WarnMessage(final String template, Map<String,String> params)
        {
        this(
            DEFAULT_TYPE_URL,
            template,
            params
            );
        }
    public WarnMessage(final String typeurl, final String template, Map<String,String> params)
        {
        super(
            MessageItem.LevelEnum.INFO,
            typeurl,
            template,
            params
            );
        }
    }


