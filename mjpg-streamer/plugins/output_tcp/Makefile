###############################################################
#
# Purpose: Makefile for "M-JPEG Streamer"
# Author.: Tom Stoeveken (TST)
# Version: 0.3
# License: GPL
#
###############################################################
#
# This file was adapted from the mjpg-streamer project to compile the
# output_tcp plugin.
#
# Author: Manuel Schulze <manuel_schulze@i-entwicklung.de>
#
CC = gcc

OTHER_HEADERS = ../../mjpg_streamer.h ../../utils.h ../output.h ../input.h

CFLAGS += -O2 -DLINUX -D_GNU_SOURCE -Wall -shared -fPIC
#CFLAGS += -DDEBUG
#CFLAGS += -DPRINT_TIMESTAMPS
LFLAGS += -lpthread -ldl

all: output_tcp.so

clean:
	rm -f *.a *.o core *~ *.so *.lo

output_tcp.so: $(OTHER_HEADERS) output_tcp.c
	$(CC) $(CFLAGS) -o $@ output_tcp.c
