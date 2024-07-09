import httpx
import yaml
import xmltodict
import json

class ParcolarClient:
    def __init__(self, base_url: str):
        self.base_url = base_url

    def _parse_response(self, response: httpx.Response):
        if 'application/json' in response.headers['Content-Type']:
            return response.json()
        elif 'application/yaml' in response.headers['Content-Type']:
            return yaml.safe_load(response.text)
        elif 'application/xml' in response.headers['Content-Type']:
            return xmltodict.parse(response.text)
        else:
            response.raise_for_status()

    def _prepare_payload(self, data, content_type):
        if content_type == 'application/json':
            return json.dumps(data), content_type
        elif content_type == 'application/yaml':
            return yaml.dump(data), content_type
        elif content_type == 'application/xml':
            # Wrap data in a root element
            return xmltodict.unparse({'ParcolarRequest': data}, pretty=True), content_type
        else:
            raise ValueError(f"Unsupported content type: {content_type}")

    def post_ambleck(self, data, content_type='application/json', accept='application/json'):
        payload, content_type = self._prepare_payload(data, content_type)
        headers = {'Content-Type': content_type, 'Accept': accept}
        response = httpx.post(f"{self.base_url}/ambleck", headers=headers, content=payload)
        return self._parse_response(response)

if __name__ == "__main__":
    client = ParcolarClient(base_url="http://localhost:8000")

    # Sample data
    request_data = {
        "executable": {
            "type": "urn:ping-executable",
            "name": "Ping Test",
            "spec": {
                "target": "example.com"
            }
        },
        "resources": {
            "compute": [],
            "storage": []
        }
    }

    # Send request in JSON format and accept JSON response
    response = client.post_ambleck(request_data, content_type='application/json', accept='application/json')
    print("JSON Response:", response)

    # Send request in YAML format and accept YAML response
    response = client.post_ambleck(request_data, content_type='application/yaml', accept='application/yaml')
    print("YAML Response:", yaml.dump(response, sort_keys=False))

    # Send request in XML format and accept XML response
    response = client.post_ambleck(request_data, content_type='application/xml', accept='application/xml')
    print("XML Response:", xmltodict.unparse(response, pretty=True))

