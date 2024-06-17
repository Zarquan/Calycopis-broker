import unittest

from flask import json

from openapi_server.models.parcolar_request import ParcolarRequest  # noqa: E501
from openapi_server.models.parcolar_response import ParcolarResponse  # noqa: E501
from openapi_server.test import BaseTestCase


class TestAmbleckController(BaseTestCase):
    """AmbleckController integration test stubs"""

    @unittest.skip("Connexion does not support multiple consumes. See https://github.com/zalando/connexion/pull/760")
    def test_ambleck_post(self):
        """Test case for ambleck_post

        
        """
        parcolar_request = {"resources":{"compute":[{"name":"name","type":"type","spec":"{}"},{"name":"name","type":"type","spec":"{}"}],"storage":[{"name":"name","type":"type","spec":"{}"},{"name":"name","type":"type","spec":"{}"}]},"executable":{"name":"name","type":"type","spec":"{}"}}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/ambleck',
            method='POST',
            headers=headers,
            data=json.dumps(parcolar_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
