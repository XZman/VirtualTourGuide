#ifndef SGBC_MAC_H
#define SGBC_MAC_H

#include "SBGC_lib/SBGC.h"
#include "stdint.h"
#include "serial/serial.h"

void SBGC_setup(char *device);

class PiComObj : public SBGC_ComObj {
private:
    serial::Serial *srl;
public:
    void init(char *device, int baud_rate=9600);

    virtual uint16_t getBytesAvailable();
    virtual uint8_t readByte();
    virtual void writeByte(uint8_t b);
    virtual uint16_t getOutEmptySpace() { return 0xFFFF; }
};
#endif
