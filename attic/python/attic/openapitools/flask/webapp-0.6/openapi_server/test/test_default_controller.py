import unittest

from flask import json

from openapi_server.models.execution_status_response import ExecutionStatusResponse  # noqa: E501
from openapi_server.models.execution_update_request import ExecutionUpdateRequest  # noqa: E501
from openapi_server.models.offers_request import OffersRequest  # noqa: E501
from openapi_server.models.offers_response import OffersResponse  # noqa: E501
from openapi_server.test import BaseTestCase


class TestDefaultController(BaseTestCase):
    """DefaultController integration test stubs"""

    def test_execution_status(self):
        """Test case for execution_status

        
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/execution/{ident}'.format(ident='ident_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    @unittest.skip("Connexion does not support multiple consumes. See https://github.com/zalando/connexion/pull/760")
    def test_execution_update(self):
        """Test case for execution_update

        
        """
        execution_update_request = {"path":"path","type":"type"}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/execution/{ident}'.format(ident='ident_example'),
            method='POST',
            headers=headers,
            data=json.dumps(execution_update_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    @unittest.skip("Connexion does not support multiple consumes. See https://github.com/zalando/connexion/pull/760")
    def test_offers_request(self):
        """Test case for offers_request

        
        """
        offers_request = {"schedule":[{"duration":{"min":"min","max":"max"},"start":"start"},{"duration":{"min":"min","max":"max"},"start":"start"}],"resources":{"compute":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}],"data":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}],"storage":[{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}},{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}]},"executable":{"ident":"046b6c7f-0b8a-43b9-b35d-6489e6daee91","name":"name","type":"type","properties":{"key":"properties"}}}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/request',
            method='POST',
            headers=headers,
            data=json.dumps(offers_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
