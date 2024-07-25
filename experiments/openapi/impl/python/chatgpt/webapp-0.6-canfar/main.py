import logging
import yaml
from fastapi import FastAPI, HTTPException, Request, Depends, Query, Body, Path, HTTPException
from fastapi.responses import RedirectResponse, HTMLResponse, JSONResponse, Response
from fastapi.openapi.docs import get_swagger_ui_html
from fastapi.security import OAuth2PasswordBearer
from fastapi.encoders import jsonable_encoder
from jose import JWTError, jwt
import httpx
import os

from typing import Dict
from pydantic import BaseModel, Field, ValidationError, root_validator
from enum import Enum
from typing import List, Optional
import requests as http_requests


from models import (
    OffersRequest,
    OffersResponse,
    OfferStatusEnum,
    OffersResponseEnum,
    OfferStatus,
    ExecutionFull,
    ExecutionUpdateRequest,
    ExecutionStatusResponse,
    SimpleComputeResource,
    SimpleStorageResource,
    SimpleDataResource,
    S3DataResource,
    DockerContainer01,
    SingularContainer01,
    Repo2DockerContainer01,
    JupyterNotebook01,
    CanfarNotebook01,
    BinderNotebook01
    )
    
from datetime import datetime, timedelta

current_offers = {}

polymorphic_models = {
    "urn:simple-compute-resource": SimpleComputeResource,
    "urn:simple-storage-resource": SimpleStorageResource,
    "urn:simple-data-resource": SimpleDataResource,
    "urn:S3-data-resource": S3DataResource,
    "urn:docker-container-0.1": DockerContainer01,
    "urn:single-container-0.1": SingularContainer01,
    "urn:repo2docker-0.1": Repo2DockerContainer01,
    "urn:canfar-notebook-0.1": CanfarNotebook01,
    "urn:jupyter-notebook-0.1": JupyterNotebook01,
    "urn:binder-notebook-0.1": BinderNotebook01
}


# Configure logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)
    
app = FastAPI(
    title="Test Execution Broker Service",
    description="""
    Simple cut down test implementation of proposed the Execution Broker API for 
    brokering remote distributed compute resources to astronical data reduction tasks.

    This toy model just brokers offers sessions on CADC CANFAR system as a test case
    """,
    version="0.1.0"
)


def process(request: OffersRequest) -> OffersResponse:    
    response = OffersResponse(result = OffersResponseEnum.NO)
    
    logging.debug(f"\n   Processing the request\n")
    logging.debug(f"\n   Executable {request.executable} \n")
    logging.debug(f"\n   Resources  {request.resources} \n")
    logging.debug(f"\n   Schedule   {request.schedule} \n")
    
    # Only accepting notebooks at the moment
    if (isinstance(request.executable, CanfarNotebook01)):
        
        logging.debug("    Yes it's a CANFAR Notebook\n")
        offer = ExecutionFull()
        
        offer.executable = notebook(
            request.executable
            )
        status = OfferStatus()
        status.status = OfferStatusEnum.OFFERED
        status.expires = str(datetime.now() + timedelta(minutes = 5))
        offer.offer = status
        current_offers[offer.executable.uuid] = offer
        
        
        if (response.offers == None):
            response.offers = []
        response.offers.append(offer)
        response.result = OffersResponseEnum.YES
        
        logging.debug(f"    Offers: {response.offers}\n")
        logging.debug(f"    Response: {response}\n")

            
    else:
        response.result = OffersResponseEnum.NO
            
    return response
        
        
def docker(request: DockerContainer01):
    return request

def notebook(request: CanfarNotebook01):
    return request


@app.get("/broker", response_class=HTMLResponse, summary="Swagger ui auto-generated overview")
async def API_definition(request: Request):
    logger.debug(f"   app.openapi_url: {app.openapi_url}")
    return get_swagger_ui_html(openapi_url=app.openapi_url,title="Test Execution Broker API")
    

