#ifndef __LCD1602_H_
#define __LCD1602_H_

#define INTERFACE P1
sbit lcdRS=P2^3;
sbit lcdRW=P2^1;
sbit lcdE=P2^0;

//相关函数申明
void lcdInitial();
void lcdWriteCommon(unsigned char common);
void lcdWriteData(int dat);
void display(unsigned char dat[11]);
void delay40us(unsigned char time);
void processData(double arc,bit direction,double speed,unsigned char dat[11]);
#endif