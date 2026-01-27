# coding: utf-8

from fastapi.testclient import TestClient


from openapi_server.models.execution_status_response import ExecutionStatusResponse  # noqa: F401
from openapi_server.models.execution_update_request import ExecutionUpdateRequest  # noqa: F401
from openapi_server.models.offers_request import OffersRequest  # noqa: F401
from openapi_server.models.offers_response import OffersResponse  # noqa: F401


def test_execution_status(client: TestClient):
    """Test case for execution_status

    
    """

    headers = {
    }
    # uncomment below to make a request
    #response = client.request(
    #    "GET",
    #    "/execution/{ident}".format(ident='ident_example'),
    #    headers=headers,
    #)

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200


def test_execution_update(client: TestClient):
    """Test case for execution_update

    
    """
    execution_update_request = {"path":"path","type":"type"}

    headers = {
    }
    # uncomment below to make a request
    #response = client.request(
    #    "POST",
    #    "/execution/{ident}".format(ident='ident_example'),
    #    headers=headers,
    #    json=execution_update_request,
    #)

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200


def test_offers_request(client: TestClient):
    """Test case for offers_request

    
    """
    offers_request = {"schedule":[{"duration":{"min":"min","max":"max"},"start":"start"},{"duration":{"min":"min","max":"max"},"start":"start"}],"resources":{"compute":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}],"data":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}],"storage":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}]},"executable":{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}}

    headers = {
    }
    # uncomment below to make a request
    #response = client.request(
    #    "POST",
    #    "/request",
    #    headers=headers,
    #    json=offers_request,
    #)

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200

