#include<reg51.h>
#include<bujin.h>
#include<remoteController.h>
#include<Lcd1602.h>
#include<keyBoard.h>
#include"serialPort.h"


/********************************************************************************************
功能： 该程序是通过自写的上位机实现步进电机的控制，可以控制步进电机的转向，转动位移，转动速度，并能通过红外热感感应到人实现
步进电机的急停。并能将相关数据上传到电脑，用以文件的形势来保存这些数据


**********************************************************************************************/
void delay(unsigned int i);
//将接收到的数据进行转换
void convert();

//中断初始化函数
void interInit();

//定义全局变量
bit direction=0;
double arc=12.70;
double speed=2.2;
unsigned char dat[11];


//以下主程序我列出了主要的框架，还要通过以后的编写来实现
void main()
{
	bit flag=0;

	unsigned char i;
	unsigned char symbal;
	
	
	//串口初始化
	serialInit();
	//lcd1602初始化
	lcdInitial();
	//中断初始化
	interInit();
	
	P0=0x77;
	while(1)
	{

		//通过串口读取相关控制数据
		if(flag==0)
		{
			if(readByte()=='*')
			{
				for(i=0;i<11;i++)
				{
					dat[i]=readByte();
					
				}
				flag=1;
			}
			sendByte('*');
			for(i=0;i<11;i++)
				{
					sendByte(dat[i]);
					
				}

		}
		
		
		if(flag)
		{
		convert();  //处理接收到的数据，将其转换到对应的弧度和速度。 
		
		//通过lcd1602显示收到的数据
		processData(arc,direction,speed,dat);
		display(dat);
			
		//调用步进电机运行程序   
		 run(arc,direction,speed);
			
			//处理标志
			sendByte('*');
			flag=0;
		}
		
		
		//在运行过程中存在两个中断  一个是通过按键实现的普通暂停中断 另一个是保障人身安全的急停中断
		//如果走过一圈更新数码管显示
	}
	
}

void delay(unsigned int i)
{
	while(--i);
}

//实现数据的转换
//输入格式比如 22.22522.22
void convert()
{
	unsigned int temp=0;
	unsigned char i;
	//处理弧度
	arc=0.0;
	for(i=0;i<5;i++)
	{
		if(dat[i]=='.')
			continue;
		temp*=10;
		temp+=(dat[i]-'0');
		
	}
	arc=temp;
	arc=arc*0.01;
	
	//处理方向
	if((dat[5]-'0')>0)
		direction =1;
	else 
		direction =0;
	
	
	//处理速度
	speed=0.0;
	temp=0;
	for(i=0;i<5;i++)
	{
		if(dat[i+6]=='.')
			continue;
		temp*=10;
		temp+=(dat[6+i]-'0');
	}
	speed=temp;
	speed=speed*0.01;
}

//中断初始化函数
void interInit()
{
	//初始化中断0
	IT0=1; //边沿触发方式
	EX0=1; //开中断
	EA=1;
}

//中断函数
void Stop() interrupt 0
{
	EA=0;
	while(!IE0);
	IE0=0;
	EA=1;
	
}
