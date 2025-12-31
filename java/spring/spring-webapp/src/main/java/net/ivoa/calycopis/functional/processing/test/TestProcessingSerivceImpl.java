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

package net.ivoa.calycopis.functional.processing.test;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.ProcessingRequestEntity;
import net.ivoa.calycopis.functional.processing.ProcessingService;
import net.ivoa.calycopis.functional.processing.ProcessingServiceImpl;
import net.ivoa.calycopis.functional.processing.compute.PrepareComputeResourceRequest;
import net.ivoa.calycopis.functional.processing.data.PrepareDataResourceRequest;
import net.ivoa.calycopis.functional.processing.executable.PrepareExecutableRequest;
import net.ivoa.calycopis.functional.processing.session.PrepareSessionRequest;
import net.ivoa.calycopis.functional.processing.session.SessionAvailableRequest;
import net.ivoa.calycopis.functional.processing.storage.PrepareStorageResourceRequest;

/**
 * 
 */
@Slf4j
@Service
public class TestProcessingSerivceImpl
extends ProcessingServiceImpl
implements ProcessingService
    {

    private final Platform platform;

    @Autowired
    public TestProcessingSerivceImpl(final Platform platform)
        {
        super();
        this.platform = platform;
        }

    @Override
    public List<URI> getKinds()
        {
        return List.of(
            PrepareSessionRequest.KIND,
            PrepareStorageResourceRequest.KIND,
            PrepareDataResourceRequest.KIND,
            PrepareComputeResourceRequest.KIND,
            PrepareExecutableRequest.KIND,
            SessionAvailableRequest.KIND
            );
        }


    @Override
    @Scheduled(fixedDelay = 10000)
    public void loop()
        {
        super.loop();
        }
    
    @Override
    protected ProcessingAction preProcess(ProcessingRequestEntity request)
        {
        log.debug("Service [{}] pre-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        request.preProcess(
            this.platform
            );
        return null;
        }

    @Override
    protected void postProcess(ProcessingRequestEntity request, ProcessingAction action)
        {
        log.debug("Service [{}] post-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        request.postProcess(
            this.platform
            );
        }
    }
