#include <stdio.h>
#include "SBGC_lib/SBGC.h"
#include "SBGC_mac.h"
#include "sensor.h"
#include <sys/socket.h>
#include <iostream>
#include <unistd.h>
#include <ctime>

SBGC_Parser sbgc_parser;
PiComObj    pi_obj;
Angle       a;

SBGC_cmd_control_t c = { 0, 0, 0, 0, 0, 0, 0 };


void setup(char *device) {
    pi_obj.init(device,115200);
    sbgc_parser.init(&pi_obj);
    c.mode = SBGC_CONTROL_MODE_SPEED_ANGLE;
}

int main(int argc, char *argv[]) {
    setup(argv[1]);
    Sensor *s = new Sensor();
    if (s==NULL)
        return 0;
    for (int i = 0; i < 128; i++) {
        if(!s->getAngle(a))
            continue;
        //a.x = 0;
        //a.y = 0;
        //a.z = 0;
        printf("%u %u %u %.6f %.6f %.6f\n", a.x, a.y, a.z, a.speed_x, a.speed_y, a.speed_z);
        c.anglePITCH    =   SBGC_DEGREE_TO_ANGLE((int8_t)a.z);
        c.angleROLL     =   SBGC_DEGREE_TO_ANGLE(-(int8_t)a.y);
        c.angleYAW      =   SBGC_DEGREE_TO_ANGLE((int8_t)a.x);
        c.speedPITCH    =   a.speed_z * SBGC_SPEED_SCALE;
        c.speedROLL     =   a.speed_y * SBGC_SPEED_SCALE;
        c.speedPITCH    =   a.speed_x * SBGC_SPEED_SCALE;
        printf("return val: %u \n",SBGC_cmd_control_send(c, sbgc_parser));
        printf("command sent\n");
        //usleep(400 * 1000);
    }
    return 0;
}
