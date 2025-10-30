/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
package net.ivoa.calycopis.datamodel.data.skao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaIvoaObsCoreItem;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaSkaoReplicaItem;

/**
 * A Validator implementation to handle IvoaDataResources.
 *
 */
@Slf4j
public class SkaoDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements SkaoDataResourceValidator
    {
    /**
     * Our Spring database template.
     * 
     */
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Factory for creating Entities.
     *
     */
    final SkaoDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     *
     */
    public SkaoDataResourceValidatorImpl(
        final JdbcTemplate jdbcTemplate,
        final SkaoDataResourceEntityFactory entityFactory,
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        super(
            storageValidators
            );
        this.jdbcTemplate  = jdbcTemplate  ;
        this.entityFactory = entityFactory ;
        }

    @Override
    public AbstractDataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", context.makeDataValidatorResultKey(requested), requested.getClass().getName());
        if (requested instanceof IvoaSkaoDataResource)
            {
            return validate(
                (IvoaSkaoDataResource) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    public AbstractDataResourceValidator.Result validate(
        final IvoaSkaoDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSkaoDataResource)");
        log.debug("Resource [{}]", context.makeDataValidatorResultKey(requested));

        boolean success = true ;

        IvoaSkaoDataResource validated = new IvoaSkaoDataResource(
            SkaoDataResource.TYPE_DISCRIMINATOR
            );

        success &= duplicateCheck(
            requested,
            context
            );

        AbstractStorageResourceValidator.Result storage = storageCheck(
            requested,
            validated,
            context
            );
        success &= ResultEnum.ACCEPTED.equals(storage.getEnum());
                
        validated.setUuid(
            requested.getUuid()
            );
        validated.setName(
            trim(
                requested.getName()
                )
            );
        
        //
        // Validate the IvoaIvoaDataResourceBlock
        success &= validate(
            requested.getIvoa(),
            validated,
            context
            );

        //
        // Validate the IvoaSkaoDataResourceBlock
        success &= validate(
            requested.getSkao(),
            validated,
            context
            );

        //
        // Calculate the preparation time.
        /*
         * 
        validated.setSchedule(
            new IvoaComponentSchedule()
            );
        success &= setPrepareDuration(
            context,
            validated.getSchedule(),
            this.predictPrepareTime(
                validated
                )
            );
         * 
         */
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            //
            // Create a new validator Result.
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public SkaoDataResourceEntity build(final SessionEntity session)
                    {
                    return entityFactory.create(
                        session,
                        storage.getEntity(),
                        this
                        );
                    }

                @Override
                public Long getPreparationTime()
                    {
                    return predictPrepareTime(
                        validated
                        );
                    }
                };
            //
            // Add our Result to our context.
            context.addDataValidatorResult(
                dataResult
                );
            //
            // Add the DataResource to the StorageResource.
            storage.addDataResourceResult(
                dataResult
                );
            return dataResult ;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }

    // Inherit this from Ivoa validator ?
    public boolean validate(
        final IvoaIvoaDataResourceBlock requested,
        final IvoaIvoaDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        if (requested != null)
            {
            IvoaIvoaDataResourceBlock block = new IvoaIvoaDataResourceBlock();
            validated.setIvoa(block);
            
            IvoaIvoaObsCoreItem obscore = requested.getObscore();
            if (null != obscore)
                {
                // TODO process the ObsCore data.
                block.setObscore(obscore);
                }

            IvoaIvoaDataLinkItem datalink = requested.getDatalink();
            if (null != datalink)
                {
                // TODO process the DataLink data.
                block.setDatalink(datalink);
                }
            }
        return success ;
        }

    public boolean validate(
        final IvoaSkaoDataResourceBlock requested,
        final IvoaSkaoDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        if (requested != null)
            {
            IvoaSkaoDataResourceBlock block = new IvoaSkaoDataResourceBlock();
            validated.setSkao(block);
            // TODO Validate the values.
            block.setNamespace(requested.getNamespace());
            block.setObjectname(requested.getObjectname());
            block.setObjecttype(requested.getObjecttype());
            block.setDatasize(requested.getDatasize());
            block.setChecksum(requested.getChecksum());

            for (IvoaSkaoReplicaItem replica : requested.getReplicas())
                {
                // TODO Validate the fields
                IvoaSkaoReplicaItem newReplica = new IvoaSkaoReplicaItem();
                newReplica.setRsename(replica.getRsename());
                newReplica.setDataurl(replica.getDataurl());
                block.addReplicasItem(
                    newReplica
                    );
                }
            }
        return success ;
        }

    /*
     * TODO This will be platform dependent.
     * Different PrepareData implementations will have different preparation times.
     * Some will just symlink the Rucio data, others will have an additional copy operation.
     * Alternatively we could offload all of this to the local PrepareData service ? 
     * 
     */
    private Long predictPrepareTime(final IvoaSkaoDataResource validated)
        {
        log.debug("predictPrepareTime()");

        IvoaSkaoDataResourceBlock skaoBlock = validated.getSkao();
        if (null == skaoBlock)
            {
            log.error("Null IvoaSkaoDataResourceBlock");
            return null ;
            }
        
        StringBuilder replicaList = new StringBuilder();
        for (IvoaSkaoReplicaItem replica : skaoBlock.getReplicas())
            {
            if (false == replicaList.isEmpty())
                {
                replicaList.append(", ");
                }
            replicaList.append("'");
            replicaList.append(
                replica.getRsename()
                );
            replicaList.append("'");
            }
        log.debug("replicaList [{}]", replicaList);
        
        String replicaQuery = "SELECT source, seconds FROM TransferTimes WHERE source IN (:replicalist:) ORDER BY seconds";
        replicaQuery = replicaQuery.replace(":replicalist:", replicaList); 
        log.debug("replicaQuery [{}]", replicaQuery);

        List<TransferRateRecord> list = JdbcClient.create(jdbcTemplate)
            .sql(replicaQuery)
            .query(new TransferTimeMapper())
            .list();
        
        if (list.isEmpty())
            {
            log.error("No TransferTimes found");
            return null;
            }

        TransferRateRecord transferRateRecord = list.getFirst();
        log.debug("TransferTimeCost [{}][{}]", transferRateRecord.getSource(), transferRateRecord.getSeconds());

        Long transferRate = transferRateRecord.getSeconds();
        if (null == transferRate)
            {
            log.error("Null TransferTime seconds");
            return null;
            }

        final Long GIGABYTE = 1024L * 1024L * 1024 ;

        Long dataSize = skaoBlock.getDatasize();
        if (dataSize > GIGABYTE)
            {
            dataSize /= GIGABYTE ;
            }
        else {
            dataSize = 1L ;
            }
        Long transferTime = transferRate * dataSize ;

        log.debug("Data size (GB) [{}]", dataSize);
        log.debug("Transfer rate (s/GB) [{}]", transferRate);
        log.debug("Transfer time (s) [{}]", transferTime);

        String delayQuery = "SELECT seconds FROM ResponseTimes WHERE service = 'rucio.replica.delay' ORDER BY seconds";
        Object object = JdbcClient.create(jdbcTemplate)
            .sql(delayQuery)
            .query()
            .singleValue();
        if (object instanceof Long)
            {
            Long serviceDelay = (Long) object ; 
            transferTime += serviceDelay ;
            log.debug("Service delay (s) [{}]", serviceDelay);
            log.debug("Transfer time (s) [{}]", transferTime);
            }
        return transferTime;
        }

    static class TransferRateRecord
        {
        protected TransferRateRecord(final String source, final Long seconds )
            {
            this.source  = source;
            this.seconds = seconds;
            }
        private final String source ;
        public String getSource()
            {
            return this.source;
            }
        private final Long seconds;
        public Long getSeconds()
            {
            return this.seconds;
            }
        }
    
    static class TransferTimeMapper
    implements RowMapper<TransferRateRecord>
        {
        @Override
        public TransferRateRecord mapRow(ResultSet resultSet, int rowNum)
        throws SQLException
            {
            return new TransferRateRecord(
                resultSet.getString("source"), 
                resultSet.getLong("seconds")
                );
            }
        }
    }
