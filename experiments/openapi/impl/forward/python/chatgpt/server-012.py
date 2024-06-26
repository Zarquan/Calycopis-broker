from fastapi import FastAPI, HTTPException, Request
from pydantic import BaseModel, Field, ValidationError, root_validator
from typing import List, Union, Optional
from fastapi.responses import JSONResponse, Response
from fastapi.encoders import jsonable_encoder
import yaml
import xmltodict

app = FastAPI(
    title="OpenAPI definition",
    version="v0"
)

# Define the models based on the OpenAPI spec

class MinMaxInteger(BaseModel):
    min: Optional[int] = None
    max: Optional[int] = None
    units: Optional[str] = None

class AbstractSpecific(BaseModel):
    pass

class SimpleComputeSpecific(AbstractSpecific):
    cores: Optional[MinMaxInteger] = None
    memory: Optional[MinMaxInteger] = None

class SimpleStorageSpecific(AbstractSpecific):
    size: Optional[MinMaxInteger] = None

class PingSpecific(AbstractSpecific):
    target: Optional[str] = None

class DelaySpecific(AbstractSpecific):
    duration: Optional[int] = None

class AbstractComputeResource(BaseModel):
    type: str
    name: Optional[str] = None
    spec: Optional[AbstractSpecific] = None

class SimpleComputeResource(AbstractComputeResource):
    spec: Optional[SimpleComputeSpecific] = None

class AbstractStorageResource(BaseModel):
    type: str
    name: Optional[str] = None
    spec: Optional[AbstractSpecific] = None

class SimpleStorageResource(AbstractStorageResource):
    spec: Optional[SimpleStorageSpecific] = None

class AbstractExecutable(BaseModel):
    type: str
    name: Optional[str] = None
    spec: Optional[AbstractSpecific] = None

    class Config:
        discriminator = 'type'

class PingExecutable(AbstractExecutable):
    spec: Optional[PingSpecific] = None

class DelayExecutable(AbstractExecutable):
    spec: Optional[DelaySpecific] = None

# Use Pydantic's Union and discriminator features to handle polymorphism
ExecutableUnion = Union[PingExecutable, DelayExecutable]

class Resources(BaseModel):
    compute: Optional[List[AbstractComputeResource]] = Field(default=None, alias="computes")
    storage: Optional[List[AbstractStorageResource]] = Field(default=None, alias="storages")

    class Config:
        allow_population_by_field_name = True

class ParcolarRequest(BaseModel):
    executable: Optional[ExecutableUnion] = None
    resources: Optional[Resources] = None

    @root_validator(pre=True)
    def check_executable_type(cls, values):
        if 'executable' in values and isinstance(values['executable'], dict):
            exec_type = values['executable'].get('type')
            if exec_type == "urn:ping-executable":
                values['executable'] = PingExecutable(**values['executable'])
            elif exec_type == "urn:delay-executable":
                values['executable'] = DelayExecutable(**values['executable'])
            else:
                raise ValueError(f"Unknown executable type: {exec_type}")
        return values

class ParcolarOffer(BaseModel):
    executable: Optional[ExecutableUnion] = None
    resources: Optional[Resources] = None

    @root_validator(pre=True)
    def check_executable_type(cls, values):
        if 'executable' in values and isinstance(values['executable'], dict):
            exec_type = values['executable'].get('type')
            if exec_type == "urn:ping-executable":
                values['executable'] = PingExecutable(**values['executable'])
            elif exec_type == "urn:delay-executable":
                values['executable'] = DelayExecutable(**values['executable'])
            else:
                raise ValueError(f"Unknown executable type: {exec_type}")
        return values

class ParcolarResponse(BaseModel):
    result: str
    offers: Optional[List[ParcolarOffer]] = None

    def yaml(self):
        return yaml.dump(jsonable_encoder(self), sort_keys=False)

    def xml(self):
        # Convert the model to a dictionary first
        dict_data = jsonable_encoder(self)
        # Wrap lists with proper element names
        dict_data = {
            'ParcolarResponse': {
                'result': dict_data['result'],
                'offers': {
                    'offer': dict_data['offers']
                } if dict_data['offers'] else None
            }
        }
        # Convert the dictionary to an XML string
        xml_str = xmltodict.unparse(dict_data, pretty=True)
        return xml_str

#
# Process a request.
def process(request: ParcolarRequest):

    offers = []

    if isinstance(request.executable, PingExecutable):

        offers.append(
            ParcolarOffer(
                executable = PingExecutable(
                    type = "urn:ping-executable",
                    name = request.executable.name,
                    spec = PingSpecific(
                        target = request.executable.spec.target
                        )
                    )
                )
            )

    elif isinstance(request.executable, DelayExecutable):

        offers.append(
            ParcolarOffer(
                executable = DelayExecutable(
                    type = "urn:delay-executable",
                    name = request.executable.name,
                    spec = DelaySpecific(
                        duration = request.executable.spec.duration
                        )
                    )
                )
            )

    if (len(offers) > 0):
        return ParcolarResponse(
            result="YES",
            offers=offers
            )
    else:
        return ParcolarResponse(
            result="NO"
            )

@app.post("/ambleck", summary="Post an ambleck request")
async def ambleck_post(request: Request):
    content_type = request.headers.get('content-type')

    if content_type == "application/json":
        try:
            parcolar_request = ParcolarRequest(**await request.json())
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/yaml":
        try:
            body = await request.body()
            parcolar_request = ParcolarRequest(**yaml.safe_load(body))
        except ValidationError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    elif content_type == "application/xml":
        try:
            body = await request.body()
            dict_data = xmltodict.parse(body)
            parcolar_request = ParcolarRequest(**dict_data['ParcolarRequest'])
        except (ValidationError, KeyError) as e:
            raise HTTPException(status_code=400, detail=str(e))
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
    else:
        raise HTTPException(status_code=415, detail="Unsupported Media Type")

    # Implement your business logic here
    parcolar_response = process(
        parcolar_request
        )

    # Determine the response format
    accept_header = request.headers.get('accept')
    if 'application/xml' in accept_header:
        return Response(content=parcolar_response.xml(), media_type="application/xml")
    elif 'application/yaml' in accept_header:
        return Response(content=parcolar_response.yaml(), media_type="application/yaml")
    else:
        return JSONResponse(content=jsonable_encoder(parcolar_response))

# Add the following lines to start the server if running directly
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)


