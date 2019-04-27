#ifndef __DIGITAL_H_
#define __DIGITAL_H_
#include<reg51.h>

//定义两个控制端
sbit com1=P2^1;


//定义使能电位
#define EN 1

//定义数据输出io口
#define NUMBER P1

//申明相关函数
 void exhibition(unsigned char count);
  void shumaDelay(unsigned int i);

#endif