# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from openapi_server.apis.default_api_base import BaseDefaultApi
import openapi_server.impl

from fastapi import (  # noqa: F401
    APIRouter,
    Body,
    Cookie,
    Depends,
    Form,
    Header,
    Path,
    Query,
    Response,
    Security,
    status,
)

from openapi_server.models.extra_models import TokenModel  # noqa: F401
from openapi_server.models.execution_status_response import ExecutionStatusResponse
from openapi_server.models.execution_update_request import ExecutionUpdateRequest
from openapi_server.models.offers_request import OffersRequest
from openapi_server.models.offers_response import OffersResponse


router = APIRouter()

ns_pkg = openapi_server.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.get(
    "/execution/{ident}",
    responses={
        200: {"model": ExecutionStatusResponse, "description": "OK"},
    },
    tags=["default"],
    response_model_by_alias=True,
)
async def execution_status(
    ident: str = Path(..., description="The execution identifier"),
) -> ExecutionStatusResponse:
    ...


@router.post(
    "/execution/{ident}",
    responses={
        200: {"model": ExecutionStatusResponse, "description": "OK"},
    },
    tags=["default"],
    response_model_by_alias=True,
)
async def execution_update(
    ident: str = Path(..., description="The execution identifier"),
    execution_update_request: ExecutionUpdateRequest = Body(None, description=""),
) -> ExecutionStatusResponse:
    ...


@router.post(
    "/request",
    responses={
        200: {"model": OffersResponse, "description": "OK"},
    },
    tags=["default"],
    response_model_by_alias=True,
)
async def offers_request(
    offers_request: OffersRequest = Body(None, description=""),
) -> OffersResponse:
    ...
