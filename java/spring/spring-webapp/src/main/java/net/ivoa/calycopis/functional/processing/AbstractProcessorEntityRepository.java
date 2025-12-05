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

package net.ivoa.calycopis.functional.processing;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
public interface AbstractProcessorEntityRepository
extends JpaRepository<AbstractProcessorEntity, UUID>
    {

    //@Transactional
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT
            p.uuid
        FROM
            AbstractProcessorEntity p
        WHERE
            p.threadId = :threadId
        AND
            p.phase = ProcessorPhase.ACTIVE
        AND
            p.kind IN :kinds
        AND
            activation < CURRENT_TIMESTAMP
        ORDER BY
            p.activation ASC
        LIMIT 1
        """
            )
    public UUID selectNextActive(@Param("threadId") final UUID threadId, @Param("kinds") final List<URI> kinds) ;

    @Transactional
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying
    @Query(
        """
        UPDATE
            AbstractProcessorEntity p
        SET
            p.threadId = :threadId,
            p.phase = ProcessorPhase.ACTIVE
        WHERE p.uuid = (
            SELECT
                p.uuid
            FROM
                AbstractProcessorEntity p
            WHERE
                p.threadId IS NULL
            AND
                p.phase = ProcessorPhase.ACTIVE
            AND
                p.kind IN :kinds
            AND
                activation < CURRENT_TIMESTAMP
            ORDER BY
                p.activation ASC
            LIMIT 1
            )
        """
        )
    public int updateNextActive(@Param("threadId") final UUID threadId, @Param("kinds") final List<URI> kinds);
    
    }
