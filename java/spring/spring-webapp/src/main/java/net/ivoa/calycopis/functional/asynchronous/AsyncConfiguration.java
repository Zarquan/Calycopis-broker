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

package net.ivoa.calycopis.functional.asynchronous;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration bean for the Spring asynchronous services.
 * https://www.geeksforgeeks.org/advance-java/spring-boot-handling-background-tasks-with-spring-boot/
 * 
 */
@Slf4j
@Configuration
public class AsyncConfiguration
    {

    /**
     * 
     */
    public AsyncConfiguration()
        {
        super();
        log.info("AsyncConfiguration created");
        
        }

    // https://stackoverflow.com/a/45174389
    @Bean(name = "TaskScheduler-55")
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        return scheduler;
        }

    @Bean(name = "TaskExecutor-21")
    public Executor taskExecutor()
        {
        log.info("AsyncConfiguration checked");
        //return Executors.newWorkStealingPool(); // Limited number of threads 
        //return Executors.newVirtualThreadPerTaskExecutor(); // Database unavailable !
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;

        /*
         * Assume we don't need this any more.
         * Virtual threads are part of the standard JDK. 
        if (supportsVirtualThreads()) {
            // Configure for virtual threads if supported
            return Executors.newWorkStealingPool();
            }
        else {
            // Fall back to standard thread pool
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("AsyncThread-");
            executor.initialize();
            return executor;
            }
         * 
         */
        }    
    }
