# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.execution_status_response import ExecutionStatusResponse
from openapi_server.models.execution_update_request import ExecutionUpdateRequest
from openapi_server.models.offers_request import OffersRequest
from openapi_server.models.offers_response import OffersResponse


class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)
    def execution_status(
        self,
        ident: str,
    ) -> ExecutionStatusResponse:
        ...


    def execution_update(
        self,
        ident: str,
        execution_update_request: ExecutionUpdateRequest,
    ) -> ExecutionStatusResponse:
        ...


    def offers_request(
        self,
        offers_request: OffersRequest,
    ) -> OffersResponse:
        ...
