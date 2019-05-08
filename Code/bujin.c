#include<reg51.h>
#include<bujin.h>
/*
功能：实现对步进电机的控制
参数：角位移，方向，速度
*/
//定义步进电机时序图
code int positiveRe[8]={0x80,0xc0,0x40,0x60,0x20,0x30,0x10,0x90};

//定义数码管显示数组
code unsigned char numbers[10]={0x77,0x14,0xb3,0xb6,0xd4,0xe6,0xef,0x34,0xf7,0xf6};

void run(double arc,bit direction,double speed)
{
	
	unsigned int count; 
	double time;
	unsigned int i;

	//用弧度计算出步数
	count=(arc/3.14*180)/STEP;
	//用转速计算出总的用时
	time=arc/speed;
	//计算每一步大约需要的延时
	time=time/count;

	//实现脉冲交替变化
	for(i=0;i<count;i++)
	{
		if(direction)
		switch(i%8)
		{
			case 0:PORT=(PORT&0X0F)|positiveRe[0];break;
			case 1:PORT=(PORT&0X0F)|positiveRe[1];break;
			case 2:PORT=(PORT&0X0F)|positiveRe[2];break;
			case 3:PORT=(PORT&0X0F)|positiveRe[3];break;
			case 4:PORT=(PORT&0X0F)|positiveRe[4];break;
			case 5:PORT=(PORT&0X0F)|positiveRe[5];break;
			case 6:PORT=(PORT&0X0F)|positiveRe[6];break;
			case 7:PORT=(PORT&0X0F)|positiveRe[7];break;
		}
		else
			switch(i%8)
				{
			case 7:PORT=(PORT&0X0f)|positiveRe[0];break;
			case 6:PORT=(PORT&0X0f)|positiveRe[1];break;
			case 5:PORT=(PORT&0X0f)|positiveRe[2];break;
			case 4:PORT=(PORT&0X0f)|positiveRe[3];break;
			case 3:PORT=(PORT&0X0f)|positiveRe[4];break;
			case 2:PORT=(PORT&0X0f)|positiveRe[5];break;
			case 1:PORT=(PORT&0X0f)|positiveRe[6];break;
			case 0:PORT=(PORT&0X0f)|positiveRe[7];break;
				}
		
				//判断是否转过了一圈
			if(i%COUNT==0)
					P0=numbers[i/COUNT];
		//bujinDelay_n_ms(100);  //延长一定时间用以调节速度
			bujinDelay_n_100us((unsigned int)(time*10000));
	}
}
	//延时相应时间
	void bujinDelay_n_100us(unsigned int i)
	{
		while(--i)
		{
			delay100us();
		}
	}
	
void delay100us(void)   //误差 -0.173611111111us
{
    unsigned char a,b;
    for(b=3;b>0;b--)
        for(a=13;a>0;a--);
}


	
