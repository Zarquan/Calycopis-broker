/**
 * 
 */
package uk.co.metagrid.calycopis.offers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import uk.co.metagrid.ambleck.model.FactoryBase;
import wtf.metio.storageunits.model.StorageUnit;
import wtf.metio.storageunits.model.StorageUnits;

/**
 * 
 */
@Slf4j
@Component
public class OfferBlockFactoryImpl
    extends FactoryBase
    implements OfferBlockFactory
    {

    private final JdbcTemplate template;

    public OfferBlockFactoryImpl(final JdbcTemplate template)
        {
        this.template = template;
        }
    
    // TODO Move all of these values to a PlatformConfiguration that can be shared with the OfferSetRequestParser.
    // TODO Perform the main verification checks in the OfferSetRequestParser.
    
    /**
     * The the granularity of time values in the database.
     * Set to 5 minute steps.
     *
     */
    public static final Long BLOCK_STEP_SECONDS  = 60L * 5L ;

    /**
     * How far to look ahead.
     * Set to 24 hours.
     * This should be set by the Duration of the requested start Interval. 
     *
     */
    public static final Long BLOCK_RANGE_SECONDS = 24L * 60L * 60L; 
    
    /**
     * The default start time range if none is specified in the request.
     * Set to 5 minutes.
     *  
     */
    public static final Duration DEFAULT_START_RANGE = Duration.ofMinutes(5);
    /**
     * The maximum start time range allowed.
     * Set to 24 hours.
     *  
     */
    public static final Duration MAXIMUM_START_RANGE  = Duration.ofHours(24);    

    /**
     * The default execution duration if none is specified in the request.
     * Set to 2 hours.
     *  
     */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(2);    
    /**
     * How much more time we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     *  
     */
    public static final int OFFER_DURATION_SCALE = 2;    
    /**
     * The maximum execution duration we are allowed to offer.
     * Set to 4 hours.
     *  
     */
    public static final Duration MAXIMUM_DURATION = Duration.ofHours(4);    
    
    /**
     * The default number of CPU cores if none is specified in the request.
     *  
     */
    public static final Long DEFAULT_CPU_CORES_REQUEST = 1L ;
    /**
     * The total number of CPU cores available on the platform.
     *  
     */
    public static final Long TOTAL_AVAILABLE_CPU_CORES = 32L ;
    /**
     * How many more CPU cores we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     *  
     */
    public static final int OFFER_CPU_CORES_SCALE = 2;    

    /**
     * The default amount of CPU memory if none is specified in the request.
     *  
     */
    public static final StorageUnit<?> DEFAULT_CPU_MEMORY_REQUEST = StorageUnits.gibibyte(1) ;
    /**
     * The total amount of CPU memory available on the platform.
     *  
     */
    public static final StorageUnit<?> TOTAL_AVAILABLE_CPU_MEMORY = StorageUnits.gibibyte(32);
    /**
     * How much more memory we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     *  
     */
    public static final int OFFER_CPU_MEMORY_SCALE = 2;    

    @Override
    public List<OfferBlock> other(Interval requeststart, Duration requestduration, Long requestcores, Long requestmemory)
        {
        // If no starttime, use the default. 
        if (requeststart == null)
            {
            requeststart = Interval.of(
                Instant.now(),
                DEFAULT_START_RANGE
                );
            }

        // If no startend, use the default. 
        if (requeststart.getEnd() == Instant.MAX)
            {
            requeststart = Interval.of(
                requeststart.getStart(),
                DEFAULT_START_RANGE
                );
            }

        // If no duration, use the default.
        if (requestduration == null)
            {
            requestduration = DEFAULT_DURATION;
            }
        
        if (requestcores == null)
            {
            requestcores = DEFAULT_CPU_CORES_REQUEST;
            }

        if (requestmemory == null)
            {
            requestmemory = DEFAULT_CPU_MEMORY_REQUEST.longValue();
            }
        
        // Calculate the maximum duration.
        Duration maxduration = Duration.ofSeconds(
            requestduration.getSeconds() * OFFER_DURATION_SCALE
            );
        if (maxduration.getSeconds() >= MAXIMUM_DURATION.getSeconds())
            {
            maxduration = MAXIMUM_DURATION;
            }
//--        
        String query =
            """
            WITH ExecutionBlocks AS
                (
                SELECT
                    Executions.State AS BlockState,
                    Executions.StartInstantSec / :blockstep AS BlockStart,
                    Executions.ExeDurationSec  / :blockstep AS BlockLength,
                    COALESCE(SimpleCompute.offeredcores,  SimpleCompute.requestedcores)  AS MinCores,
                    COALESCE(SimpleCompute.offeredmemory, SimpleCompute.requestedmemory) AS MinMemory
                FROM
                    Executions
                JOIN
                    SimpleCompute
                ON
                    SimpleCompute.parent = Executions.uuid
                WHERE
                    Executions.state IN ('OFFERED', 'PREPARING', 'WAITING', 'RUNNING', 'FINISHING')
                ),
            ExpandedDataset AS
                (
                SELECT
                    StartRange.StartRow AS StartRow,
                    COUNT(ExecutionBlocks.BlockStart) AS RowCount,
                    (:totalcores  - IfNull(sum(ExecutionBlocks.MinCores),  0)) AS FreeCores,
                    (:totalmemory - IfNull(sum(ExecutionBlocks.MinMemory), 0)) AS FreeMemory
                FROM
                    (
                    SELECT
                        x + :rangeoffset AS StartRow
                    FROM
                        SYSTEM_RANGE(:rangestart, :rangeend)
                    ) AS StartRange
                LEFT OUTER JOIN
                    ExecutionBlocks
                ON  (
                        (ExecutionBlocks.BlockStart <= StartRange.StartRow)
                        AND
                        ((ExecutionBlocks.BlockStart + ExecutionBlocks.BlockLength) > StartRange.StartRow)
                        )
                GROUP BY
                    StartRange.StartRow
                ),
            ConsecutiveBlocks AS (
                SELECT
                    StartRow,
                    (StartRow + 1) -
                        (
                        ROW_NUMBER() OVER (
                            PARTITION BY (FreeCores >= :minfreecores AND FreeMemory >= :minfreememory)
                            ORDER BY StartRow
                            )
                        ) AS BlockGroup,
                    FreeCores,
                    FreeMemory
                FROM
                    ExpandedDataset
                WHERE
                    FreeCores >= :minfreecores
                    AND
                    FreeMemory >= :minfreememory
                ),
            BlockInfo AS (
                SELECT
                    MIN(StartRow) AS BlockStart,
                    COUNT(*) AS BlockLength,
                    MIN(FreeCores) AS MaxFreeCores,
                    MIN(FreeMemory) AS MaxFreeMemory
                FROM
                    ConsecutiveBlocks
                WHERE
                    BlockGroup IS NOT NULL
                GROUP BY
                    BlockGroup
                HAVING
                    COUNT(*) >= :minblocklength
                ),
            SplitBlocks AS (
                SELECT
                    BlockStart + :maxblocklength * (n - 1) AS BlockStart,
                    LEAST(
                        :maxblocklength,
                        BlockLength - :maxblocklength * (n - 1)
                        ) AS BlockLength,
                    MaxFreeCores,
                    MaxFreeMemory
                FROM
                    BlockInfo,
                    (
                    SELECT
                        x AS n
                    FROM
                        SYSTEM_RANGE(1, :maxblocklength)
                    ) AS Numbers
                WHERE
                    BlockStart + :maxblocklength * (n - 1) < BlockStart + BlockLength
                ),
            BlockResources AS (
                SELECT
                    BlockStart,
                    BlockLength,
                    StartRow,
                    FreeCores,
                    FreeMemory
                FROM
                    ExpandedDataset
                JOIN
                    SplitBlocks
                WHERE
                    StartRow >= BlockStart
                AND
                    StartRow < (BlockStart + BlockLength)
                AND
                    BlockLength >= :minblocklength
                AND
                    BlockLength <= :maxblocklength
                )
            SELECT
                BlockStart,
                BlockLength,
                MIN(FreeCores)  AS FreeCores,
                MIN(FreeMemory) AS FreeMemory
            FROM
                BlockResources
            GROUP BY
                BlockStart,
                BlockLength
            ORDER BY
                BlockStart  ASC,
                FreeCores  DESC,
                FreeMemory DESC,
                BlockLength DESC
            LIMIT 4
            """;

            query = query.replace(":blockstep",      String.valueOf(BLOCK_STEP_SECONDS));
            query = query.replace(":totalcores",     String.valueOf(TOTAL_AVAILABLE_CPU_CORES));
            query = query.replace(":totalmemory",    String.valueOf(
                TOTAL_AVAILABLE_CPU_MEMORY.longValue()
                ));
            query = query.replace(":rangeoffset",    String.valueOf(
                requeststart.getStart().getEpochSecond() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":rangestart",     String.valueOf(1));
            query = query.replace(":rangeend",       String.valueOf(
                BLOCK_RANGE_SECONDS  / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":minfreecores",   String.valueOf(
                requestcores
                ));
            query = query.replace(":minfreememory",  String.valueOf(
                requestmemory
                ));
            query = query.replace(":minblocklength", String.valueOf(
                requestduration.getSeconds() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":maxblocklength", String.valueOf(
                maxduration.getSeconds() / BLOCK_STEP_SECONDS
                ));
//--
        log.debug("Running query ...");
        log.debug(query);
        List<OfferBlock> list = JdbcClient.create(template)
            .sql(query)
            .query(new OfferBlockMapper())
            .list();
        log.debug("Count [{}]", list.size());
        return list;
        }

    public static class OfferBlockMapper implements RowMapper<OfferBlock>
        {
        @Override
        public OfferBlock mapRow(ResultSet resultset, int rownumber)
        throws SQLException
            {
            log.debug("mapRow(ResultSet, int)");
            log.debug("Row number [{}]", rownumber);
            try {
                OfferBlock block = new OfferBlockImpl(
                    Instant.ofEpochSecond(
                        resultset.getLong("BlockStart") * BLOCK_STEP_SECONDS
                        ),
                    Duration.ofSeconds(
                        resultset.getLong("BlockLength") * BLOCK_STEP_SECONDS
                        ),
                    resultset.getLong("FreeCores"),
                    resultset.getLong("FreeMemory")
                    );
                return block;
                }
            catch (IllegalArgumentException ouch)
                {
                throw new SQLException(
                    ouch
                    );
                }
            }
        }
    }
