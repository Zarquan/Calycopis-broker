from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.abstract_compute_resource import AbstractComputeResource
from openapi_server.models.compute_resource_volume import ComputeResourceVolume
from openapi_server.models.min_max_integer import MinMaxInteger
from openapi_server import util

from openapi_server.models.abstract_compute_resource import AbstractComputeResource  # noqa: E501
from openapi_server.models.compute_resource_volume import ComputeResourceVolume  # noqa: E501
from openapi_server.models.min_max_integer import MinMaxInteger  # noqa: E501

class SimpleComputeResource(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, ident=None, name=None, properties=None, type=None, cores=None, memory=None, volumes=None):  # noqa: E501
        """SimpleComputeResource - a model defined in OpenAPI

        :param ident: The ident of this SimpleComputeResource.  # noqa: E501
        :type ident: str
        :param name: The name of this SimpleComputeResource.  # noqa: E501
        :type name: str
        :param properties: The properties of this SimpleComputeResource.  # noqa: E501
        :type properties: Dict[str, str]
        :param type: The type of this SimpleComputeResource.  # noqa: E501
        :type type: str
        :param cores: The cores of this SimpleComputeResource.  # noqa: E501
        :type cores: MinMaxInteger
        :param memory: The memory of this SimpleComputeResource.  # noqa: E501
        :type memory: MinMaxInteger
        :param volumes: The volumes of this SimpleComputeResource.  # noqa: E501
        :type volumes: List[ComputeResourceVolume]
        """
        self.openapi_types = {
            'ident': str,
            'name': str,
            'properties': Dict[str, str],
            'type': str,
            'cores': MinMaxInteger,
            'memory': MinMaxInteger,
            'volumes': List[ComputeResourceVolume]
        }

        self.attribute_map = {
            'ident': 'ident',
            'name': 'name',
            'properties': 'properties',
            'type': 'type',
            'cores': 'cores',
            'memory': 'memory',
            'volumes': 'volumes'
        }

        self._ident = ident
        self._name = name
        self._properties = properties
        self._type = type
        self._cores = cores
        self._memory = memory
        self._volumes = volumes

    @classmethod
    def from_dict(cls, dikt) -> 'SimpleComputeResource':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The SimpleComputeResource of this SimpleComputeResource.  # noqa: E501
        :rtype: SimpleComputeResource
        """
        return util.deserialize_model(dikt, cls)

    @property
    def ident(self) -> str:
        """Gets the ident of this SimpleComputeResource.


        :return: The ident of this SimpleComputeResource.
        :rtype: str
        """
        return self._ident

    @ident.setter
    def ident(self, ident: str):
        """Sets the ident of this SimpleComputeResource.


        :param ident: The ident of this SimpleComputeResource.
        :type ident: str
        """

        self._ident = ident

    @property
    def name(self) -> str:
        """Gets the name of this SimpleComputeResource.


        :return: The name of this SimpleComputeResource.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """Sets the name of this SimpleComputeResource.


        :param name: The name of this SimpleComputeResource.
        :type name: str
        """

        self._name = name

    @property
    def properties(self) -> Dict[str, str]:
        """Gets the properties of this SimpleComputeResource.

        A map of name->value properties. See https://swagger.io/docs/specification/data-models/dictionaries/   # noqa: E501

        :return: The properties of this SimpleComputeResource.
        :rtype: Dict[str, str]
        """
        return self._properties

    @properties.setter
    def properties(self, properties: Dict[str, str]):
        """Sets the properties of this SimpleComputeResource.

        A map of name->value properties. See https://swagger.io/docs/specification/data-models/dictionaries/   # noqa: E501

        :param properties: The properties of this SimpleComputeResource.
        :type properties: Dict[str, str]
        """

        self._properties = properties

    @property
    def type(self) -> str:
        """Gets the type of this SimpleComputeResource.


        :return: The type of this SimpleComputeResource.
        :rtype: str
        """
        return self._type

    @type.setter
    def type(self, type: str):
        """Sets the type of this SimpleComputeResource.


        :param type: The type of this SimpleComputeResource.
        :type type: str
        """
        if type is None:
            raise ValueError("Invalid value for `type`, must not be `None`")  # noqa: E501

        self._type = type

    @property
    def cores(self) -> MinMaxInteger:
        """Gets the cores of this SimpleComputeResource.

        The number of cpu cores required. Default units of `cores` are equivalent to a single physical cpu core. Use `milli cores` or `m cores` to specify Kubernetes 1/1000 values.   # noqa: E501

        :return: The cores of this SimpleComputeResource.
        :rtype: MinMaxInteger
        """
        return self._cores

    @cores.setter
    def cores(self, cores: MinMaxInteger):
        """Sets the cores of this SimpleComputeResource.

        The number of cpu cores required. Default units of `cores` are equivalent to a single physical cpu core. Use `milli cores` or `m cores` to specify Kubernetes 1/1000 values.   # noqa: E501

        :param cores: The cores of this SimpleComputeResource.
        :type cores: MinMaxInteger
        """

        self._cores = cores

    @property
    def memory(self) -> MinMaxInteger:
        """Gets the memory of this SimpleComputeResource.

        The size of memory required. Specified in SI units, default is `GiB`.   # noqa: E501

        :return: The memory of this SimpleComputeResource.
        :rtype: MinMaxInteger
        """
        return self._memory

    @memory.setter
    def memory(self, memory: MinMaxInteger):
        """Sets the memory of this SimpleComputeResource.

        The size of memory required. Specified in SI units, default is `GiB`.   # noqa: E501

        :param memory: The memory of this SimpleComputeResource.
        :type memory: MinMaxInteger
        """

        self._memory = memory

    @property
    def volumes(self) -> List[ComputeResourceVolume]:
        """Gets the volumes of this SimpleComputeResource.

        A list of resources that need to be mounted as volumes.   # noqa: E501

        :return: The volumes of this SimpleComputeResource.
        :rtype: List[ComputeResourceVolume]
        """
        return self._volumes

    @volumes.setter
    def volumes(self, volumes: List[ComputeResourceVolume]):
        """Sets the volumes of this SimpleComputeResource.

        A list of resources that need to be mounted as volumes.   # noqa: E501

        :param volumes: The volumes of this SimpleComputeResource.
        :type volumes: List[ComputeResourceVolume]
        """

        self._volumes = volumes