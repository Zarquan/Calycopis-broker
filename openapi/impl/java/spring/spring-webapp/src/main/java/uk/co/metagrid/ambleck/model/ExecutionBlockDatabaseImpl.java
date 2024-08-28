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
    -- Create our database table.
    DROP TABLE IF EXISTS ExecutionBlocks;
    CREATE TABLE ExecutionBlocks(
        Ident INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        BlockStart  LONG,
        BlockLength LONG,
        MinCores INT,
        MaxCores INT,
        MinMemory INT,
        MaxMemory INT
        );
    SELECT * FROM ExecutionBlocks ;

 *
 */
package uk.co.metagrid.ambleck.model;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

//import java.sql.SQLException;
//import java.util.Optional;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import org.threeten.extra.Interval;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ExecutionBlockDatabaseImpl
    extends FactoryBase
    implements ExecutionBlockDatabase
    {

    private JdbcTemplate template;

    @Autowired
    public ExecutionBlockDatabaseImpl(final JdbcTemplate template)
        {
        super();
        this.template = template ;
        }

    /**
     * Insert an ExecutionBlock into our database.
     *
     */
    @Override
    public int insert(final ExecutionBlock block)
        {
        log.debug("INSERT [{}]", block.getOfferUuid());
        return template.update(
            "INSERT INTO ExecutionBlocks (BlockState, OfferUuid, ParentUuid, ExpiryTime, BlockStart, BlockLength, MinCores, MaxCores, MinMemory, MaxMemory) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            new Object[] {
                block.getState().toString(),
                block.getOfferUuid(),
                block.getParentUuid(),
                block.getExpiryTime(),
                block.getBlockStart(),
                block.getBlockLength(),
                block.getMinCores(),
                block.getMaxCores(),
                block.getMinMemory(),
                block.getMaxMemory()
                }
            );
        }

    /**
     * Select an ExecutionBlock from our database.
     *
     */
    public ExecutionBlock select(final UUID offeruuid)
        {
        String query =
            """
            SELECT * FROM ExecutionBlocks WHERE OfferUuid = :offeruuid
            """;
        return JdbcClient.create(template)
            .sql(query)
            .param("offeruuid", offeruuid.toString())
            .query(new ExecutionBlockMapper())
            .single();
        }

    /**
     * Accept an ExecutionBlock in our database.
     *
     */
    public int accept(final UUID offeruuid)
        {
        log.debug("Accept [{}]", offeruuid);
        int one = template.update(
            """
            UPDATE
                ExecutionBlocks
            SET
                BlockState = 'ACCEPTED',
                ExpiryTime = NULL
            WHERE
                OfferUuid = ?
            AND
                BlockState IN ('PROPOSED', 'OFFERED')
            ;
            UPDATE
                ExecutionBlocks
            SET
                BlockState = 'REJECTED'
            WHERE
                ParentUuid IN (
                    SELECT
                        ParentUuid
                    FROM
                        ExecutionBlocks
                    WHERE
                        OfferUuid = ?
                    )
            AND
                BlockState IN ('PROPOSED', 'OFFERED')
            """,
            new Object[] {
                offeruuid,
                offeruuid
                }
            );
        return one ;
        }

    /**
     * Reject an ExecutionBlock in our database.
     *
     */
    public int reject(final UUID offeruuid)
        {
        log.debug("Reject [{}]", offeruuid);
        return template.update(
            """
            UPDATE
                ExecutionBlocks
            SET
                BlockState = 'REJECTED'
            WHERE
                OfferUuid = ?
            AND
                BlockState IN ('PROPOSED', 'OFFERED')
            """,
            new Object[] {
                offeruuid
                }
            );
        }

    /**
     * Update any expired offers.
     *
     */
    @Override
    public int sweepUpdate(final Integer limit)
        {
        log.debug("Sweep [{}]", limit);
        return template.update(
            """
            UPDATE
                ExecutionBlocks
            SET
                BlockState = 'EXPIRED'
            WHERE
                Ident
            IN (
                SELECT
                    Ident
                FROM
                    ExecutionBlocks
                WHERE
                    BlockState IN ('PROPOSED', 'OFFERED')
                AND
                    (ExpiryTime < CURRENT_TIMESTAMP())
                ORDER BY
                    Ident
                LIMIT ?
                )
            """,
            new Object[] {
                ((limit != null) ? limit : Integer.valueOf(1))
                }
            );
        }

    /**
     * Delete expired offers.
     *
     */
    @Override
    public int sweepDelete(final Integer limit)
        {
        log.debug("Sweep [{}]", limit);
        return template.update(
            """
            DELETE FROM
                ExecutionBlocks
            WHERE
                Ident
            IN (
                SELECT
                    Ident
                FROM
                    ExecutionBlocks
                WHERE
                    BlockState IN ('EXPIRED', 'REJECTED')
                AND
                    (ExpiryTime < CURRENT_TIMESTAMP())
                ORDER BY
                    Ident
                LIMIT ?
                )
            """,
            new Object[] {
                ((limit != null) ? limit : Integer.valueOf(1))
                }
            );
        }


    /**
     * Generate a list of ExecutionBlock offers based on a ProcessingContext.
     *
     */
    @Override
    public List<ExecutionBlock> generate(final ProcessingContext context)
        {
        Interval starttime = null;
        Duration minduration  = null;
        Duration maxduration  = null;

        if (context.getExecutionTime() != null)
            {
            starttime   = context.getExecutionTime().getStartTime();
            minduration = context.getExecutionTime().getDuration();
            }

        if (starttime == null)
            {
            starttime = Interval.of​(
                Instant.now(),
                Duration.ofMinutes(5)
                );
            }

        if (minduration == null)
            {
            minduration = Duration.ofMinutes(30);
            }
        if (maxduration == null)
            {
            maxduration = Duration.ofSeconds(
                minduration.getSeconds()
                );
            }

        String query =
            """
            WITH ExpandedDataset AS
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
                'PROPOSED' AS BlockState,
                NULL AS OfferUuid,
                NULL AS ParentUuid,
                NULL AS ExpiryTime,
                BlockStart,
                BlockLength,
                MIN(FreeCores)  AS MinFreeCores,
                MIN(FreeMemory) AS MinFreeMemory
            FROM
                BlockResources
            GROUP BY
                BlockStart,
                BlockLength
            ORDER BY
                BlockStart  ASC,
                MinFreeCores  DESC,
                MinFreeMemory DESC,
                BlockLength DESC
            LIMIT 4
            """;

            query = query.replace(":totalcores",     String.valueOf(32));
            query = query.replace(":totalmemory",    String.valueOf(32));
            query = query.replace(":rangeoffset",    String.valueOf(
                starttime.getStart().getEpochSecond() / ExecutionBlock.BLOCK_STEP_SECONDS
                ));
            query = query.replace(":rangestart",     String.valueOf(1));
            query = query.replace(":rangeend",       String.valueOf(
                ((24 * 60) / ExecutionBlock.BLOCK_STEP_MINUTES)
                ));
            query = query.replace(":minfreecores",   String.valueOf(
                context.getMinCores()
                ));
            query = query.replace(":minfreememory",  String.valueOf(
                context.getMinMemory()
                ));
            query = query.replace(":minblocklength", String.valueOf(
                minduration.getSeconds() / ExecutionBlock.BLOCK_STEP_SECONDS
                ));
            query = query.replace(":maxblocklength", String.valueOf(
                maxduration.getSeconds() / ExecutionBlock.BLOCK_STEP_SECONDS
                ));

        List<ExecutionBlock> list = JdbcClient.create(template)
            .sql(query)
            .query(new ExecutionBlockMapper())
            .list();

/*
 * Filling in the template with named params like this caused a RuntimeException in the H2 database.
 *
            .param("totalcores",     new Integer(32))
            .param("totalmemory",    new Integer(32))
            .param("rangeoffset",    (starttime.getStart().getEpochSecond() / ExecutionBlock.BLOCK_STEP_SECONDS))
            .param("rangestart",     new Integer(0))
            .param("rangeend",       new Integer(23))
            .param("minfreecores",   context.getMinCores())
            .param("minfreememory",  context.getMinMemory())
            .param("minblocklength", (minduration.getSeconds() / ExecutionBlock.BLOCK_STEP_SECONDS))
            .param("maxblocklength", (maxduration.getSeconds() / ExecutionBlock.BLOCK_STEP_SECONDS))
 */

        return list ;
        }

    /**
     * Null safe translation between time formats.
     * Handles a null timestamp from the database.
     *
     */
    private static Instant timestampToInstant(final Timestamp timestamp)
        {
        if (timestamp != null)
            {
            return timestamp.toInstant();
            }
        else {
            return null ;
            }
        }

    public static class ExecutionBlockMapper implements RowMapper<ExecutionBlock>
        {
        @Override
        public ExecutionBlock mapRow(ResultSet rs, int rowNum)
        throws SQLException
            {
            try {
                ExecutionBlock block = new ExecutionBlockImpl(
                    rs.getString("BlockState"),
                    (UUID) rs.getObject("OfferUuid"),
                    (UUID) rs.getObject("ParentUuid"),
                    timestampToInstant(rs.getTimestamp("ExpiryTime")),
                    rs.getLong("BlockStart"),
                    rs.getLong("BlockLength"),
                    rs.getInt("MinFreeCores"),
                    rs.getInt("MinFreeCores"),
                    rs.getInt("MinFreeMemory"),
                    rs.getInt("MinFreeMemory")
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

