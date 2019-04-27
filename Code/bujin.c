#include<reg51.h>
#include<bujin.h>
/*
功能：实现对步进电机的控制
参数：角位移，方向，速度
*/

code int positiveRe[8]={0x08,0x0c,0x04,0x06,0x02,0x03,0x01,0x09};

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
		P2=0X00;
		if(direction)
		switch(i%8)
		{
			case 0:PORT=(PORT&0XF0)|positiveRe[0];break;
			case 1:PORT=(PORT&0XF0)|positiveRe[1];break;
			case 2:PORT=(PORT&0XF0)|positiveRe[2];break;
			case 3:PORT=(PORT&0XF0)|positiveRe[3];break;
			case 4:PORT=(PORT&0XF0)|positiveRe[4];break;
			case 5:PORT=(PORT&0XF0)|positiveRe[5];break;
			case 6:PORT=(PORT&0XF0)|positiveRe[6];break;
			case 7:PORT=(PORT&0XF0)|positiveRe[7];break;
		}
		else
			switch(i%8)
				{
			case 7:PORT=(PORT&0XF0)|positiveRe[0];break;
			case 6:PORT=(PORT&0XF0)|positiveRe[1];break;
			case 5:PORT=(PORT&0XF0)|positiveRe[2];break;
			case 4:PORT=(PORT&0XF0)|positiveRe[3];break;
			case 3:PORT=(PORT&0XF0)|positiveRe[4];break;
			case 2:PORT=(PORT&0XF0)|positiveRe[5];break;
			case 1:PORT=(PORT&0XF0)|positiveRe[6];break;
			case 0:PORT=(PORT&0XF0)|positiveRe[7];break;
				}
		bujinDelay(time*1000);  //延长一定时间用以调节速度
	}
}
	
	void bujinDelay_n_ms(unsigned int i)
	{
		//延时函数，根据输入的数字延时相应毫秒
	}
	
