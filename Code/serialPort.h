#ifndef __SERIALPORT_H_
#define __SERIALPORT_H_
#include<reg51.h>

//º¯ÊıÉêÃ÷
void serialInit();
void sendByte(unsigned char dat);
unsigned char readByte();


#endif