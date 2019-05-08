package serialPort;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.JOptionPane;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serialException.*;

/**
 * 监测数据显示类
 * @author Zhong
 *
 */
public class DataView extends Frame {
		
	/**
	 * 
	 */
	//定义参数
	double distance=0.0;
	double v=0.0;
	int dir=0;
	
	
	private static final long serialVersionUID = 1L;

	Client client = null;

	private List<String> commList = null;	//保存可用端口号
	private SerialPort serialPort = null;	//保存串口对象
	
	private Font font = new Font("微软雅黑", Font.BOLD, 25);
	
	private Label dis = new Label("暂无数据", Label.CENTER);	//温度
	private Label speed = new Label("暂无数据", Label.CENTER);	//湿度
	private Label direction = new Label("暂无数据", Label.CENTER);	//压强
//	private Label rain = new Label("暂无数据", Label.CENTER);	//雨量
//	private Label win_sp = new Label("暂无数据", Label.CENTER);	//风速
//	private Label win_dir = new Label("暂无数据", Label.CENTER);	//风向
	
	private Choice commChoice = new Choice();	//串口选择（下拉框）
	private Choice bpsChoice = new Choice();	//波特率选择
	
	
	private Button openSerialButton = new Button("打开串口");
	
	//定义输入按钮
	private Button insert=new Button("输入数据");
	private Button send =new Button("Send Data");
	
	Image offScreen = null;	//重画时的画布
	
	//设置window的icon
	Toolkit toolKit = getToolkit();
	Image icon = toolKit.getImage(DataView.class.getResource("computer.png"));

	/**
	 * 类的构造方法
	 * @param client
	 */
	public DataView(Client client) {
		this.client = client;
		commList = SerialTool.findPort();	//程序初始化时就扫描一次有效串口
	}
	
