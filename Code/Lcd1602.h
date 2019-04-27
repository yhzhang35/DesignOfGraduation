#ifndef __LCD1602_H_
#define __LCD1602_H_

#define INTERFACE P0
sbit lcdRS=P2^6;
sbit lcdRW=P2^5;
sbit lcdE=P2^7;

//相关函数申明
void lcdInitial();
void lcdWriteCommon(unsigned char common);
void lcdWriteData(int dat);
void display(unsigned char x,unsigned char y,unsigned char dat);
void delay40us(unsigned char time);
#endif