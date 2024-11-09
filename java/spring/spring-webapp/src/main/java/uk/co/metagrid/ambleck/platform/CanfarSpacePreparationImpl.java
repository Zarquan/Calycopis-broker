/*
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
package uk.co.metagrid.ambleck.platform;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CanfarSpacePreparationImpl
    extends PreparationBase<CanfarExecution>
    implements CanfarSpacePreparationStep
    {

    public CanfarSpacePreparationImpl(final CanfarExecution parent)
        {
        super(parent);
        log.debug("constructor");
        }

    public String getUserName()
        {
        return this.getParent().getUserName();
        }

    public StringBuilder getUserHomeBuilder()
        {
        StringBuilder builder = new StringBuilder();
        builder.append("/home/");
        builder.append(this.getUserName());
        return builder;
        }

    public String getUserHome()
        {
        return this.getUserHomeBuilder().toString();
        }

    public String getSessionName()
        {
        return this.getParent().getUuid().toString();
        }

    public StringBuilder getSessionHomeBuilder()
        {
        StringBuilder builder = this.getUserHomeBuilder();
        builder.append("/");
        builder.append(this.getSessionName());
        return builder ;
        }

    public String getSessionHome()
        {
        return this.getSessionHomeBuilder().toString();
        }

    @Override
    public void run()
        {
        log.debug("run()");
        this.getParent().setSessionHome(
            this.getSessionHome()
            );
        // Call the VOSpace service to create session home.
        try {
            Thread.sleep(30);
            }
        catch (Exception ouch)
            {
            log.warn(
                "Sleep interrupted",
                ouch
                );
            }
        }
    }

