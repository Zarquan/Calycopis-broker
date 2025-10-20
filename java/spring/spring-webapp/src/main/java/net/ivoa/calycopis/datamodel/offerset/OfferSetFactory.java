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
 * Based on
 * https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/
 * https://howtodoinjava.com/spring-boot/spring-boot-jparepository-example/
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

/**
 * Service for responding to OfferSet requests.
 *
 */
public interface OfferSetFactory
    extends FactoryBase
    {
    /**
     * The default expiry time for offers.
     *
     */
    public static final Long DEFAULT_EXPIRY_TIME_SECONDS = 5 * 60L ;

    /**
     * Select an OfferSet based on its identifier.
     *
     */
    public Optional<OfferSetEntity> select(final UUID uuid);

    /**
     * Create a new OfferSet based on an OfferSetRequest.
     *
     */
    public OfferSetEntity create(final IvoaOfferSetRequest request);

    }

