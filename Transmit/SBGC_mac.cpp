#include "SBGC_mac.h"
#include "SBGC_lib/SBGC.h"
#include "serial/serial.h"
#include <string>


void PiComObj::init(char *device, int baud_rate) {
    srl = new serial::Serial(device, baud_rate, serial::Timeout::simpleTimeout(1000));
}

uint16_t PiComObj::getBytesAvailable() {
    return (uint16_t)srl->available();
}

uint8_t PiComObj::readByte() {
    uint8_t rs;
    srl->read(&rs,1);
    return (uint8_t)rs;
}

void PiComObj::writeByte(uint8_t b) {
    uint8_t *rs = new uint8_t;
    *rs = b;
    srl->write(rs, 1);
}

