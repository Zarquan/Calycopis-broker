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
package uk.co.metagrid.ambleck.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.f4b6a3.uuid.UuidCreator;

import net.ivoa.calycopis.openapi.model.IvoaAbstractOption;
import net.ivoa.calycopis.openapi.model.IvoaEnumValueOption;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionResponse;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetLink;
import uk.co.metagrid.ambleck.platform.Execution;

public class ExecutionResponseImpl
    extends IvoaExecutionSessionResponse
    implements ExecutionResponseAPI
    {

    private OfferSetAPI parent;
    protected OfferSetAPI getParent()
        {
        return this.parent;
        }
    private Execution<?> execution;
    protected Execution<?> getExecution()
        {
        return this.execution ;
        }

    public ExecutionResponseImpl(final IvoaExecutionSessionStatus state, final String baseurl, final OfferSetAPI parent, final Execution<?> execution)
        {
        this.parent = parent ;
        this.execution = execution ;
        this.setState(
            state
            );
        this.setUuid(
            UuidCreator.getTimeBased()
            );
        this.setHref(
            baseurl + "/execution/" + this.getUuid()
            );
        this.created(
            OffsetDateTime.now()
            );
        this.expires(
            parent.getExpires()
            );

        IvoaOfferSetLink offerset = new IvoaOfferSetLink();
        this.setOfferset(
            offerset
            );
        offerset.setUuid(
            this.parent.getUuid()
            );
        offerset.setHref(
            this.parent.getHref()
            );

        this.updateOptions();

        }

    /**
     * Update the options based on the state.
     *
     */
    public void updateOptions()
        {
        List<IvoaAbstractOption> options = new ArrayList<IvoaAbstractOption>();
        this.setOptions(options);

        switch(this.getState())
            {
            case OFFERED:
                options.add(
                    new IvoaEnumValueOption(
                        List.of("ACCEPTED", "REJECTED"),
                        "urn:enum-value-option",
                        "state"
                        )
                    );
                break;

            case ACCEPTED:
            case PREPARING:
            case WAITING:
            case RUNNING:
                options.add(
                    new IvoaEnumValueOption(
                        List.of("CANCELLED"),
                        "urn:enum-value-option",
                        "state"
                        )
                    );
                break;
            default:
			    break;
            }
        }
    }

