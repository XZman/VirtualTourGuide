#include "SBGC_pi.h"
#include "SBGC_lib/SBGC.h"
#include <wiringSerial.h>

void PiComObj::init(char *device, int baud_rate) {
    fd = serialOpen(device, baud_rate);
}

uint16_t PiComObj::getBytesAvailable() {
    int rs = serialDataAvail(fd);
    if (rs != -1)
        return (uint16_t)rs;
    else
        return 0;
}

uint8_t PiComObj::readByte() {
    int rs = serialGetchar(fd);
    if (rs != -1)
        return (uint8_t)rs;
    else
        return 0;
}

void PiComObj::writeByte(uint8_t b) {
    unsigned char q = (unsigned char)b;
    serialPutchar(fd, q);
}

