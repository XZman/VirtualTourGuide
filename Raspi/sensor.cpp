#include "sensor.h"
#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <inttypes.h>

typedef struct sockaddr_in sin;

Sensor::Sensor(int port) {
    if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        perror("cannot create socket");
        delete this;
        return;
    }
    sin *myaddr = new sin();
    myaddr->sin_family = AF_INET;
    myaddr->sin_addr.s_addr = htonl(INADDR_ANY);
    myaddr->sin_port = htons(port);
    if (bind(fd, (sockaddr*)myaddr, sizeof(*myaddr)) < 0) {
        perror("cannot bind");
        delete this;
        return;
    }
    printf("Bind successfully with 0.0.0.0:%d\n", port);
}

Sensor::~Sensor() {
    shutdown(fd, SHUT_RDWR);
    printf("Successfully shutdown the socket");
    delete this;
}

void Sensor::getAngle(Angle &a) {
    int recvlen = recv(fd, buf, BUFFSIZE, 0);
    a.x = buf[0];
    a.y = buf[1];
    a.z = buf[2];
}
