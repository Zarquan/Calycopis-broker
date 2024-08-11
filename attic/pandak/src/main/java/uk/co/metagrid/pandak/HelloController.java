/*
 *
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2023 by Wizzard Solutions Ltd, wizzard@metagrid.co.uk
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

package uk.co.metagrid.pandak;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    public static class TextResponse {

        private String text;

        public TextResponse() {
            this.setText("No text");
            }

        public TextResponse(String text) {
            this.setText(text);
            }

        public String getText() {
            return text;
            }

        public void setText(String text) {
            this.text = text;
            }
        }

    @GetMapping(
        value = "/appleblert",
//        consumes = "application/json"
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            "application/yaml"
            }
        )
    @ResponseBody
	public TextResponse getAny() {
		return new TextResponse("Example result");
	    }

    @PostMapping(
        value = "/ponditak",
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            "application/yaml"
            },
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            "application/yaml"
            }
        )
    @ResponseBody
	public TextResponse postAny(
	    @RequestBody TextResponse request
	    ) {
	    if (null != request)
	        {
		    return new TextResponse(
		        request.getText()
		        );
	        }
        else {
		    return new TextResponse(
		        "Null request"
		        );
		    }
	    }
    }



