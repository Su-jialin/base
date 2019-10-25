package bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		new Client().start();
	}
	
	private Scanner sc=new Scanner(System.in);
	DataInputStream dis;
	DataOutputStream dos;
	public void start() throws UnknownHostException, IOException{
		
		
		Socket server=new Socket("172.18.50.68",8888);
		System.out.println("成功连接服务器！");
		//获取网络地址对象
		InetAddress addr=server.getInetAddress();
		System.out.println("服务器的主机地址："+addr.getHostAddress());
		System.out.println("服务器的IP地址："+Arrays.toString(addr.getAddress()));
		
		InputStream in=server.getInputStream();
		OutputStream out=server.getOutputStream();
		
		dis=new DataInputStream(in);
		dos=new DataOutputStream(out);
		
		boolean running =true;
		while(running){
			System.out.println("*************");
			System.out.println("*   1.开户            *");
			System.out.println("*   2.存款            *");
			System.out.println("*   3.取款           *");
			System.out.println("*   4.转账            *");
			System.out.println("*   0.退出            *");
			System.out.println("*************");
			System.out.println("请输入:");
			String command=sc.nextLine();
			switch (command) {
			case "1":
				register();
				break;
			case "2":
				diposit();
				break;
			case "3":
				withdraw();
				break;
			case "4":
				transfer();
				break;
			case "0":
				System.out.println("bye");
				running=false;
			}
		}
		server.close();
		sc.close();
	}
	
	//开户
	public void register() throws IOException{
		System.out.println("请输入账号：");
		String cardno=sc.nextLine();
		dos.writeUTF("register");
		dos.writeUTF(cardno);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	
	//存款
	public void diposit() throws IOException {
		System.out.println("请输入用户的账号：");
		String cardno=sc.nextLine();
		System.out.println("请输入存款金额：");
		float money=sc.nextFloat();
		dos.writeUTF("diposit");
		dos.writeUTF(cardno);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	
	//取款
	public void withdraw() throws IOException{
		System.out.println("请输入账号:");
		String cardno=sc.nextLine();
		System.out.println("请输入金额：");
		float money=sc.nextFloat();
		dos.writeUTF("withdraw");
		dos.writeUTF(cardno);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	
	//转账
	public void transfer() throws IOException{
		System.out.println("请输入账号:");
		String cardno=sc.nextLine();
		System.out.println("请输入转账目标账号");
		String tCardno=sc.nextLine();
		System.out.println("请输入金额：");
		float money=sc.nextFloat();
		dos.writeUTF("transfer");
		dos.writeUTF(cardno);
		dos.writeUTF(tCardno);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
}
