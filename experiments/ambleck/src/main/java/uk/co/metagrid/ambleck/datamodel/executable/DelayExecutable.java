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
package uk.co.metagrid.ambleck.datamodel.executable;

public class DelayExecutable extends AbstractExecutable {

    public static final String TYPE_URL = "urn:delay-executable";

    public static final String  DEFAULT_NAME = "Delay test";
    public static final Integer DEFAULT_DURATION = new Integer(20);

    public DelayExecutable()
        {
        super(TYPE_URL, DEFAULT_NAME);
        }

    public DelayExecutable(final Integer duration)
        {
        this(duration, DEFAULT_NAME);
        }

    public DelayExecutable(final Integer duration, final String name)
        {
        super(TYPE_URL, name);
        this.spec = new DelaySpecific(
            duration
            ) ;
        }

    public static class DelaySpecific extends AbstractExecutable.Specific {

        public DelaySpecific()
            {
            super();
            }

        public DelaySpecific(final Integer duration)
            {
            super();
            this.duration = duration ;
            }

        private Integer duration ;
        public Integer getDuration()
            {
            return this.duration ;
            }
        public void setDuration(final Integer duration)
            {
            this.duration = duration ;
            }
        }

    private DelaySpecific spec ;
    public DelaySpecific getSpec()
        {
        return this.spec;
        }
    public void setSpec(final DelaySpecific spec)
        {
        this.spec = spec;
        }
    }