@app.post("/broker/request", summary="Post a request for offers", tags=['request'])
async def request_post(request: Request):
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
            print(dict_data)
            requestobj = OffersRequest(**dict_data['execution-request'])
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
    if accept_header == "application/xml":
        return Response(content=responseobj.xml(), media_type="application/xml")
    elif accept_header == "application/yaml":
        return Response(content=responseobj.yaml(), media_type="application/yaml")
    else:
                                                                                                                                                                                                                                                                                    return JSONResponse(responseobj.json())
                                                                                                                                                                                                                                                                                


@app.get("/broker/offers/{ident}", response_model=ExecutionStatusResponse, tags=['offers'])
async def status_offers(request: Request, ident: str) -> ExecutionStatusResponse:
    content_type = request.headers.get('Content-Type')
    
    logging.debug(f"   Have GET offer request for {ident}\n")

    if ident in current_offers:
        offer = current_offers[ident]
        logging.debug(f"   Found offer: {offer}")
    else:
        offer = []
        logging.debug("    Not found corresponding offer")
        raise HTTPException(status_code=404, detail="Offer not founds")
    
    responseobj = ExecutionStatusResponse()

    responseobj.offer = offer.offer
    responseobj.execution = offer.execution
    
    
    accept_header = request.headers.get('accept')
    if accept_header == "application/xml":
        return Response(content=responseobj.xml(), media_type="application/xml")
    elif accept_header == "application/yaml":
        return Response(content=responseobj.yaml(), media_type="application/yaml")
    else:
        return JSONResponse(responseobj.json())
                                

    
@app.post("/broker/offers/{ident}", response_model=ExecutionStatusResponse, tags=['offers'])
async def update_offers(request: Request, ident: str) -> ExecutionStatusResponse: 
    content_type = request.headers.get('Content-Type')

    client_cert = "cadcproxy.pem"
    logging.debug(f"   Cert; {client_cert}")
    
    logging.debug(f"   Have POST offer request for {ident}\n")

    if ident in current_offers:
        offer = current_offers[ident]
        logging.debug(f"   Found offer: {offer}")
    else:
        offer = []
        logging.debug("    Not found corresponding offer")
        raise HTTPException(status_code=404, detail="Offer not founds")
    
    responseobj = ExecutionStatusResponse()
    
    responseobj.offer = offer.offer
    responseobj.execution = offer.execution
        
    
    if content_type == "application/json":
        try:
            requestobj = ExecutionUpdateRequest(**await request.json())
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/yaml":
        try:
            yamlbody = await request.body()
            logging.debug(f"    yaml body: {yamlbody}\n")
            requestobj = ExecutionFull(**yaml.safe_load(yamlbody))
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/xml":
        try:
            xmlbody = await request.body()
            dict_data = xmltodict.parse(xmlbody)
            print(dict_data)
            requestobj = ExecutionUpdateRequest(**dict_data['execution-request'])
        except (ValidationError, KeyError) as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    else:
        raise HTTPException(status_code=415, detail="Unsupported Media Type")
    
    logging.debug(f"    Request: {requestobj}\n")
    
    current_status = offer.offer.status
    request_status = requestobj.offer.status

    logging.debug(f"   Current status: {current_status}  Requested: {request_status}\n")
    
    if current_status=="OFFERED" and request_status=="ACCEPTED":
        logging.debug("   Changing status to ACCEPTED and lauching session")
        offer.offer.status = "ACCEPTED"
        responseobj.offer = offer.offer     
        
        image = offer.executable.image
        name = offer.executable.name
        logging.debug(f"\n    image: {image}  name: {name}\n") 
                                
        url = "https://ws-uv.canfar.net/skaha/v0/session"
        info = {"name":name, "image":image}
        r = http_requests.post(url, cert=client_cert, data=info)
        
            
        if r.status_code==200:
            logging.debug(f"Session created with id: {r.text}")
        else:
            logging.debug(f"Failed with error {r.status_code} due to {r.reason}")
        
    # Determine the response format
    accept_header = request.headers.get('accept')
    if accept_header == "application/xml":
        return Response(content=responseobj.xml(), media_type="application/xml")
    elif accept_header == "application/yaml":
        return Response(content=responseobj.yaml(), media_type="application/yaml")
    else:
                                                                                                                                                                                                                                                                                    return JSONResponse(responseobj.json())
                                                                                                                                                                                                                                                                                
                                            
                                        
