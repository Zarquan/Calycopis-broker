from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List, Union, Optional

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

class PingExecutable(AbstractExecutable):
    spec: Optional[PingSpecific] = None

class DelayExecutable(AbstractExecutable):
    spec: Optional[DelaySpecific] = None

class Resources(BaseModel):
    compute: Optional[List[AbstractComputeResource]] = None
    storage: Optional[List[AbstractStorageResource]] = None

class ParcolarRequest(BaseModel):
    executable: Optional[AbstractExecutable] = None
    resources: Optional[Resources] = None

class ParcolarOffer(BaseModel):
    executable: Optional[AbstractExecutable] = None
    resources: Optional[Resources] = None

class ParcolarResponse(BaseModel):
    result: str
    offers: Optional[List[ParcolarOffer]] = None

@app.post("/ambleck", response_model=ParcolarResponse, summary="Post an ambleck request")
async def ambleck_post(request: ParcolarRequest):
    # Implement your business logic here
    # return ParcolarResponse(result="YES", offers=[])

    spec1 = PingSpecific(
        target = "www.metagrid.xyz"
        )

    executable1 = PingExecutable(
        name = "test-001",
        type = "urn:ping-executable",
        spec = spec1
        )

    offer1 = ParcolarOffer(
        executable = executable1
        )

    offers = []
    offers.append(offer1)

    return ParcolarResponse(
        result="YES",
        offers=offers
        )


# Add the following lines to start the server if running directly
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)

