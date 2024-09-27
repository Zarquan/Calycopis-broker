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
import java.time.OffsetDateTime;

import org.apache.commons.text.StringSubstitutor;

import net.ivoa.calycopis.openapi.model.IvoaMessageItem;

public class MessageBase extends IvoaMessageItem
    {
    public MessageBase(final IvoaMessageItem.LevelEnum level, final String type, final String template, Map<String,String> params)
        {
        super();
        this.time(
            OffsetDateTime.now()
            );
        this.level(
            level
            );
        this.type(
            type
            );
        this.template(
            template
            );
        params.forEach(
            (key,value) -> this.putValuesItem(
                key,
                value
                )
            );
        this.message(
            "[" + level + "] " +
            new StringSubstitutor(
                params
                ).replace(
                    template
                    )
            );
        }
    }


