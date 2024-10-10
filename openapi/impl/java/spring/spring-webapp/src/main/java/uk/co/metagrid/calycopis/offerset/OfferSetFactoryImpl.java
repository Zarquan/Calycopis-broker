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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.calycopis.processing.NewProcessingContext;
import uk.co.metagrid.calycopis.processing.NewProcessingContextFactory;
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
    /**
     * The default expiry time for offers, in minutes.
     *
     */
    public static final int DEFAULT_EXPIRY_TIME = 5 ;

    private final NewProcessingContextFactory contexts;

    private final OfferSetRepository repository;

    @Autowired
    public OfferSetFactoryImpl(final OfferSetRepository repository, final NewProcessingContextFactory contexts)
        {
        super();
        this.contexts = contexts;
        this.repository = repository;
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

    @Override
    public OfferSetEntity create(final IvoaOfferSetRequest request)
        {
        return this.create(
            request,
            true
            );
        }
    
    @Override
    public OfferSetEntity create(final IvoaOfferSetRequest request, boolean save)
    	{
    	OfferSetEntity offerset = new OfferSetEntity(
	        request.getName(),
	        OffsetDateTime.now(),
	        OffsetDateTime.now().plusMinutes(
                DEFAULT_EXPIRY_TIME
                )
	        );
    	log.debug("offerset [{}]", offerset.getUuid());
    	//
    	// Process the request to generate some offers.
    	final NewProcessingContext context = contexts.create();
    	context.process(
	        request,
	        offerset
	        );
    	//
    	// Save the offerset in the database. 
        if (save)
            {
            offerset= this.repository.save(offerset);
            log.debug("offerset [{}]", offerset.getUuid());
            }
    	
/*
 * 
        for(int i = 0 ; i < 4 ; i++)
            {
            created.getOffers().add(
                (ExecutionEntity) exefactory.create(
                    request,
                    created,
                    save
                    )
                );
            }
 *      
 */
        
        return offerset ;
    	}
    }

