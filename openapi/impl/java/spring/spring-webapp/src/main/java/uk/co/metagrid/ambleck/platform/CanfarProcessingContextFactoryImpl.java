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

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.metagrid.ambleck.model.FactoryBase;
import uk.co.metagrid.ambleck.model.OfferSetAPI;
import uk.co.metagrid.ambleck.model.OfferSetRequest;
import uk.co.metagrid.ambleck.model.ExecutionResponseAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CanfarProcessingContextFactoryImpl
    extends FactoryBase
    implements CanfarProcessingContextFactory
    {

    private CanfarPreparationStepFactory stepory ;

    @Autowired
    public CanfarProcessingContextFactoryImpl(final CanfarPreparationStepFactory stepory)
        {
        this.stepory = stepory ;
        }

    public CanfarProcessingContext create(final String baseurl, final OfferSetRequest request, final OfferSetAPI offerset)
        {
        return new CanfarProcessingContextImpl(
            stepory,
            baseurl,
            request,
            offerset,
            new CanfarExecutionImpl(
                offerset
                )
            );
        }
    }

