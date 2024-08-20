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

import java.util.List;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import org.threeten.extra.Interval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ExecutionBlockDatabaseImpl implements ExecutionBlockDatabase
    {

    private JdbcTemplate template;

    @Autowired
    public ExecutionBlockDatabaseImpl(final JdbcTemplate template)
        {
        this.template = template ;
        }

    /**
     * Insert an ExecutionBlock into our database.
     *
     */
    @Override
    public int insert(final ExecutionBlock block)
        {
        return template.update(
            "INSERT INTO ExecutionBlocks (BlockStart, BlockLength, MinCores, MaxCores, MinMemory, MaxMemory) VALUES(?, ?, ?, ?, ?, ?)",
            new Object[] {
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
     * Generate a list of ExecutionBlock offers based on a ProcessingContext.
     *
     */
    @Override
    public List<ExecutionBlock> generate(final ProcessingContext context)
        {
        Interval starttime = null;
        Duration minduration = null;
        Duration maxduration = null;

        List<ProcessingContext.ScheduleItem> items = context.getScheduleItems();
        // This is where we only take the forst item in the list.
        if ((items != null) && (items.size() > 0))
            {
            starttime = items.get(0).getStartTime();
            minduration = items.get(0).getMinDuration();
            maxduration = items.get(0).getMaxDuration();
            }

        if (starttime == null)
            {
            starttime = Interval.of​(
                Instant.now(),
                Duration.ofMinutes(5)
                );
            }

        if ((minduration == null) && (maxduration == null))
            {
            minduration = Duration.ofMinutes(20);
            maxduration = Duration.ofMinutes(20);
            }
        if (minduration == null)
            {
            minduration = maxduration;
            }
        if (maxduration == null)
            {
            maxduration = minduration;
            }

        // This is where we truncate the Interval into an Instant.
        List<ExecutionBlock> list = new ArrayList<ExecutionBlock>();
        list.add(
            new ExecutionBlockImpl(
                starttime.getStart(),
                minduration,
                3,6,4,8
                )
            );
        list.add(
            new ExecutionBlockImpl(
                starttime.getStart(),
                minduration,
                6,12,8,16
                )
            );
        return list ;
        }

    }

