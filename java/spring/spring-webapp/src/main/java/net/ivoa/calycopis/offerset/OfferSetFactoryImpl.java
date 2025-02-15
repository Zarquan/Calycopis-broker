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
package net.ivoa.calycopis.offerset;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;

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
     * The default expiry time for offers, in seconds.
     *
     */
    public static final int DEFAULT_EXPIRY_TIME_SECONDS = 5 * 60 ;

    private final OfferSetRequestParserFactory parserFactory;
    
    private final OfferSetRepository offersetRepository;

    @Autowired
    public OfferSetFactoryImpl(final OfferSetRepository offersetRepository, final OfferSetRequestParserFactory parserFactory)
        {
        super();
        this.offersetRepository = offersetRepository;
        this.parserFactory = parserFactory;
        }

    /**
     * Select an OfferSet based on its identifier.
     *
     */
    @Override
    public Optional<OfferSetEntity> select(final UUID uuid)
		{
        log.debug("select(UUID)");
        log.debug("UUID [{}]", uuid);
		return this.offersetRepository.findById(
            uuid
            ); 
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
                DEFAULT_EXPIRY_TIME_SECONDS
                )
	        );
    	log.debug("OfferSet [{}]", offerset.getUuid());

        if (save)
            {
            log.debug("save(OfferSet)");
            log.debug("OfferSet [{}]", offerset.getUuid());
            offerset = this.offersetRepository.save(offerset);
            log.debug("OfferSet [{}]", offerset.getUuid());
            for (ExecutionSessionEntity execution : offerset.getOffers())
                {
                log.debug("Execution [{}]", execution.getUuid());
                }
            }
    	
    	//
    	// Process the request and generate some offers.
    	final OfferSetRequestParser parser = parserFactory.create(); 
    	parser.process(
	        request,
	        offerset
	        );
    	//
    	// Save the offerset in the database. 
        if (save)
            {
            log.debug("save(OfferSet)");
            log.debug("OfferSet [{}]", offerset.getUuid());
            for (ExecutionSessionEntity execution : offerset.getOffers())
                {
                log.debug("Execution [{}]", execution.getUuid());
                }
            offerset = this.offersetRepository.save(offerset);
            log.debug("OfferSet [{}]", offerset.getUuid());
            for (ExecutionSessionEntity execution : offerset.getOffers())
                {
                log.debug("Execution [{}]", execution.getUuid());
                }
            }
        return offerset ;
    	}
    }

