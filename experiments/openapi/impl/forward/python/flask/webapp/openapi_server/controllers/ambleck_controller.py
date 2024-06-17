import connexion
from typing import Dict
from typing import Tuple
from typing import Union

from openapi_server.models.parcolar_request import ParcolarRequest  # noqa: E501
from openapi_server.models.parcolar_response import ParcolarResponse  # noqa: E501
from openapi_server import util


def ambleck_post(parcolar_request):  # noqa: E501
    """ambleck_post

     # noqa: E501

    :param parcolar_request: 
    :type parcolar_request: dict | bytes

    :rtype: Union[ParcolarResponse, Tuple[ParcolarResponse, int], Tuple[ParcolarResponse, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        parcolar_request = ParcolarRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
