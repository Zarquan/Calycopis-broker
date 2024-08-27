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

import uk.co.metagrid.ambleck.model.OfferSetAPI;
import uk.co.metagrid.ambleck.model.ExecutionResponseAPI;

public class CanfarExecutionImpl
    extends ExecutionBase<CanfarPreparationStep>
    implements CanfarExecution
    {

    public CanfarExecutionImpl(final ExecutionResponseAPI response, final OfferSetAPI offerset)
        {
        super(
            response,
            offerset
            );
        }


    private String userName;
    public String getUserName()
        {
        return this.userName;
        }

    private String userHome;
    public String getUserHome()
        {
        return this.userHome;
        }

    private String sessionName;
    public String getSessionName()
        {
        return this.sessionName;
        }
    public void setSessionName(String name)
        {
        this.sessionName = name ;
        }

    private String sessionHome;
    public String getSessionHome()
        {
        return this.sessionHome;
        }
    public void setSessionHome(String path)
        {
        this.sessionHome = path ;
        }


//  public List<CanfarPreparationStep> getPreparationSteps();

    }

