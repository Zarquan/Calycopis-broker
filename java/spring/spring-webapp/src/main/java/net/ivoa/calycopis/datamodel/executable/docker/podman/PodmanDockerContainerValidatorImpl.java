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

package net.ivoa.calycopis.datamodel.executable.docker.podman;

import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerValidatorImpl;
import net.ivoa.calycopis.functional.platfom.podman.PodmanPlatform;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;

/**
 * 
 */
public class PodmanDockerContainerValidatorImpl
extends DockerContainerValidatorImpl
implements PodmanDockerContainerValidator
    {

    public PodmanDockerContainerValidatorImpl(PodmanPlatform platform)
        {
        super(platform);
        }


    /*
     * TODO This will be platform dependent.
     * 
     */
    public static final Long DEFAULT_PREPARE_TIME = 35L;
    @Override
    protected Long predictPrepareTime(final IvoaDockerContainer validated)
        {
        return DEFAULT_PREPARE_TIME;
        }
    }
