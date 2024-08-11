

import requests as http_requests

cert = "cadcproxy.pem"
name = "albert"
image = "images.canfar.net/skaha/base-notebook:latest"

url = "https://ws-uv.canfar.net/skaha/v0/session"
info = {"name":name, "image":image}
r = http_requests.post(url, cert=cert, data=info)


Swagger UI
https://ws-uv.canfar.net/skaha/#/


https://ws-uv.canfar.net/session/notebook/cxjyor0y/lab/tree/arc/home/Zarquan

https://ws-uv.canfar.net/session/desktop/pquvztl3/?password=pquvztl3&path=session/desktop/pquvztl3/


#
# Can we see the user's home directory ?


