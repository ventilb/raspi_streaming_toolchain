/*******************************************************************************
#                                                                              #
#      MJPG-streamer allows to stream JPG frames from an input-plugin          #
#      to several output plugins                                               #
#                                                                              #
#      Copyright (C) 2007 Tom Stöveken                                         #
#                                                                              #
# This program is free software; you can redistribute it and/or modify         #
# it under the terms of the GNU General Public License as published by         #
# the Free Software Foundation; version 2 of the License.                      #
#                                                                              #
# This program is distributed in the hope that it will be useful,              #
# but WITHOUT ANY WARRANTY; without even the implied warranty of               #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                #
# GNU General Public License for more details.                                 #
#                                                                              #
# You should have received a copy of the GNU General Public License            #
# along with this program; if not, write to the Free Software                  #
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    #
#                                                                              #
*******************************************************************************/
/*
This output plugin is based on code from the mjpg-streamer project and the following files
- output_file.c
- output_http.c
- output_udp.c
- httpd.c

This output plugin sends the jpg images over a TCP connection to a remote server.
TODO: The images should be streamed to the server without opening a connection for each image. Implement a higher level transfer protocol.

Author: Manuel Schulze <manuel_schulze@i-entwicklung.de>
Since.: 02.04.2013

*/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <linux/videodev2.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <signal.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <resolv.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <getopt.h>
#include <pthread.h>
#include <fcntl.h>
#include <time.h>
#include <syslog.h>

#include <dirent.h>

#include "../../utils.h"
#include "../../mjpg_streamer.h"

#define OUTPUT_PLUGIN_NAME "TCP Socket output plugin"

static pthread_t worker;
static globals *pglobal;

/*
Ip-Address and port of the server to send the jpg images to.
*/
static char *server = "127.0.0.1";
static int port = 8080;
/*
The index number into the input plugin array which will act as the image source for this output plugin.
*/
static int input_number = 0;

/*
Buffer to hold a jpg image.
*/
static unsigned char *frame = NULL;

static int max_frame_size;


/******************************************************************************
Description.: print a help message
Input Value.: -
Return Value: -
******************************************************************************/
void help(void)
{
    fprintf(stderr, " ---------------------------------------------------------------\n" \
            " Help for output plugin..: "OUTPUT_PLUGIN_NAME"\n" \
            " ---------------------------------------------------------------\n" \
            " The following parameters can be passed to this plugin:\n\n" \
            " [-s | --server ]........: hostname to upload images to\n" \
            " [-p | --port ]..........: port on server\n" \
            " ---------------------------------------------------------------\n");
}

/******************************************************************************
Description.: clean up allocated ressources
Input Value.: unused argument
Return Value: -
******************************************************************************/
void worker_cleanup(void *arg)
{
    static unsigned char first_run = 1;

    if(!first_run) {
        DBG("already cleaned up ressources\n");
        return;
    }

    first_run = 0;
    OPRINT("cleaning up ressources allocated by worker thread\n");
}

