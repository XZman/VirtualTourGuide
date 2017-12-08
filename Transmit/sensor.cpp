#include "sensor.h"
#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>
#include <inttypes.h>

typedef struct sockaddr_in sin;

float btof(uint8_t *st) {
    unsigned long tmp = 0;
    float rs;

    for (int i=0;i<4;i++) {
        tmp = tmp << 8;
        tmp += *st;
        st++;
    }
    char *pul = (char *)&tmp;
    char *pf = (char *)&rs;
    memcpy(pf, pul, sizeof(float));
    return rs;
}

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

bool Sensor::getAngle(Angle &a) {
    int recvlen = recv(fd, buf, BUFFSIZE, MSG_DONTWAIT);
    if (recvlen != 15)
        return false;
    a.x = buf[0] - 90;
    a.y = buf[1] - 90;
    a.z = buf[2] - 90;
    a.speed_x = btof(buf+3);
    a.speed_y = btof(buf+7);
    a.speed_z = btof(buf+11);
    return true;
}
