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
    float   speed_x;
    float   speed_y;
    float   speed_z;
};

float btof(uint8_t *st);

class Sensor {
private:
    int         fd;
    uint8_t    buf[BUFFSIZE];

public:
    Sensor(int port=23333);
    ~Sensor();

    bool getAngle(Angle &a);
};

#endif
