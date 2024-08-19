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
    DROP TABLE IF EXISTS ExecutionBlock;
    CREATE TABLE ExecutionBlock(
        Ident INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        Start  LONG,
        Length LONG,
        MinCores INT,
        MaxCores INT,
        MinMemory INT,
        MaxMemory INT
        );
    SELECT * FROM ExecutionBlock ;

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
            "INSERT INTO ExecutionBlock (Start, Length, MinCores, MaxCores, MinMemory, MaxMemory) VALUES(?, ?, ?, ?, ?, ?)",
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

        long starttime = InstantSource.tick(
            InstantSource.system(),
            Duration.ofSeconds(1)
            )
            .instant()
            .getEpochSecond();

        starttime = starttime / 60 ;

        //long duration = Duration.ofMinutes(5).getSeconds();
        long duration = 5;

        List<ExecutionBlock> list = new ArrayList<ExecutionBlock>();
        list.add(
            new ExecutionBlockImpl(
                starttime,
                duration,
                3,6,4,8
                )
            );
        list.add(
            new ExecutionBlockImpl(
                starttime,
                duration,
                6,12,8,16
                )
            );
        return list ;
        }

    }

