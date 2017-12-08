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
    std::string str = srl->read();
    uint8_t rs = str[0];
    return (uint8_t)rs;
}

void PiComObj::writeByte(uint8_t b) {
    srl->write(&b, 1);
}

