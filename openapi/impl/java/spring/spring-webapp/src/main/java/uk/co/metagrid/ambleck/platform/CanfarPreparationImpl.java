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

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CanfarPreparationImpl
    extends PreparationBase<CanfarExecution>
    implements CanfarPreparationStep
    {

    public CanfarPreparationImpl(final CanfarExecution parent)
        {
        super(parent) ;
        this.setPrepCost(
            Duration.ofMinutes(5)
            );
        }


    @Override
    public void run()
        {
        String username = this.getParent().getUserName();
        String sessionid = this.getParent().getUuid().toString();
        StringBuilder builder = new StringBuilder();
        builder.append("/home/");
        builder.append(username);
        builder.append("/");
        builder.append(sessionid);
        this.getParent().setSessionHome(
            builder.toString()
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

