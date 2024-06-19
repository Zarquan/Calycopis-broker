from fastapi import FastAPI
from pydantic import BaseModel, Field
from typing import List, Union, Optional
from pydantic.class_validators import root_validator

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
    compute: Optional[List[AbstractComputeResource]] = None
    storage: Optional[List[AbstractStorageResource]] = None

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
        return values

class ParcolarOffer(BaseModel):
    executable: Optional[ExecutableUnion] = None
    resources: Optional[Resources] = None
'''
    @root_validator(pre=True)
    def check_executable_type(cls, values):
        if 'executable' in values and isinstance(values['executable'], dict):
            exec_type = values['executable'].get('type')
            if exec_type == "urn:ping-executable":
                values['executable'] = PingExecutable(**values['executable'])
            elif exec_type == "urn:delay-executable":
                values['executable'] = DelayExecutable(**values['executable'])
        return values
'''

class ParcolarResponse(BaseModel):
    result: str
    offers: Optional[List[ParcolarOffer]] = None

@app.post("/ambleck", response_model=ParcolarResponse, summary="Post an ambleck request")
async def ambleck_post(request: ParcolarRequest):
    # Implement your business logic here

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


# Add the following lines to start the server if running directly
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)

