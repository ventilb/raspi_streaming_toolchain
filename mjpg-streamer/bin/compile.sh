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
# Implements a simple build script to compile the output_tcp plugin and
# mjpg-streamer.

. bin/config

printConfiguration

if [ -d "$COMPILE_BASE_DIR" ] ; then
    echo "Removing old compile base dir"
    rm -rf $COMPILE_BASE_DIR
fi

mkdir $COMPILE_BASE_DIR
cd $COMPILE_BASE_DIR

echo "Downloading mjpg-streamer sources from SVN"
svn co https://mjpg-streamer.svn.sourceforge.net/svnroot/mjpg-streamer/mjpg-streamer $MODULE_NAME

echo "Copying output_tcp plugin into mjpg-streamer sources"
cp -R "$CURRENT_WORKING_DIR" "$COMPILE_BASE_DIR"

cd $MODULE_NAME

echo "Patching Makefile"
patch -p0 Makefile plugins/output_tcp/patches/__Patch_for_mjpg-streamer_Makefile_to_compile_our_output_tcp_plugin.patch
patch -p0 plugins/input_uvc/input_uvc.c plugins/output_tcp/patches/__Thread_sleep_time_reduced_by_100ms.patch

echo "Compiling mjpg-streamer"
make