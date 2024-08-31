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
package uk.co.metagrid.ambleck.webapp;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.metagrid.ambleck.model.ExecutionResponse;
import uk.co.metagrid.ambleck.model.ExecutionBlockDatabase;
import uk.co.metagrid.ambleck.platform.ExecutionManager;
import uk.co.metagrid.ambleck.platform.PreparationStep.StateEnum;

@Service
@ComponentScan("uk.co.metagrid.ambleck")
public class SystemApiDelegateImpl
    extends BaseDelegateImpl
    implements SystemApiDelegate
    {

    private final ExecutionManager manager ;
    private final ExecutionBlockDatabase database ;

    @Autowired
    public SystemApiDelegateImpl(
        final NativeWebRequest request,
        final ExecutionManager manager,
        final ExecutionBlockDatabase database
        )
        {
        super(request);
        this.manager = manager ;
        this.database = database ;
        }

    @Override
    public ResponseEntity<String> sweepUpdate()
        {
        int result = this.database.sweepUpdate(1);
        return new ResponseEntity<String>(
            "Update [:result:]".replace(
                ":result:",
                String.valueOf(
                    result
                    )
                ),
            HttpStatus.OK
            );
        }

    @Override
    public ResponseEntity<String> sweepDelete()
        {
        int result = this.database.sweepDelete(1);
        return new ResponseEntity<String>(
            "Delete [:result:]".replace(
                ":result:",
                String.valueOf(
                    result
                    )
                ),
            HttpStatus.OK
            );
        }


    @Override
    public ResponseEntity<ExecutionResponse> stepUpdate(final UUID uuid)
        {
        ExecutionResponse response = manager.advance(
            uuid
            );
        if (null != response)
            {
            return new ResponseEntity<ExecutionResponse>(
                response,
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
                );
            }
        }
    }
