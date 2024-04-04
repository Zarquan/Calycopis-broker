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
 */
package uk.co.metagrid.ambleck.datamodel.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GenericMinMaxPair<Type> {

    public GenericMinMaxPair()
        {
        }

    public GenericMinMaxPair(final Type min, final Type max)
        {
        this.min = min;
        this.max = max;
        }

    private Type min ;
    public Type getMin()
        {
        return this.min ;
        }
    public void setMin(final Type min)
        {
        this.min = min;
        }
    private Type max ;
    public Type getMax()
        {
        return this.max ;
        }
    public void setMax(final Type max)
        {
        this.max = max;
        }
    }



