#!/bin/bash

###############################################################################
# raspi_streaming_toolchain - mjpg-streamer
# Copyright (C) 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#
###############################################################################
#
# Author: Manuel Schulze <manuel_schulze@i-entwicklung.de>
# Since.: 04.04.2013 - 20:12
#
# Install script to download, compile and install mjpg-streamer and the
# output_tcp plugin on a Raspberry PI. Deploy this script onto your Raspberry PI
# and run to reinstall the mjpg-streamer software.
#
# I.e. copy this script to /usr/local/bin on your Rasperry PI.

PACKAGE_BASE=mjpg-streamer.tar.gz
PACKAGE=/tmp/mjpg-streamer-src/$PACKAGE_BASE
INSTALL_DIR=/opt

cd /tmp

if [ -d "raspi_streaming_toolchain" ] ; then
    rm -rf raspi_streaming_toolchain
fi

git clone git://github.com/ventilb/raspi_streaming_toolchain.git

cd raspi_streaming_toolchain/mjpg-streamer

echo `pwd`

bash ./bin/compile.sh
bash ./bin/package.sh

if [ -f "$PACKAGE" ] ; then
    echo "Installing mjpg-streamer package into $INSTALL_DIR"

    sudo mv "$PACKAGE" "$INSTALL_DIR"

    cd $INSTALL_DIR

    sudo tar -xzf $INSTALL_DIR/$PACKAGE_BASE
fi

