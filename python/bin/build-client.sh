#!/bin/sh
#
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2025, Manchester (http://www.manchester.ac.uk/)
#
#     This information is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This information is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#
#

# -----------------------------------------------------
# Build the Python client classes.

    generatorName=openapi-generator-cli
    generatorVersion=7.14.0
    generatorJarFile=${generatorName}-${generatorVersion}.jar
    generatorPath=/opt/openapi-generator
    generatorFullPath=${generatorPath}/${generatorJarFile}

    #
    # Check if the generator is instralled.
    if [ ! -d "${generatorPath}" ]
    then
        mkdir "${generatorPath}"
    fi

    if [ ! -e "${generatorFullPath}" ]
    then

        which wget &> /dev/null
        if [ $? -ne 0 ]
        then
            dnf install -y \
                wget
        fi

        wget https://repo1.maven.org/maven2/org/openapitools/${generatorName}/${generatorVersion}/${generatorJarFile} \
             --output-document "${generatorFullPath}"
    fi

    #
    # Generate our Python client classes.
    # https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md

    source=/trebula/target/Calycopis-broker-full.yaml
    target=/calycopis/python/broker/client

    rm -rf "${target:?}"
    mkdir --parents "${target:?}"

    java -jar "${generatorFullPath}" \
        generate \
        --generator-name python \
        --input-spec "${source}" \
        --output     "${target}" \
        --package-name "broker_client"


    #
    # Generate our FastAPI service classes.
    # https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md

    source=/trebula/target/Calycopis-broker-full.yaml
    target=/calycopis/python/broker/fastapi

    rm -rf "${target:?}"
    mkdir --parents "${target:?}"

    java -jar "${generatorFullPath:?}" \
        generate \
        --generator-name python-fastapi \
        --input-spec "${source:?}" \
        --output     "${target:?}" \
        --package-name "broker_fastapi"


    #
    # Generate our Java client classes.
    # https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md

    source=/trebula/target/Calycopis-broker-full.yaml
    target=/calycopis/java/broker/client

    rm -rf "${target:?}"
    mkdir --parents "${target:?}"

    java -jar "${generatorFullPath}" \
        generate \
        --generator-name java \
        --input-spec "${source}" \
        --output     "${target}" \
        --api-package "net.ivoa.calycopis.openapi.webapp" \
        --model-package "net.ivoa.calycopis.openapi.model" \
        --model-name-prefix "Ivoa"


    #
    # Generate our Spring service classes.
    # https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md

    source=/trebula/target/Calycopis-broker-full.yaml
    target=/calycopis/java/broker/spring

    rm -rf "${target:?}"
    mkdir --parents "${target:?}"

    java -jar "${generatorFullPath}" \
        generate \
        --generator-name spring \
        --input-spec "${source}" \
        --output     "${target}" \
        --api-package "net.ivoa.calycopis.openapi.webapp" \
        --model-package "net.ivoa.calycopis.openapi.model" \
        --model-name-prefix "Ivoa"

