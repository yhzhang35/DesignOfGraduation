package serialPort;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import serialException.ExceptionWriter;

/**
 * 主程序
 * @author zhong
 *
 */
public class Client extends Frame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 800;
	
	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 620;
	
	/**
	 * 程序界面出现位置（横坐标）
	 */
	public static final int LOC_X = 200;
	
	/**
	 * 程序界面出现位置（纵坐标）
	 */
	public static final int LOC_Y = 70;

	Color color = Color.WHITE; 
	Image offScreen = null;	//用于双缓冲
	
	//设置window的icon（这里我自定义了一下Windows窗口的icon图标，因为实在觉得哪个小咖啡图标不好看 = =）
	Toolkit toolKit = getToolkit();
	Image icon = toolKit.getImage(Client.class.getResource("computer.png"));
	
	//持有其他类
	DataView dataview = new DataView(this);	//主界面类（显示监控数据主面板）

	/**
	 * 主方法
	 * @param args	//
	 */
	public static void main(String[] args) {
		new Client().launchFrame();	
	}
	
	/**
	 * 显示主界面
	 */
	public void launchFrame() {
		this.setBounds(LOC_X, LOC_Y, WIDTH, HEIGHT);	//设定程序在桌面出现的位置
		this.setTitle("单片机工程项目");	//设置程序标题
		this.setIconImage(icon);
		this.setBackground(Color.yellow);	//设置背景色
		
		this.addWindowListener(new WindowAdapter() {
			//添加对窗口状态的监听
			public void windowClosing(WindowEvent arg0) {
				//当窗口关闭时
				System.exit(0);	//退出程序
			}
			
		});

		this.addKeyListener(new KeyMonitor());	//添加键盘监听器
		this.setResizable(false);	//窗口大小不可更改
		this.setVisible(true);	//显示窗口
			
		new Thread(new RepaintThread()).start();	//开启重画线程
	}
	
	/**
	 * 画出程序界面各组件元素
	 */
	public void paint(Graphics g) {
		Color c = g.getColor();
		
		g.setFont(new Font("微软雅黑", Font.BOLD, 40));
		g.setColor(Color.black);
		g.drawString("欢迎使用上位机实时监控系统", 45, 190);
		
		g.setFont(new Font("微软雅黑", Font.ITALIC, 26));
		g.setColor(Color.BLACK);
		g.drawString("Version：1.0   Powered By：ZhangYunHai", 260, 260);
		
		g.setFont(new Font("微软雅黑", Font.BOLD, 30));
		g.setColor(color);
		g.drawString("————点击Enter键进入主界面————", 100, 480);
		//使文字 "————点击Enter键进入主界面————" 黑白闪烁
		if (color == Color.WHITE)	color = Color.black;
		else if (color == color.BLACK)	color = Color.white;
		
		
	}
	
	/**
	 * 双缓冲方式重画界面各元素组件
	 */
	public void update(Graphics g) {
		if (offScreen == null)	offScreen = this.createImage(WIDTH, HEIGHT);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, WIDTH, HEIGHT);	//重画背景画布
		this.paint(gOffScreen);	//重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null);	//将新画好的画布“贴”在原画布上
	}
	
	/*
	 * 内部类形式实现对键盘事件的监听
	 */
	private class KeyMonitor extends KeyAdapter {

		//这儿是方法重写
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) {	//当监听到用户敲击键盘enter键后执行下面的操作
				setVisible(false);	//隐去欢迎界面
				dataview.setVisible(true);	//显示监测界面
				dataview.dataFrame();	//初始化监测界面
			}
		}
		
	}
	
	
	/*
	 * 重画线程（每隔250毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					//重画线程出错抛出异常时创建一个Dialog并显示异常详细信息
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
		
	}
	
}
