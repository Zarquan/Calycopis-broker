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

from datetime import datetime, timedelta

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

@app.post("/old-request", response_model=OffersResponse)
async def handle_offers_request(request: Request, body: OffersRequest):
    content_type = request.headers.get('Content-Type')

    response = process(body)

    return get_response(
        vars(response),
        content_type
        )

@app.post("/request", summary="Post a request for offers")
async def ambleck_post(request: Request):
    content_type = request.headers.get('content-type')

    if content_type == "application/json":
        try:
            requestobj = OffersRequest(**await request.json())
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/yaml":
        try:
            yamlbody = await request.body()
            requestobj = OffersRequest(**yaml.safe_load(yamlbody))
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/xml":
        try:
            xmlbody = await request.body()
            dict_data = xmltodict.parse(xmlbody)
            requestobj = OffersRequest(**dict_data['OffersRequest'])
        except (ValidationError, KeyError) as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    else:
        raise HTTPException(status_code=415, detail="Unsupported Media Type")

    # Implement your business logic here
    responseobj = process(
        requestobj
        )

    # Determine the response format
    accept_header = request.headers.get('accept')
    if 'application/xml' in accept_header:
        return Response(content=responseobj.xml(), media_type="application/xml")
    elif 'application/yaml' in accept_header:
        return Response(content=responseobj.yaml(), media_type="application/yaml")
    else:
        return JSONResponse(content=jsonable_encoder(responseobj))

def process(request: OffersRequest):

    response = OffersResponse()

    if (isinstance(request.getExecutable(), DockerContainer01)):

        offer = ExecutionFull()

        offer.setExecutable(
            docker(
                request.getExecutable()
                )
            )
        status = OfferStatus()
        status.status(
            OfferStatus.StatusEnum.OFFERED
            )
        status.expires(
            datetime.now() + timedelta(minutes = 5)
            )
        offer.setOffer(
            status
            );
        response.addOffersItem(
             offer
            )
        response.setResult(
            OffersResponse.ResultEnum.YES
            )

    else:
        response.setResult(
            OffersResponse.ResultEnum.NO
            )

def docker(request: DockerContainer01):
    return request


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

