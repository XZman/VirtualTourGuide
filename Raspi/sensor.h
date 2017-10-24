#ifndef SENSOR_H
#define SENSOR_H

#define BUFFSIZE 128

#include <sys/socket.h>
#include <sys/types.h>
#include <inttypes.h>

struct Angle {
    uint8_t x;
    uint8_t y;
    uint8_t z;
};

class Sensor {
private:
    int         fd;
    uint8_t    buf[BUFFSIZE];

public:
    Sensor(int port=23333);
    ~Sensor();

    void getAngle(Angle &a);
};

#endif
