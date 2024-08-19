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
 *
 */
package uk.co.metagrid.ambleck.model;

/*
 * Resource statistics for an offer.
 *
 */
public class ExecutionBlockImpl implements ExecutionBlock
    {
    public ExecutionBlockImpl(
        long blockStart,
        long blockLength,
        int minCores,
        int maxCores,
        int minMemory,
        int maxMemory
        ) {
        this.blockStart  = blockStart  ;
        this.blockLength = blockLength ;
        this.minCores = minCores ;
        this.maxCores = maxCores ;
        this.minMemory = minMemory ;
        this.maxMemory = maxMemory ;
        }

    private long blockStart;
    public long getBlockStart()
        {
        return this.blockStart;
        }

    private long blockLength;
    public long getBlockLength()
        {
        return this.blockLength;
        }

    private int minCores;
    public int  getMinCores()
        {
        return this.minCores;
        }

    private int maxCores;
    public int  getMaxCores()
        {
        return this.maxCores;
        }

    private int minMemory;
    public int  getMinMemory()
        {
        return this.minMemory;
        }

    private int maxMemory;
    public int  getMaxMemory()
        {
        return this.maxMemory;
        }
    }

