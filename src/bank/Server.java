package bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

/**
 * CS架构的程序
 *
 */
public class Server {
	
	public static void main(String[] args) throws IOException {
		new Server().start();
	}
	private BankDAO dao=new BankDAO();
	DataInputStream dis;
	DataOutputStream dos;
	public void start() throws IOException{
		//创建套接字服务器
		ServerSocket server=new ServerSocket(8888);
		//启动服务器
		System.out.println("服务器启动完成，监听端口：8888");
		
		boolean running =true;
		while(running){
			//当前线程进入阻塞状态
			Socket client=server.accept();
			//创建线程来处理业务
			new Thread(){
				public void run(){
					//获取网络地址对象
					InetAddress addr=client.getInetAddress();
					System.out.println("客户端的主机地址："+addr.getHostAddress());
					System.out.println("客户端的IP地址："+Arrays.toString(addr.getAddress()));
					
					try {
						InputStream in=client.getInputStream();
						OutputStream out=client.getOutputStream();
						dos=new DataOutputStream(out);
						boolean running =true;
						while(running){
							/**
							 * 业务的约定--->协议
							 * 如果客户端发送一个命令：diposit,接受该命令所需要的参数
							 */
							DataInputStream dis=new DataInputStream(in);
							try{
								String command=dis.readUTF();
								switch (command) {
								case "register":
									register(dis.readUTF());
									break;
								case "diposit":
									diposit(dis.readUTF(),dis.readFloat());
									break;
								case "withdraw":
									withdraw(dis.readUTF(), dis.readFloat());
									break;
								case "transfer":
									transfer(dis.readUTF(), dis.readUTF(), dis.readFloat());
									break;
								}
							}catch(EOFException e){
								break;
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
		
	}
	//开户
	public void register(String cardno) throws SQLException, IOException{
		dao.add(cardno);
		dos.writeUTF("开户成功！！！");
		dos.flush();
	}
	
	//存款
	public void diposit(String cardno,float money) throws IOException, SQLException {
		dao.update(cardno, money);
		dos.writeUTF("存款成功！！！");
		dos.flush();
	}
	
	//取款
	public void withdraw(String cardno,float money) throws SQLException, IOException{
		Map<String, Object> map=dao.find(cardno);
		//System.out.println("++++"+map.get("balance"));
		float temp;
		if(( map.get("balance"))!=null){
			temp=(float) map.get("balance");
		}else{
			temp=0;
		}
		System.out.println(temp);
		if(temp>=money){
			dao.withDraw(cardno, money);
			dos.writeUTF("取款成功！！！");
			dos.flush();
		}else{
			dos.writeUTF("账户余额不足！！！");
			dos.flush();
		}
		
	}
	
	//转账
	public void transfer(String card,String tCard,float money) throws SQLException, IOException{
		dao.transfer(card, tCard, money);
		dos.writeUTF("转账成功！！！");
		dos.flush();
	}
}
