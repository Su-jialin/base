package bank;

import java.io.IOException;
import java.util.Properties;

/**
 * 单例模式
 * 
 * @author 第一组
 *
 */
public class MyPropeties extends Properties {

	private static MyPropeties propeties;

	private MyPropeties() {
		try {
			// 读取配置文件中数据，加载到当前MyPropeties
			this.load(MyPropeties.class.getClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MyPropeties getInstance() {
		if (null == propeties) {
			propeties = new MyPropeties();
		}
		return propeties;
	}

}