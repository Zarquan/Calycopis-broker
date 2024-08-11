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
package uk.co.metagrid.ambleck.datamodel;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("response")
public class ResponseObject {

    public static enum ResultCode {
        YES,
        NO
        }

    public ResponseObject()
        {
        this(null, null);
        }

    public ResponseObject(final ResultCode result)
        {
        this(result, null);
        }

    public ResponseObject(final ResultCode result, final List<OfferObject> offers)
        {
        this.result = result;
        if (offers == null)
            {
            this.offers = new ArrayList<OfferObject>();
            }
        else {
            this.offers = offers;
            }
        }

    private ResultCode result ;
    public ResultCode getResult()
        {
        return this.result ;
        }
    public void setResult(final ResultCode result)
        {
        this.result = result;
        }

    private List<OfferObject> offers;
    public List<OfferObject> getOffers()
        {
        return this.offers;
        }
    public void setOffers(final List<OfferObject> offers)
        {
        this.offers = offers;
        }
    public void addOffer(final OfferObject offer)
        {
        this.offers.add(
            offer
            );
        }
    }