/******************************************************************************
Description.: this is the main worker thread
              it loops forever, grabs a fresh frame and stores it to file
Input Value.:
Return Value:
******************************************************************************/
void *worker_thread(void *arg)
{
    int ok = 1;
    int frame_size = 0;
    unsigned char *tmp_framebuffer = NULL;

    struct sockaddr_in addr;
    int sd;

    bzero(&addr, sizeof(addr));


    /* set cleanup handler to cleanup allocated ressources */
    pthread_cleanup_push(worker_cleanup, NULL);

    // set TCP server data structures ---------------------------
    if(port <= 0) {
        OPRINT("a valid TCP port must be provided\n");
        return NULL;
    }

    addr.sin_addr.s_addr = inet_addr(server);
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    // -----------------------------------------------------------

    struct timeval imageProcessingStart;
    struct timeval imageProcessingStop;

    struct timeval socketStart;
    struct timeval socketStop;

    while (ok >= 0 && !pglobal->stop) {
        //DBG("waiting for fresh frame\n");

        gettimeofday(&imageProcessingStart, NULL);

        pthread_mutex_lock(&pglobal->in[input_number].db);
        pthread_cond_wait(&pglobal->in[input_number].db_update, &pglobal->in[input_number].db);


        /* read buffer */
        frame_size = pglobal->in[input_number].size;

        /* check if buffer for frame is large enough, increase it if necessary */
        if(frame_size > max_frame_size) {
            DBG("increasing buffer size to %d\n", frame_size);

            max_frame_size = frame_size + (1 << 16);
            if((tmp_framebuffer = realloc(frame, max_frame_size)) == NULL) {
                pthread_mutex_unlock(&pglobal->in[input_number].db);
                LOG("not enough memory\n");
                return NULL;
            }

            frame = tmp_framebuffer;
        }

        /* copy frame to our local buffer now */
        memcpy(frame, pglobal->in[input_number].buf, frame_size);

        /* allow others to access the global buffer again */
        pthread_mutex_unlock(&pglobal->in[input_number].db);

        gettimeofday(&imageProcessingStop, NULL);

        printDuration(&imageProcessingStart, &imageProcessingStop, "Image");

        /* Send image */
        gettimeofday(&socketStart, NULL);

        DBG("Create connection to %s:%d\n", server, port);
        sd = socket(AF_INET, SOCK_STREAM, 0);

        // Test
        usleep(200 * 1000);

        if (connect(sd , (struct sockaddr*) &addr , sizeof(addr)) == 0) {
            DBG("Connection to %s:%d established\n", server, port);

            if (!send(sd, frame, frame_size, 0)) {
                perror("Image was not send");
            }

            DBG("Closing connection to %s:%d\n", server, port);
            close(sd);
        } else {
            perror("connect");
        }

        gettimeofday(&socketStop, NULL);

        printDuration(&socketStart, &socketStop, "Socket");
    }

    DBG("Ending TCP worker thread\n");

    /* cleanup now */
    pthread_cleanup_pop(1);

    return NULL;
}


void printDuration(struct timeval *start, struct timeval *end, char * text) {
    #ifdef PRINT_TIMESTAMPS
    OPRINT("%s: Start: %d:%d, End: %d:%d, Duration: %d:%d\n", text, start->tv_sec, start->tv_usec, end->tv_sec, end->tv_usec, end->tv_sec - start->tv_sec, end->tv_usec - start->tv_usec);
    #endif
}

/*** plugin interface functions ***/
/******************************************************************************
Description.: this function is called first, in order to initialize
              this plugin and pass a parameter string
Input Value.: parameters
Return Value: 0 if everything is OK, non-zero otherwise
******************************************************************************/
int output_init(output_parameter *param)
{
	int i;

    param->argv[0] = OUTPUT_PLUGIN_NAME;

    /* show all parameters for DBG purposes */
    for(i = 0; i < param->argc; i++) {
        DBG("argv[%d]=%s\n", i, param->argv[i]);
    }

    reset_getopt();
    while(1) {
        int option_index = 0, c = 0;
        static struct option long_options[] = {
            {"h", no_argument, 0, 0
            },
            {"help", no_argument, 0, 0},
            {"s", required_argument, 0, 0},
            {"server", required_argument, 0, 0},
            {"p", required_argument, 0, 0},
            {"port", required_argument, 0, 0},
            {0, 0, 0, 0}
        };

        c = getopt_long_only(param->argc, param->argv, "", long_options, &option_index);

        /* no more options to parse */
        if(c == -1) break;

        /* unrecognized option */
        if(c == '?') {
            help();
            return 1;
        }

        switch(option_index) {
            /* h, help */
        case 0:
        case 1:
            DBG("case 0,1\n");
            help();
            return 1;
            break;

            /* s, server */
        case 2:
        case 3:
            DBG("case 2,3\n");
            server = malloc(strlen(optarg) + 1);
            strcpy(server, optarg);
            break;

            /* p, port */
        case 4:
        case 5:
            DBG("case 4,5\n");
            port = atoi(optarg);
            break;
        }
    }

    pglobal = param->global;

    OPRINT("server.....: %s\n", server);
    OPRINT("port.......: %d\n", port);


    return 0;
}

/******************************************************************************
Description.: calling this function stops the worker thread
Input Value.: -
Return Value: always 0
******************************************************************************/
int output_stop(int id)
{
    DBG("will cancel worker thread\n");
    pthread_cancel(worker);
    return 0;
}

/******************************************************************************
Description.: calling this function creates and starts the worker thread
Input Value.: -
Return Value: always 0
******************************************************************************/
int output_run(int id)
{
    DBG("launching worker thread\n");
    pthread_create(&worker, 0, worker_thread, NULL);
    pthread_detach(worker);
    return 0;
}
