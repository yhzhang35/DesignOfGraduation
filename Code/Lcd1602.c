#include<reg51.h>
#include<Lcd1602.h>

/*
初始化函数
*/
void lcdInitial()
{
	
	lcdWriteCommon(0x38);//设置8位格式，两行
	lcdWriteCommon(0x0c);//整体显示，关光标.不闪烁
	lcdWriteCommon(0x06);//设定输入方式，增量不移位
	lcdWriteCommon(0x01);//清除屏幕显示
	//lcdWriteCommon(0x80);  //设置起点
	delay40us(200);//等待清屏完成
	
}

//向芯片中写入数据
//上升沿触发数据进入
void lcdWriteCommon(unsigned char common)
{
	
	lcdE=0;  //使能
	lcdRS=0;  //写入数据
	lcdRW=0;  //写入命令
	
	INTERFACE=common;  //传输数据
	delay40us(50);
	
	lcdE=1;   //上升沿触发
	delay40us(5);
	lcdE=0;//重新使能



}

//写入数据
void lcdWriteData(unsigned char dat)
{
	lcdE=0; //使能
	lcdRS=1; //选择数据
	lcdRW=0; //选择写入
	
	INTERFACE=dat;  //传输数据
	delay40us(50);  //等待数据稳定
	
	lcdE=1;
	delay40us(5);
	lcdE=0;  //重新使能
	
}

//显示相关字符
void display(unsigned char x,unsigned char y,unsigned char dat)
{
	unsigned char address=0;
	
	if(y==1)
		address+=0x80;//第1行的起始地址加上了相关控制位
	else
		address+=0xc0;//第2行的起始地址加上了相关控制位
	address+=x;
	
	lcdWriteCommon(address);
	lcdWriteData(dat);
}
//延时函数
void delay40us(unsigned char time)
{
	unsigned char i,j;
	for(i=time ;i>0;i--)
		for(j=0;j<2;j++);
						
}
