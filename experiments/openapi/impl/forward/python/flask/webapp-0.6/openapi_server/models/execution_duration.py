from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server import util


class ExecutionDuration(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, min=None, max=None):  # noqa: E501
        """ExecutionDuration - a model defined in OpenAPI

        :param min: The min of this ExecutionDuration.  # noqa: E501
        :type min: str
        :param max: The max of this ExecutionDuration.  # noqa: E501
        :type max: str
        """
        self.openapi_types = {
            'min': str,
            'max': str
        }

        self.attribute_map = {
            'min': 'min',
            'max': 'max'
        }

        self._min = min
        self._max = max

    @classmethod
    def from_dict(cls, dikt) -> 'ExecutionDuration':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ExecutionDuration of this ExecutionDuration.  # noqa: E501
        :rtype: ExecutionDuration
        """
        return util.deserialize_model(dikt, cls)

    @property
    def min(self) -> str:
        """Gets the min of this ExecutionDuration.


        :return: The min of this ExecutionDuration.
        :rtype: str
        """
        return self._min

    @min.setter
    def min(self, min: str):
        """Sets the min of this ExecutionDuration.


        :param min: The min of this ExecutionDuration.
        :type min: str
        """

        self._min = min

    @property
    def max(self) -> str:
        """Gets the max of this ExecutionDuration.


        :return: The max of this ExecutionDuration.
        :rtype: str
        """
        return self._max

    @max.setter
    def max(self, max: str):
        """Sets the max of this ExecutionDuration.


        :param max: The max of this ExecutionDuration.
        :type max: str
        """

        self._max = max