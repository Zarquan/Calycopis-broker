/**
 *
 */
package net.ivoa.calycopis.offers;

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
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import wtf.metio.storageunits.model.StorageUnit;
import wtf.metio.storageunits.model.StorageUnits;

/**
 *
 */
@Slf4j
@Component
public class OfferBlockFactoryImpl
    extends FactoryBaseImpl
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

    /**
     * The number of rows in each category to select.
     *
     */
    public static final int QUERY_ROW_LIMIT = 4;

    @Override
    public List<OfferBlock> generate(Interval requeststart, Duration requestduration, Long requestcores, Long requestmemory)
        {
        log.debug("generate(Interval, Duration, Long, Long");
        log.debug("Interval [{}]", requeststart);
        log.debug("Duration [{}]", requestduration);
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
                    Sessions.phase AS BlockPhase,
                    Sessions.StartInstantSec / :blockstep AS BlockStart,
                    Sessions.ExeDurationSec  / :blockstep AS BlockLength,
                    COALESCE(SimpleComputeResources.maxofferedcores,  SimpleComputeResources.maxrequestedcores)  AS UsedCores,
                    COALESCE(SimpleComputeResources.maxofferedmemory, SimpleComputeResources.maxrequestedmemory) AS UsedMemory
                FROM
                    Sessions
                JOIN
                    ComputeResources
                ON
                    ComputeResources.parent = Sessions.uuid
                JOIN
                    SimpleComputeResources
                ON
                    SimpleComputeResources.uuid = ComputeResources.uuid
                WHERE
                    Sessions.phase IN ('OFFERED', 'PREPARING', 'WAITING', 'RUNNING', 'RELEASING')
                ),
            AvailableBlocks AS
                (
                SELECT
                    StartRange.StartRow AS StartRow,
                    COUNT(ExecutionBlocks.BlockStart) AS RowCount,
                    (:totalcores  - IfNull(sum(ExecutionBlocks.UsedCores),  0)) AS FreeCores,
                    (:totalmemory - IfNull(sum(ExecutionBlocks.UsedMemory), 0)) AS FreeMemory
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
                    AvailableBlocks.StartRow,
                    (AvailableBlocks.StartRow + 1) -
                        (
                        ROW_NUMBER() OVER (
                            PARTITION BY (
                                AvailableBlocks.FreeCores  >= :mincores
                                AND
                                AvailableBlocks.FreeMemory >= :minmemory
                                )
                            ORDER BY AvailableBlocks.StartRow
                            )
                        ) AS BlockGroup,
                    FreeCores,
                    FreeMemory
                FROM
                    AvailableBlocks
                WHERE
                    AvailableBlocks.FreeCores  >= :mincores
                    AND
                    AvailableBlocks.FreeMemory >= :minmemory
                ),
            CombinedBlocks AS (
                SELECT
                    COUNT(*) AS BlockLength,
                    MIN(ConsecutiveBlocks.StartRow)   AS BlockStart,
                    MIN(ConsecutiveBlocks.FreeCores)  AS FreeCores,
                    MIN(ConsecutiveBlocks.FreeMemory) AS FreeMemory
                FROM
                    ConsecutiveBlocks
                WHERE
                    ConsecutiveBlocks.BlockGroup IS NOT NULL
                GROUP BY
                    ConsecutiveBlocks.BlockGroup
                HAVING
                    COUNT(*) >= :minblocklength
                ),
            SplitBlocks AS (
                SELECT
                    (CombinedBlocks.BlockStart + (:maxblocklength * (n - 1))) AS BlockStart,
                    LEAST(
                        :maxblocklength,
                        (CombinedBlocks.BlockLength - (:maxblocklength * (n - 1)))
                        ) AS BlockLength,
                    CombinedBlocks.FreeCores  AS FreeCores,
                    CombinedBlocks.FreeMemory AS FreeMemory
                FROM
                    CombinedBlocks,
                    (
                    SELECT
                        x AS n
                    FROM
                        SYSTEM_RANGE(1, :maxblocklength)
                    ) AS Numbers
                WHERE
                    (CombinedBlocks.BlockStart + (:maxblocklength * (n - 1))) < (BlockStart + BlockLength)
                ),
            MatchingBlocks AS (
                SELECT
                    AvailableBlocks.StartRow,
                    SplitBlocks.BlockStart,
                    SplitBlocks.BlockLength,
                    SplitBlocks.FreeCores,
                    SplitBlocks.FreeMemory
                FROM
                    AvailableBlocks
                JOIN
                    SplitBlocks
                WHERE
                    AvailableBlocks.StartRow >= SplitBlocks.BlockStart
                AND
                    AvailableBlocks.StartRow < (SplitBlocks.BlockStart + SplitBlocks.BlockLength)
                AND
                    SplitBlocks.BlockLength >= :minblocklength
                AND
                    SplitBlocks.BlockLength <= :maxblocklength
                ),
            GroupedBlocks AS (
                SELECT
                    MatchingBlocks.BlockStart,
                    MatchingBlocks.BlockLength,
                    MIN(MatchingBlocks.FreeCores)  AS FreeCores,
                    MIN(MatchingBlocks.FreeMemory) AS FreeMemory
                FROM
                    MatchingBlocks
                GROUP BY
                    MatchingBlocks.BlockStart,
                    MatchingBlocks.BlockLength
                ),
            ScaledBlocks AS (
                SELECT
                    GroupedBlocks.BlockStart,
                    GroupedBlocks.BlockLength,
                    LEAST(
                        :maxcores,
                        GroupedBlocks.FreeCores
                        ) AS BlockCores,
                    LEAST(
                        :maxmemory,
                        GroupedBlocks.FreeMemory
                        ) AS BlockMemory
                FROM
                    GroupedBlocks
                ),
            EarlyBlocks AS (
                SELECT
                    *
                FROM
                    ScaledBlocks
                ORDER BY
                    ScaledBlocks.BlockStart    ASC,
                    ScaledBlocks.BlockCores    DESC,
                    ScaledBlocks.BlockMemory   DESC,
                    ScaledBlocks.BlockLength   DESC
                LIMIT :querylimit
                ),
            HiMemBlocks AS (
                SELECT
                    *
                FROM
                    ScaledBlocks
                ORDER BY
                    ScaledBlocks.BlockMemory   DESC,
                    ScaledBlocks.BlockCores    DESC,
                    ScaledBlocks.BlockStart    ASC,
                    ScaledBlocks.BlockLength   DESC
                LIMIT :querylimit
                ),
            HiCpuBlocks AS (
                SELECT
                    *
                FROM
                    ScaledBlocks
                ORDER BY
                    ScaledBlocks.BlockCores    DESC,
                    ScaledBlocks.BlockMemory   DESC,
                    ScaledBlocks.BlockStart    ASC,
                    ScaledBlocks.BlockLength   DESC
                LIMIT :querylimit
                ),
            CombinedQuery AS (
                (
                SELECT
                    *
                FROM
                    EarlyBlocks
                )
            UNION
                (
                SELECT
                    *
                FROM
                    HiMemBlocks
                )
            UNION
                (
                SELECT
                    *
                FROM
                    HiCpuBlocks
                )
            )

            SELECT * FROM CombinedQuery

            """;

            query = query.replace(":blockstep",   String.valueOf(BLOCK_STEP_SECONDS));
            query = query.replace(":totalcores",  String.valueOf(TOTAL_AVAILABLE_CPU_CORES));
            query = query.replace(":totalmemory", String.valueOf(TOTAL_AVAILABLE_CPU_MEMORY.longValue()));
            query = query.replace(":rangeoffset", String.valueOf(
                requeststart.getStart().getEpochSecond() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":rangestart",  String.valueOf(1));
            query = query.replace(":rangeend",    String.valueOf(
                BLOCK_RANGE_SECONDS  / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":mincores",   String.valueOf(requestcores));
            query = query.replace(":minmemory",  String.valueOf(requestmemory));
            query = query.replace(":minblocklength", String.valueOf(
                requestduration.getSeconds() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":maxblocklength", String.valueOf(
                maxduration.getSeconds() / BLOCK_STEP_SECONDS
                ));

            query = query.replace(":maxcores", String.valueOf(
                requestcores * OFFER_CPU_CORES_SCALE
                ));
            query = query.replace(":maxmemory", String.valueOf(
                requestmemory * OFFER_CPU_MEMORY_SCALE
                ));
            query = query.replace(":querylimit", String.valueOf(
                QUERY_ROW_LIMIT
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
                    resultset.getLong("BlockCores"),
                    resultset.getLong("BlockMemory")
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
