#!/bin/sh
#
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2024, Manchester (http://www.manchester.ac.uk/)
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
# Set the build date and repository name.
#[user@desktop]

    buildtag=$(date '+%Y.%m.%d')
    reponame="ghcr.io"
    repopath="${reponame}/ivoa"

# -----------------------------------------------------
# Check our secret function works.
#[user@desktop]

    getsecret example.frog

# -----------------------------------------------------
# Login to the Docker registry.
#[user@desktop]

    guser=$(getsecret 'devops.ghcrio.user')
    gpass=$(getsecret 'devops.ghcrio.pass')

    echo "${gpass:?}" \
    | podman login \
        --password-stdin \
        --username "${guser:?}" \
        "${reponame}"


# -----------------------------------------------------
# Push our images to our GitHub repository.
#[user@desktop]

    pushtag()
        {
        local imagename=${1:?}
        local imagetag=${2:?}
        podman tag \
            "${imagename:?}:${imagetag:?}" \
            "${repopath:?}/${imagename:?}:${imagetag:?}"
        podman push \
            "${repopath:?}/${imagename:?}:${imagetag:?}"
        }

    pushtag "calycopis/fedora-base" "latest"
    pushtag "calycopis/fedora-base" "${buildtag:?}"

    pushtag "calycopis/java-builder" "latest"
    pushtag "calycopis/java-builder" "${buildtag:?}"


