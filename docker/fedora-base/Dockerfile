#
# Copyright (c) 2024, Manchester (http://www.manchester.ac.uk/)
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# https://docs.fedoraproject.org/en-US/fedora/latest/release-notes/
FROM registry.fedoraproject.org/fedora:42

# https://docs.docker.com/v17.09/engine/reference/builder/#understand-how-arg-and-from-interact
ARG buildtag
ARG buildtime

LABEL maintainer "Dave Morris <docker-admin@metagrid.co.uk>"
LABEL buildtag   "${buildtag}"
LABEL buildtime  "${buildtime}"
LABEL vendor     "UK SKA Regional Centre (UKSRC)"
LABEL version    "${buildtag}"
LABEL license    "GPL-3.0-or-later"
LABEL org.opencontainers.image.created  "${buildtime}"
LABEL org.opencontainers.image.vendor   "UK SKA Regional Centre (UKSRC)"
LABEL org.opencontainers.image.version  "${buildtag}"
LABEL org.opencontainers.image.licenses "GPL-3.0-or-later"
LABEL org.opencontainers.image.source   "https://github.com/ivoa/Calycopis-broker"
LABEL org.opencontainers.image.authors  "https://github.com/Zarquan"

#
# Mount the YUM cache as a volume.
VOLUME /var/cache/dnf
VOLUME /var/cache/yum

#
# Install common admin tools.
# https://github.com/wfau/atolmis/issues/17
# https://github.com/wfau/atolmis/issues/28
RUN dnf install -y \
    bind-utils \
    dateutils \
    diffutils \
    findutils \
    hostname \
    gnupg \
    gzip  \
    less  \
    nano  \
    patch \
    pwgen \
    sed   \
    tar   \
    wget  \
    which \
    zip

#
# Install system monitoring tools.
RUN dnf install -y \
    atop \
    htop \
    iotop \
    procps-ng

#
# Install the SSH client.
RUN dnf install -y \
    openssh-clients

#
# Install the HTTP client.
RUN dnf install -y \
    curl

#
# Install the S3 client
RUN dnf install -y \
    s3cmd

#
# Install JSON, YAML and XML parsers.
RUN dnf install -y \
    jq \
    yq \
    xmlstarlet \
    yamllint

#
# Install the ipcalc tools.
# http://jodies.de/ipcalc
RUN dnf install -y \
    ipcalc

#
# Install rsync
RUN dnf install -y \
    rsync

#
# Install git
RUN dnf install -y \
    git

#
# Add our install scripts.
COPY bin /usr/local/bin/
RUN chmod a+x /usr/local/bin/*.sh

#
# Run our install scripts.
RUN /usr/local/bin/install-locale.sh

