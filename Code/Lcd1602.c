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
	lcdWriteCommon(0x80);  //设置起点
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
void display(unsigned char dat[11])
{
	unsigned char address=0;
	unsigned char i=0;
	unsigned char str1[]="arc:";
	unsigned char str2[]="dir:";
	unsigned char str3[]="v:";
	
		address+=0x80;//第1行的起始地址加上了相关控制位
	//显示弧度
		lcdWriteCommon(address);
		for(i=0;i<4;i++)
			{
				lcdWriteData(str1[i]);
			}
		for(i=0;i<5;i++)
			{
				lcdWriteData(dat[4-i]);
			}
			
			//显示方向
		lcdWriteCommon(address+11);		
		for(i=0;i<4;i++)
			{
				lcdWriteData(str2[i]);
			}
			lcdWriteData(dat[5]);
			
		address=0x00;
		address+=0xc0;//第2行的起始地址加上了相关控制位
			//显示角速度
		lcdWriteCommon(address);
		for(i=0;i<2;i++)
			{
				lcdWriteData(str3[i]);
			}
			for(i=0;i<5;i++)
			{
				lcdWriteData(dat[10-i]);
			}
}
//延时函数
void delay40us(unsigned char time)
{
	unsigned char i,j;
	for(i=time ;i>0;i--)
		for(j=0;j<2;j++);
						
}

//数据处理函数
void processData(double arc,bit direction,double speed,unsigned char dat[11])
{
	unsigned int temp=0;
	unsigned int i;
	
	//处理弧度
	temp=arc*100;
	for(i=0;i<5;i++)
	{
		if(i==2)
		{
			dat[i]='.';
			continue;
		}
		dat[i]=temp%10+'0';
		temp=temp/10;
	}
	
	//处理方向
	if(direction==1)
		dat[i++]='S';
	else
		dat[i++]='N';
	
	//处理速度
	temp=0;
	temp=speed*100;
	for(;i<11;i++)
	{
		if(i==8)
		{
			dat[i]='.';
			continue;
		}
		dat[i]=temp%10+'0';
		temp=temp/10;
	}
}
