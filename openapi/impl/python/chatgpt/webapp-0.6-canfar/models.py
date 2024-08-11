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

from pydantic import BaseModel, Field, RootModel, ValidationError, root_validator
from typing import List, Optional, Dict, Union
from enum import Enum, auto
from strenum import StrEnum
from fastapi.encoders import jsonable_encoder

import yaml
import uuid
import xmltodict

class MinMaxInteger(BaseModel):
    min: Optional[int] = None
    max: Optional[int] = None
    units: Optional[str] = None

class MinMaxFloat(BaseModel):
    min: Optional[float] = None
    max: Optional[float] = None
    units: Optional[str] = None

class NameValueMap(RootModel):
    root: Dict[str, str]

class AbstractComponent(BaseModel):
    uuid: Optional[str] = str(uuid.uuid1())
    name: Optional[str] = None
    properties: Optional[NameValueMap] = None

class AbstractPolymorph(AbstractComponent):
    type: str

class AbstractComputeResource(AbstractPolymorph):
    pass

class SimpleComputeResource(AbstractComputeResource):
    cores: Optional[MinMaxInteger] = None
    memory: Optional[MinMaxInteger] = None
    volumes: Optional[List[Dict]] = None

class AbstractStorageResource(AbstractPolymorph):
    pass

class SimpleStorageResource(AbstractStorageResource):
    size: Optional[MinMaxInteger] = None

class AbstractDataResource(AbstractPolymorph):
    pass

class SimpleDataResource(AbstractDataResource):
    location: str

class S3DataResource(AbstractDataResource):
    endpoint: Optional[str] = None
    template: Optional[str] = None
    bucket: Optional[str] = None
    object: Optional[str] = None

class DockerNetworkPort(BaseModel):
    internal: int
    external: Optional[str] = None
    address: Optional[str] = None
    protocol: Optional[str] = None

class DockerNetworkSpec(BaseModel):
    ports: Optional[List[DockerNetworkPort]] = None

class DockerContainer01(AbstractPolymorph):
    image: Optional[str] = None
    namespace: Optional[str] = None
    tag: Optional[str] = None
    repository: Optional[str] = None
    platform: Optional[str] = None
    privileged: Optional[bool] = False
    entrypoint: Optional[str] = None
    environment: Optional[NameValueMap] = None
    network: Optional[DockerNetworkSpec] = None

class SingularContainer01(AbstractPolymorph):
    image: Optional[str] = None

class Repo2DockerContainer01(AbstractPolymorph):
    source: Optional[str] = None

class JupyterNotebook01(AbstractPolymorph):
    notebook: Optional[str] = None

class CanfarNotebook01(AbstractPolymorph):
    image: Optional[str] = None
    
class BinderNotebook01(AbstractPolymorph):
    repository: Optional[str] = None
    notebook: Optional[str] = None

class ExecutionDuration(BaseModel):
    min: Optional[str] = None
    max: Optional[str] = None

class ExecutionScheduleItem(BaseModel):
    start: Optional[str] = None
    duration: Optional[ExecutionDuration] = None

class ExecutionResourceList(BaseModel):
    compute: Optional[List[AbstractComputeResource]] = None
    storage: Optional[List[AbstractStorageResource]] = None
    data: Optional[List[AbstractDataResource]] = None

class OfferStatus(BaseModel):
    status: Optional[str] = None
    expires: Optional[str] = None

class OfferStatusEnum(StrEnum):
    OFFERED = "OFFERED"
    EXPIRED = "EXPIRED"
    ACCEPTED = "ACCEPTED"
    REJECTED = "REJECTED"

class ExecutionStatus(BaseModel):
    started: Optional[str] = None
    completed: Optional[str] = None
    status: Optional[str] = None

class ExecutionBase(BaseModel):
    executable: AbstractPolymorph = None
    resources: Optional[ExecutionResourceList] = None
    schedule: Optional[List[ExecutionScheduleItem]] = None

class ExecutionFull(ExecutionBase):
    offer: Optional[OfferStatus] = None
    execution: Optional[ExecutionStatus] = None

class OffersRequest(ExecutionBase):

    @root_validator(pre=True)
    def check_executable_type(cls, values):
        if 'executable' in values and isinstance(values['executable'], dict):
            exec_type = values['executable'].get('type')
            if exec_type == "urn:docker-container-0.1":
                values['executable'] = DockerContainer01(**values['executable'])
            elif exec_type == "urn:single-container-0.1":
                values['executable'] = SingularContainer01(**values['executable'])
            elif exec_type == "urn:repo2docker-0.1":
                values['executable'] = Repo2DockerContainer01(**values['executable'])
            elif exec_type == "urn:jupyter-notebook-0.1":
                values['executable'] = JupyterNotebook01(**values['executable'])
            elif exec_type == "urn:canfar-notebook-0.1":
                values['executable'] = CanfarNotebook01(**values['executable'])
            elif exec_type == "urn:binder-notebook-0.1":
                values['executable'] = BinderNotebook01(**values['executable'])
            else:
                raise ValueError(f"Unknown executable type: {exec_type}")
        return values

class OffersResponseEnum(StrEnum):
    YES = "YES"
    NO  = "NO"

class OffersResponse(BaseModel):
    result: OffersResponseEnum
    offers: Optional[List[ExecutionFull]] = Field(default=None, alias="offers")
    messages: Optional[List[str]] = Field(default=None, alias="messages")

    def yaml(self):
        return yaml.dump(jsonable_encoder(self), sort_keys=False)

    def xml(self):
        # Convert the model to a dictionary first
        dict_data = jsonable_encoder(self)
        # Wrap lists with proper element names
        dict_data = {
            'excution-offers': {
                'result': dict_data['result'],
                'offers': {
                    'offer': dict_data['offers']
                } if dict_data['offers'] else None,
                'messages': {
                    'message': dict_data['messages']
                } if dict_data['messages'] else None
            }
        }
        # Convert the dictionary to an XML string
        xml_str = xmltodict.unparse(dict_data, pretty=True)
        return xml_str

    def json(self):
        return jsonable_encoder(self)

class ExecutionUpdateRequest(BaseModel):
    path: str
    type: str

class ExecutionStatusResponse(BaseModel):
    offer: Optional[OfferStatus] = None
    execution: Optional[ExecutionStatus] = None
    options: Optional[List[Dict]] = None

    def yaml(self):
        return yaml.dump(jsonable_encoder(self), sort_keys=False)

    def xml(self):
        # Convert the model to a dictionary first
        dict_data = jsonable_encoder(self)
        # Wrap the outer element
        dict_data = {
            'excution-status': dict_data
            }
        # Convert the dictionary to an XML string
        xml_str = xmltodict.unparse(dict_data, pretty=True)
        return xml_str

    def json(self):
        return jsonable_encoder(self)

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

