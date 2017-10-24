#include <stdio.h>
#include "SBGC_lib/SBGC.h"
#include "SBGC_pi.h"
#include "sensor.h"
#include <wiringPi.h>
#include <wiringSerial.h>
#include <sys/socket.h>
#include <iostream>

SBGC_Parser sbgc_parser;
PiComObj    pi_obj;
Angle       a;

SBGC_cmd_control_t c = { 0, 0, 0, 0, 0, 0, 0 };

void setup(char *device) {
    pi_obj.init(device);
    sbgc_parser.init(&pi_obj);
    c.mode = SBGC_CONTROL_MODE_ANGLE;
}

int main(int argc, char *argv[]) {
    setup(argv[1]);
    Sensor *s = new Sensor();
    if (s==NULL)
        return 0;
    while (true) {
        s->getAngle(a);
        printf("%u %u %u\n", a.x, a.y, a.z);
        c.anglePITCH    =   SBGC_DEGREE_TO_ANGLE(a.x);
        c.angleROLL     =   SBGC_DEGREE_TO_ANGLE(a.y);
        c.angleYAW      =   SBGC_DEGREE_TO_ANGLE(a.z);
        SBGC_cmd_control_send(c, sbgc_parser);
    }
    return 0;
}
