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

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaJupyterNotebook;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.ambleck.message.WarnMessage;
import uk.co.metagrid.ambleck.model.OfferSetAPI;
import uk.co.metagrid.ambleck.model.ProcessingContextImpl;

@Slf4j
public class CanfarProcessingContextImpl
    extends ProcessingContextImpl<CanfarExecution>
    implements CanfarProcessingContext
    {

    private CanfarPreparationStepFactory stepory ;

    public CanfarProcessingContextImpl(final CanfarPreparationStepFactory stepory, final String baseurl, final IvoaOfferSetRequest request, final OfferSetAPI offerset, final CanfarExecution execution)
        {
        super(
            baseurl,
            request,
            offerset,
            execution
            );
        this.stepory = stepory;
        this.init();
        }

    protected void init()
        {
        stepory.createSpacePreparationStep(this.execution);
        }

    public void setExecutable(final IvoaAbstractExecutable executable)
        {
        super.setExecutable(executable);
        log.debug("setExecutable [{}][{}]", executable.getType(), executable.getUuid());
        switch(executable)
            {
            case IvoaJupyterNotebook config:
                stepory.createNotebookPreparationStep(
                    this.execution,
                    config
                    );
                break;

            default:
                this.addMessage(
                    new WarnMessage(
                        "Unknown executable type [${type}]",
                        Map.of(
                            "type",
                            executable.getType()
                            )
                        )
                    );
                this.fail();
                break;
            }
        }
    }

