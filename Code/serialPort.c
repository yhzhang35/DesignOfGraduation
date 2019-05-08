#include"serialPort.h"

//串口初始化函数，
void serialInit()
{
	TMOD=0X20;//用定时器1的模式2
	TH1=TL1=0XFD;//波特率设为9600
	SCON=0X50;//串口方式1，TI和RI清零，允许接收
	TR1=1;  //打开时钟
}

//定义一个发送一个字节的函数
void sendByte(unsigned char dat)
{
	SBUF=dat;
	while(!TI);  //等待完成
	TI=0;
}

//定义一个接收一个字节的函数
unsigned char readByte()
{
	unsigned char dat;
	while(!RI);
	dat=SBUF;
	RI=0;
	return dat;
}
