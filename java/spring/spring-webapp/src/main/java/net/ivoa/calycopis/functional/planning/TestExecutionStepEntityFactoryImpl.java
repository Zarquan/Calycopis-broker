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

package net.ivoa.calycopis.functional.planning;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.SessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * Entity factory for TestExecutionSteps.
 * 
 */
@Slf4j
@Component
public class TestExecutionStepEntityFactoryImpl
extends FactoryBaseImpl
implements TestExecutionStepEntityFactory
    {

    private final TestExecutionStepEntityRepository repository;

    @Autowired
    public TestExecutionStepEntityFactoryImpl(final TestExecutionStepEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<TestExecutionStepEntity> select(UUID uuid)
        {
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public TestExecutionStepEntity create(
        final SessionEntity session,
        final ComponentEntity component,
        final Duration offset,
        final Duration duration,
        final String message
        ){
        return this.repository.save(
            new TestExecutionStepEntity(
                session,
                component,
                offset,
                duration,
                message
                )
            );
        }
    }
