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

import uk.co.metagrid.ambleck.datamodel.AbstractObject;

public class PingExecutable extends AbstractExecutable {

    public static final String TYPE_URL = "urn:ping-executable";

    public static final String DEFAULT_NAME = "Ping test";
    public static final String DEFAULT_TARGET = "localhost";

    public PingExecutable()
        {
        super(TYPE_URL, DEFAULT_NAME);
        }

    public PingExecutable(final String target)
        {
        this(target, DEFAULT_NAME);
        }

    public PingExecutable(final String target, final String name)
        {
        super(TYPE_URL, name);
        this.spec = new PingSpecific(
            target
            );
        }

    public static class PingSpecific extends AbstractObject.AbstractSpecific {

        public PingSpecific()
            {
            super();
            }

        public PingSpecific(final String target)
            {
            super();
            this.target = target ;
            }

        private String target ;
        public String getTarget()
            {
            return this.target;
            }
        public void setTarget(final String target)
            {
            this.target = target;
            }
        }

    private PingSpecific spec ;
    public PingSpecific getSpec()
        {
        return this.spec;
        }
    public void setSpec(final PingSpecific spec)
        {
        this.spec = spec;
        }
    }

