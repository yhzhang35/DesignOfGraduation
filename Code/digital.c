#include<digital.h>
 
 
 //定义数码馆显示数字表
 unsigned char numbers[10]={0x00};
 
 void exhibition(unsigned char count)
 {

	 
	 //一边转过的圈数少于十圈，所以只用了一个数码管来显示10以内的数字

		com1=EN;
		NUMBER=numbers[count%10];
		shumaDelay(100);
		
 }

 void shumaDelay(unsigned int i)
 {
		while(i--);
 }