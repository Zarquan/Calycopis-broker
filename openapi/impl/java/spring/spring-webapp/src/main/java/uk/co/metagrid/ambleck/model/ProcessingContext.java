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
 */
package uk.co.metagrid.ambleck.model;

import java.util.Map;
import java.util.List;

/**
 * A class to hold context during processing.
 *
 */
public interface ProcessingContext
    {
    public boolean valid();
    public void valid(boolean value);
    public void fail();

    public OfferSetRequest  request();
    public OfferSetResponse response();

    public String baseurl();

    public void addMessage(final MessageItem message);

    public void addDataResource(final AbstractDataResource data);
    public List<AbstractDataResource> getDataResourceList();
    public AbstractDataResource findDataResource(final String key);

    public void addComputeResource(final AbstractComputeResource comp);
    public List<AbstractComputeResource> getComputeResourceList();
    public AbstractComputeResource findComputeResource(final String key);

    public AbstractExecutable getExecutable();
    public void setExecutable(final AbstractExecutable executable);

    public int getMinCores();
    public int getMaxCores();
    public void addMinCores(int delta);
    public void addMaxCores(int delta);

    public int getMinMemory();
    public int getMaxMemory();
    public void addMinMemory(int delta);
    public void addMaxMemory(int delta);

    public long getStartTime();
    public long getDuration();
    public void setDuration(long value);
    }


