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
# Implements a simple build script to create a tar.gz package of the output_tcp
# plugin.

. bin/config

printConfiguration

if [ ! -d "$COMPILE_BASE_DIR" ] ; then
    echo "Compile directory $COMPILE_BASE_DIR does not exist"
    exit 1
fi

if [ -f "$PACKAGE_FILE" ] ; then
    echo "Removing old package file"
    rm $PACKAGE_FILE
fi

cd $COMPILE_BASE_DIR

echo "Creating package $PACKAGE_FILE"
tar -czf $PACKAGE_FILE $MODULE_NAME/*.so $MODULE_NAME/mjpg_streamer