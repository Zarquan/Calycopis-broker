from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.delay_executable import DelayExecutable
from openapi_server.models.ping_executable import PingExecutable
from openapi_server.models.ping_specific import PingSpecific
from openapi_server import util

from openapi_server.models.delay_executable import DelayExecutable  # noqa: E501
from openapi_server.models.ping_executable import PingExecutable  # noqa: E501
from openapi_server.models.ping_specific import PingSpecific  # noqa: E501

class ParcolarRequestExecutable(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, type=None, name=None, spec=None):  # noqa: E501
        """ParcolarRequestExecutable - a model defined in OpenAPI

        :param type: The type of this ParcolarRequestExecutable.  # noqa: E501
        :type type: str
        :param name: The name of this ParcolarRequestExecutable.  # noqa: E501
        :type name: str
        :param spec: The spec of this ParcolarRequestExecutable.  # noqa: E501
        :type spec: PingSpecific
        """
        self.openapi_types = {
            'type': str,
            'name': str,
            'spec': PingSpecific
        }

        self.attribute_map = {
            'type': 'type',
            'name': 'name',
            'spec': 'spec'
        }

        self._type = type
        self._name = name
        self._spec = spec

    @classmethod
    def from_dict(cls, dikt) -> 'ParcolarRequestExecutable':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ParcolarRequest_executable of this ParcolarRequestExecutable.  # noqa: E501
        :rtype: ParcolarRequestExecutable
        """
        return util.deserialize_model(dikt, cls)

    @property
    def type(self) -> str:
        """Gets the type of this ParcolarRequestExecutable.


        :return: The type of this ParcolarRequestExecutable.
        :rtype: str
        """
        return self._type

    @type.setter
    def type(self, type: str):
        """Sets the type of this ParcolarRequestExecutable.


        :param type: The type of this ParcolarRequestExecutable.
        :type type: str
        """
        if type is None:
            raise ValueError("Invalid value for `type`, must not be `None`")  # noqa: E501

        self._type = type

    @property
    def name(self) -> str:
        """Gets the name of this ParcolarRequestExecutable.


        :return: The name of this ParcolarRequestExecutable.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """Sets the name of this ParcolarRequestExecutable.


        :param name: The name of this ParcolarRequestExecutable.
        :type name: str
        """

        self._name = name

    @property
    def spec(self) -> PingSpecific:
        """Gets the spec of this ParcolarRequestExecutable.


        :return: The spec of this ParcolarRequestExecutable.
        :rtype: PingSpecific
        """
        return self._spec

    @spec.setter
    def spec(self, spec: PingSpecific):
        """Sets the spec of this ParcolarRequestExecutable.


        :param spec: The spec of this ParcolarRequestExecutable.
        :type spec: PingSpecific
        """

        self._spec = spec
