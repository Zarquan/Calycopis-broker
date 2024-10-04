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
package uk.co.metagrid.calycopis.offerset;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.execution.ExecutionFactory;
import uk.co.metagrid.calycopis.util.FactoryBaseImpl;

/**
 * Service for responding to OfferSet requests.
 *
 */
@Slf4j
@Component
public class OfferSetFactoryImpl
    extends FactoryBaseImpl
    implements OfferSetFactory
    {

    private final ExecutionFactory exefactory;
    
    private final OfferSetRepository repository;

    @Autowired
    public OfferSetFactoryImpl(final OfferSetRepository repository, final ExecutionFactory exefactory)
        {
        super();
        this.repository = repository;
        this.exefactory = exefactory;
        }

    /**
     * Select an OfferSet based on its identifier.
     *
     */
    @Override
    public Optional<OfferSetEntity> select(final UUID uuid)
		{
		Optional<OfferSetEntity> optional = this.repository.findById(
            uuid
            ); 
		if (optional.isPresent())
		    {
		    OfferSetEntity found = optional.get();
		    found.addMessage(
	            LevelEnum.DEBUG,
	            "urn:debug",
	            "OfferSetEntity select(UUID)",
	            Collections.emptyMap()
	            );
		    return Optional.of(
		         this.repository.save(
	                 found
	                 )
	            );
		    }
		else {
            return Optional.ofNullable(
                null
                );
		    }
		}

    /**
     * Create a new OfferSet based on an OfferSetRequest.
     *
     */
    @Override
    public OfferSetEntity create(final IvoaOfferSetRequest request)
    	{
    	OfferSetEntity created = new OfferSetEntity();
    	log.debug("created [{}]", created.getUuid());

    	OfferSetEntity saved = this.repository.save(created);
        log.debug("created [{}]", created.getUuid());
        log.debug("saved [{}]", saved.getUuid());

        for(int i = 0 ; i < 4 ; i++)
            {
            saved.getOffers().add(
                (ExecutionEntity) exefactory.create(
                    request,
                    saved
                    )
                );
            }
        
        return saved ;
    	}
    }

