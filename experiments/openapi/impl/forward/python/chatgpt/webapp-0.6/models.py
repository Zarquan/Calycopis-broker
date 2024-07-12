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

from pydantic import BaseModel, Field, RootModel
from typing import List, Optional, Dict, Union

class MinMaxInteger(BaseModel):
    min: int
    max: int
    units: str

class MinMaxFloat(BaseModel):
    min: float
    max: float
    units: str

class NameValueMap(RootModel):
    root: Dict[str, str]

class AbstractComponent(BaseModel):
    ident: str
    name: str
    properties: Optional[NameValueMap]

class AbstractPolymorph(AbstractComponent):
    type: str

class AbstractComputeResource(AbstractPolymorph):
    pass

class SimpleComputeResource(AbstractComputeResource):
    cores: MinMaxInteger
    memory: MinMaxInteger
    volumes: Optional[List[Dict]]

class AbstractStorageResource(AbstractPolymorph):
    pass

class SimpleStorageResource(AbstractStorageResource):
    size: MinMaxInteger

class AbstractDataResource(AbstractPolymorph):
    pass

class SimpleDataResource(AbstractDataResource):
    location: str

class S3DataResource(AbstractDataResource):
    endpoint: str
    template: str
    bucket: str
    object: Optional[str]

class DockerNetworkPort(BaseModel):
    internal: str
    external: Optional[str]
    address: Optional[str]
    protocol: Optional[str]

class DockerNetworkSpec(BaseModel):
    ports: Optional[List[DockerNetworkPort]]

class DockerContainer01(AbstractPolymorph):
    image: str
    namespace: Optional[str]
    tag: Optional[str]
    repository: Optional[str]
    platform: Optional[str]
    privileged: Optional[bool] = False
    entrypoint: Optional[str]
    environment: Optional[NameValueMap]
    network: Optional[DockerNetworkSpec]

class SingularContainer01(AbstractPolymorph):
    image: str

class Repo2DockerContainer01(AbstractPolymorph):
    source: str

class JupyterNotebook01(AbstractPolymorph):
    notebook: str

class BinderNotebook01(AbstractPolymorph):
    repository: str
    notebook: str

class ExecutionDuration(BaseModel):
    min: Optional[str]
    max: Optional[str]

class ExecutionScheduleItem(BaseModel):
    start: Optional[str]
    duration: Optional[ExecutionDuration]

class ExecutionResourceList(BaseModel):
    compute: Optional[List[AbstractComputeResource]]
    storage: Optional[List[AbstractStorageResource]]
    data: Optional[List[AbstractDataResource]]

class OfferStatus(BaseModel):
    status: Optional[str]
    expires: Optional[str]

class ExecutionStatus(BaseModel):
    started: Optional[str]
    completed: Optional[str]
    status: Optional[str]

class ExecutionBase(BaseModel):
    executable: AbstractPolymorph
    resources: ExecutionResourceList
    schedule: Optional[List[ExecutionScheduleItem]]

class ExecutionFull(BaseModel):
    offer: OfferStatus
    execution: ExecutionStatus

class OffersRequest(ExecutionBase):
    pass

class OffersResponse(BaseModel):
    result: str
    offers: Optional[List[ExecutionFull]]
    messages: Optional[List[str]]

class ExecutionUpdateRequest(BaseModel):
    path: str
    type: str

class ExecutionStatusResponse(BaseModel):
    offer: OfferStatus
    execution: ExecutionStatus
    options: Optional[List[Dict]]

class OptionBase(BaseModel):
    path: str
    type: str

class StringValueOption(OptionBase):
    pattern: Optional[str]

class EnumValueOption(OptionBase):
    values: List[str]

class IntegerValueOption(OptionBase):
    min: Optional[int]
    max: Optional[int]
    units: Optional[str]

class IntegerDeltaOption(OptionBase):
    min: Optional[int]
    max: Optional[int]
    units: Optional[str]

class StringValueUpdate(BaseModel):
    path: str
    type: str
    value: str

class EnumValueUpdate(BaseModel):
    path: str
    type: str
    value: str

class IntegerValueUpdate(BaseModel):
    path: str
    type: str
    value: int
    units: Optional[str]

class IntegerDeltaUpdate(BaseModel):
    path: str
    type: str
    delta: int
    units: Optional[str]