	/**
	 * 主菜单窗口显示；
	 * 添加Label、按钮、下拉条及相关事件监听；
	 */
	public void dataFrame() {
		this.setBounds(client.LOC_X, client.LOC_Y, client.WIDTH, client.HEIGHT);
		this.setTitle("CDIO工程项目");
		this.setIconImage(icon);
		this.setBackground(Color.white);
		this.setLayout(null);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (serialPort != null) {
					//程序退出时关闭串口释放资源
					SerialTool.closePort(serialPort);
				}
				System.exit(0);
			}
			
		});
		
		dis.setBounds(140, 103, 225, 50);
		dis.setBackground(Color.black);
		dis.setFont(font);
		dis.setForeground(Color.white);
		add(dis);
		
		speed.setBounds(520, 103, 225, 50);
		speed.setBackground(Color.black);
		speed.setFont(font);
		speed.setForeground(Color.white);
		add(speed);
		
		direction.setBounds(140, 193, 225, 50);
		direction.setBackground(Color.black);
		direction.setFont(font);
		direction.setForeground(Color.white);
		add(direction);
		
		/*
		rain.setBounds(520, 193, 225, 50);
		rain.setBackground(Color.black);
		rain.setFont(font);
		rain.setForeground(Color.white);
		add(rain);
		
		win_sp.setBounds(140, 283, 225, 50);
		win_sp.setBackground(Color.black);
		win_sp.setFont(font);
		win_sp.setForeground(Color.white);
		add(win_sp);
		
		win_dir.setBounds(520, 283, 225, 50);
		win_dir.setBackground(Color.black);
		win_dir.setFont(font);
		win_dir.setForeground(Color.white);
		add(win_dir);
		*/
		
		//添加串口选择选项
		commChoice.setBounds(160, 397, 200, 200);
		//检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size()<1) {
			JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			for (String s : commList) {
				commChoice.add(s);
			}
		}
		add(commChoice);
		
		//添加波特率选项
		bpsChoice.setBounds(526, 396, 200, 200);
		bpsChoice.add("1200");
		bpsChoice.add("2400");
		bpsChoice.add("4800");
		bpsChoice.add("9600");
		bpsChoice.add("14400");
		bpsChoice.add("19200");
		bpsChoice.add("115200");
		add(bpsChoice);
		
		//添加打开串口按钮
		openSerialButton.setBounds(150, 490, 300, 50);
		openSerialButton.setBackground(Color.lightGray);
		openSerialButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
		openSerialButton.setForeground(Color.darkGray);
		add(openSerialButton);
		//添加输入数据的按钮
		insert.setBounds(520, 193, 200, 50);
		insert.setBackground(Color.green);
		insert.setFont(new Font("微软雅黑", Font.BOLD, 20));
		insert.setForeground(Color.darkGray);
		add(insert);
		
		//添加发送按钮
		send.setBounds(520, 500, 100, 50);
		send.setBackground(Color.green);
		send.setFont(new Font("微软雅黑", Font.BOLD, 20));
		send.setForeground(Color.darkGray);
		add(send);
		
		//添加打开串口按钮的事件监听
		openSerialButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				//获取串口名称
				String commName = commChoice.getSelectedItem();			
				//获取波特率
				String bpsStr = bpsChoice.getSelectedItem();
				
				//检查串口名称是否获取正确
				if (commName == null || commName.equals("")) {
					JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);			
				}
				else {
					//检查波特率是否获取正确
					if (bpsStr == null || bpsStr.equals("")) {
						JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						//串口名、波特率均获取正确时
						int bps = Integer.parseInt(bpsStr);
						try {
							
							//获取指定端口名及波特率的串口对象
							serialPort = SerialTool.openPort(commName, bps);
							//在该串口对象上添加监听器
							SerialTool.addListener(serialPort, new SerialListener());
							//监听成功进行提示
							JOptionPane.showMessageDialog(null, "监听成功，稍后将显示监测数据！", "提示", JOptionPane.INFORMATION_MESSAGE);
							
						} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
							//发生错误时使用一个Dialog提示具体的错误信息
							JOptionPane.showMessageDialog(null, e1, "错误", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				
			}
		});
		//添加输入数据的监听
		insert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String temp=JOptionPane.showInputDialog(null,"请输入角位移","15.1");
				distance=Double.parseDouble(temp);
				dis.setText(""+distance+"rad");
				 temp=JOptionPane.showInputDialog(null,"请输入速度","6.28");
				v=Double.parseDouble(temp);
				speed.setText(""+v+"rad/s");
				temp=JOptionPane.showInputDialog(null,"请输入方向");
				dir=Integer.parseInt(temp);
				if(dir>0)
				{
					direction.setText("正向");
					dir=1;
				}
				else
					direction.setText("反向");
				
				
				
			}
		});
		
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//输入到对话框
				System.out.println(distance);
				System.out.println(v);
				String st1=Double.toString(distance);
				String st2=Double.toString(v);
				if(distance<10.0)
					st1=new String("0"+distance);
				if(st1.length()==4)
					st1=new String(st1+"0");
				if(v<10.0)
					st2=new String("0"+v);
				if(st2.length()==4)
					st2=new String(st2+"0");
				try {
					SerialTool.sendToPort(serialPort, new String("*"+st1+dir+st2).getBytes()) ;
					System.out.println(new String("*"+st1+dir+st2).getBytes().length);
					System.out.println("发送完成");
					JOptionPane.showMessageDialog(null,"发送完成");
				} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}});
		
		
		this.setResizable(false);
		
		new Thread(new RepaintThread()).start();	//启动重画线程
		
	}
	
	/**
	 * 画出主界面组件元素
	 */
	public void paint(Graphics g) {
		Color c = g.getColor();
		
		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 位移： ", 45, 130);

		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 速度： ", 425, 130);
		
		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 方向： ", 45, 220);
/*		
		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 雨量： ", 425, 220);
		
		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 风速： ", 45, 310);
		
		g.setColor(Color.black);
		g.setFont(new Font("微软雅黑", Font.BOLD, 25));
		g.drawString(" 风向： ", 425, 310);
		*/
		g.setColor(Color.gray);
		g.setFont(new Font("微软雅黑", Font.BOLD, 20));
		g.drawString(" 串口选择： ", 45, 410);
		
		g.setColor(Color.gray);
		g.setFont(new Font("微软雅黑", Font.BOLD, 20));
		g.drawString(" 波特率： ", 425, 410);
		
		
	}
	
	/**
	 * 双缓冲方式重画界面各元素组件
	 */
	public void update(Graphics g) {
		if (offScreen == null)	offScreen = this.createImage(Client.WIDTH, Client.HEIGHT);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, Client.WIDTH, Client.HEIGHT);	//重画背景画布
		this.paint(gOffScreen);	//重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null);	//将新画好的画布“贴”在原画布上
	}
	
	/*
	 * 重画线程（每隔30毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while(true) {
				//调用重画方法
				repaint();
				
				
				
				//扫描可用串口
				commList = SerialTool.findPort();
				if (commList != null && commList.size()>0) {
					
					//添加新扫描到的可用串口
					for (String s : commList) {
						
						//该串口名是否已存在，初始默认为不存在（在commList里存在但在commChoice里不存在，则新添加）
						boolean commExist = false;	
						
						for (int i=0; i<commChoice.getItemCount(); i++) {
							if (s.equals(commChoice.getItem(i))) {
								//当前扫描到的串口名已经在初始扫描时存在
								commExist = true;
								break;
							}					
						}
						
						if (commExist) {
							//当前扫描到的串口名已经在初始扫描时存在，直接进入下一次循环
							continue;
						}
						else {
							//若不存在则添加新串口名至可用串口下拉列表
							commChoice.add(s);
						}
					}
					
					//移除已经不可用的串口
					for (int i=0; i<commChoice.getItemCount(); i++) {
						
						//该串口是否已失效，初始默认为已经失效（在commChoice里存在但在commList里不存在，则已经失效）
						boolean commNotExist = true;	
						
						for (String s : commList) {
							if (s.equals(commChoice.getItem(i))) {
								commNotExist = false;	
								break;
							}
						}
						
						if (commNotExist) {
							//System.out.println("remove" + commChoice.getItem(i));
							commChoice.remove(i);
						}
						else {
							continue;
						}
					}
					
				}
				else {
					//如果扫描到的commList为空，则移除所有已有串口
					commChoice.removeAll();
				}

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
		
	}
	
	/**
	 * 以内部类形式创建一个串口监听类
	 * @author zhong
	 *
	 */
	
	private class SerialListener implements SerialPortEventListener {
		
		///**
		// * 处理监控到的串口事件
		// */
	    public void serialEvent(SerialPortEvent serialPortEvent) {
	    	
	        switch (serialPortEvent.getEventType()) {

	            case SerialPortEvent.BI: // 10 通讯中断
	            	JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
	            	break;

	            case SerialPortEvent.OE: // 7 溢位（溢出）错误

	            case SerialPortEvent.FE: // 9 帧错误

	            case SerialPortEvent.PE: // 8 奇偶校验错误

	            case SerialPortEvent.CD: // 6 载波检测

	            case SerialPortEvent.CTS: // 3 清除待发送数据

	            case SerialPortEvent.DSR: // 4 待发送数据准备好了

	            case SerialPortEvent.RI: // 5 振铃指示

	            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
	            	break;
	            
	            case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
	            	
	            	System.out.println("found data");
					byte[] data = null;
					
					try {
						if (serialPort == null) {
							JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							data = SerialTool.readFromPort(serialPort);	//读取数据，存入字节数组
							//System.out.println(new String(data));
							
							//自定义解析过程
							if (data == null || data.length < 1) {	//检查数据是否读取正确	
								JOptionPane.showMessageDialog(null, "读取数据过程中未获取到有效数据！请检查设备或程序！", "错误", JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
							else {
								String dataOriginal = new String(data);	//将字节数组数据转换位为保存了原始数据的字符串
								String dataValid = "";	//有效数据（用来保存原始数据字符串去除最开头*号以后的字符串）
								String[] elements = null;	//用来保存按空格拆分原始字符串后得到的字符串数组	
								//解析数据
								if (dataOriginal.charAt(0) == '*') {	//当数据的第一个字符是*号时表示数据接收完成，开始解析							
									dataValid = dataOriginal.substring(1);
									elements = dataValid.split(" ");
									if (elements == null || elements.length < 1) {	//检查数据是否解析正确
										JOptionPane.showMessageDialog(null, "数据解析过程出错，请检查设备或程序！", "错误", JOptionPane.INFORMATION_MESSAGE);
										System.exit(0);
									}
									else {
										try {
											
											
											
											for (int i=0; i<elements.length; i++) {
												System.out.println(elements[i]);
											}
											//System.out.println("win_dir: " + elements[5]);
											//tem.setText(elements[0] + " ℃");
											//hum.setText(elements[1] + " %");
											//pa.setText(elements[2] + " hPa");
											//rain.setText(elements[3] + " mm");
											//win_sp.setText(elements[4] + " m/s");
											//win_dir.setText(elements[5] + " °");
										} catch (ArrayIndexOutOfBoundsException e) {
											JOptionPane.showMessageDialog(null, "数据解析过程出错，更新界面数据失败！请检查设备或程序！", "错误", JOptionPane.INFORMATION_MESSAGE);
											System.exit(0);
										}
									}	
								}
							}
							
						}						
						
					} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
						JOptionPane.showMessageDialog(null, e, "错误", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);	//发生读取错误时显示错误信息后退出系统
					}	
		            
					break;
	
	        }

	    }

	}
	
	
}


