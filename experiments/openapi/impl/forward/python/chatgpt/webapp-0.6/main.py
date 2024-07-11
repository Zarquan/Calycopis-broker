#
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2024, Manchester (http://www.manchester.ac.uk/)
#
#     This information is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This information is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#
# AIMetrics: [
#     {
#     "name": "ChatGPT",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     }
#   ]
#

from fastapi import FastAPI, Request, Body, Path
from fastapi.responses import JSONResponse, Response
import yaml
import json
from typing import Dict

from models import (
    OffersRequest, OffersResponse, ExecutionStatusResponse,
    ExecutionUpdateRequest, SimpleComputeResource, SimpleStorageResource,
    SimpleDataResource, S3DataResource, DockerContainer01, SingularContainer01,
    Repo2DockerContainer01, JupyterNotebook01, BinderNotebook01
)

app = FastAPI()

polymorphic_models = {
    "urn:simple-compute-resource": SimpleComputeResource,
    "urn:simple-storage-resource": SimpleStorageResource,
    "urn:simple-data-resource": SimpleDataResource,
    "urn:S3-data-resource": S3DataResource,
    "urn:docker-container-0.1": DockerContainer01,
    "urn:single-container-0.1": SingularContainer01,
    "urn:repo2docker-01.": Repo2DockerContainer01,
    "urn:jupyter-notebook-01.": JupyterNotebook01,
    "urn:binder-notebook-01.": BinderNotebook01
}

@app.post("/request", response_model=OffersResponse)
async def handle_offers_request(request: Request, body: OffersRequest):
    content_type = request.headers.get('Content-Type')
    response_data = {
        "result": "YES",
        "offers": [],
        "messages": []
    }
    return get_response(response_data, content_type)

@app.get("/execution/{ident}", response_model=ExecutionStatusResponse)
async def get_execution_status(request: Request, ident: str):
    content_type = request.headers.get('Content-Type')
    response_data = {
        "offer": {},
        "execution": {
            "status": "RUNNING",
            "started": "2024-07-11T10:00:00Z",
            "completed": None
        },
        "options": []
    }
    return get_response(response_data, content_type)

@app.post("/execution/{ident}", response_model=ExecutionStatusResponse)
async def update_execution_status(request: Request, ident: str, body: ExecutionUpdateRequest):
    content_type = request.headers.get('Content-Type')
    response_data = {
        "offer": {},
        "execution": {
            "status": "UPDATED",
            "started": "2024-07-11T10:00:00Z",
            "completed": None
        },
        "options": []
    }
    return get_response(response_data, content_type)

def get_response(data: Dict, content_type: str):
    if content_type == "application/xml":
        return Response(content=to_xml(data), media_type="application/xml")
    elif content_type == "application/yaml":
        return Response(content=to_yaml(data), media_type="application/yaml")
    return JSONResponse(content=data)

def to_yaml(data: Dict) -> str:
    return yaml.dump(data)

def to_xml(data: Dict) -> str:
    from dicttoxml import dicttoxml
    return dicttoxml(data).decode()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)

