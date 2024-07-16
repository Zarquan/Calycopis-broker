import connexion
from typing import Dict
from typing import Tuple
from typing import Union

from openapi_server.models.execution_status_response import ExecutionStatusResponse  # noqa: E501
from openapi_server.models.execution_update_request import ExecutionUpdateRequest  # noqa: E501
from openapi_server.models.offers_request import OffersRequest  # noqa: E501
from openapi_server.models.offers_response import OffersResponse  # noqa: E501
from openapi_server import util


def execution_status(ident):  # noqa: E501
    """execution_status

     # noqa: E501

    :param ident: The execution identifier
    :type ident: str
    :type ident: str

    :rtype: Union[ExecutionStatusResponse, Tuple[ExecutionStatusResponse, int], Tuple[ExecutionStatusResponse, int, Dict[str, str]]
    """
    return 'do some magic!'


def execution_update(ident, execution_update_request):  # noqa: E501
    """execution_update

     # noqa: E501

    :param ident: The execution identifier
    :type ident: str
    :type ident: str
    :param execution_update_request: 
    :type execution_update_request: dict | bytes

    :rtype: Union[ExecutionStatusResponse, Tuple[ExecutionStatusResponse, int], Tuple[ExecutionStatusResponse, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        execution_update_request = ExecutionUpdateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def offers_request(offers_request):  # noqa: E501
    """offers_request

     # noqa: E501

    :param offers_request: 
    :type offers_request: dict | bytes

    :rtype: Union[OffersResponse, Tuple[OffersResponse, int], Tuple[OffersResponse, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        offers_request = OffersRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
