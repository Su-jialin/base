package bank;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	// 加载驱动
	static {
		try {
			Class.forName(MyPropeties.getInstance().getProperty("driverName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 获取连接对象
	public Connection getConn() throws SQLException {
		conn = DriverManager.getConnection(MyPropeties.getInstance().getProperty("url"), MyPropeties.getInstance());
		return conn;
	}

	/**
	 * 查看多条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findMutil(String sql, Object... params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			rs = pstmt.executeQuery();
			// 获取所有的列名
			List<String> columnNames = this.getAllColumnNames(rs);
			while (rs.next()) {
				map = new HashMap<String, Object>();
				// 循环所有的列
				for (String columnName : columnNames) { // 封装到map中的键都是大写字母
														// oralce数据库默认都是大写
					if (null != rs.getObject(columnName)) {
						// 获取值的类型
						String typeName = rs.getObject(columnName).getClass().getName();
						if ("oracle.sql.BLOB".equals(typeName)) {
							// 对其进行额外的处理
							// 获取值
							Blob blob = (Blob) rs.getObject(columnName);
							// 获取二进制流
							InputStream in = blob.getBinaryStream();
							byte[] bt = new byte[(int) blob.length()];
							try {
								in.read(bt);
								map.put(columnName, bt); // map中存储的字节数组
							} catch (IOException e) {
								e.printStackTrace();
							}

						} else {
							map.put(columnName, rs.getObject(columnName));
						}

					} else {
						map.put(columnName, rs.getObject(columnName));// map.put("empno",rs.getObject("empno"));
					}
				}
				list.add(map);// 添加到List集合中
			}
		} finally {
			closeAll(rs, pstmt, conn);
		}

		return list;
	}

	/**
	 * 查询单条记录 select * from tableName where id = ?
	 * 
	 * @param sql
	 *            查询的sql语句
	 * @param params
	 *            传入的参数
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> findSingle(String sql, Object... params) throws SQLException {
		Map<String, Object> map = null;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			rs = pstmt.executeQuery();
			// 获取所有的列名
			List<String> columnNames = this.getAllColumnNames(rs);
			System.out.println(columnNames);
			// 取值
			if (rs.next()) {
				map = new HashMap<String, Object>();
				// 循环所有的列
				for (String columnName : columnNames) {
					if (null != rs.getObject(columnName)) {
						// 获取值的类型
						String typeName = rs.getObject(columnName).getClass().getName();
						if ("oracle.sql.BLOB".equals(typeName)) {
							// 对其进行额外的处理
							// 获取值
							Blob blob = (Blob) rs.getObject(columnName);
							// 获取二进制流
							InputStream in = blob.getBinaryStream();
							byte[] bt = new byte[(int) blob.length()];
							try {
								in.read(bt);
								map.put(columnName, bt); // map中存储的字节数组
							} catch (IOException e) {
								e.printStackTrace();
							}

						} else {
							map.put(columnName, rs.getObject(columnName));
						}

					} else {
						map.put(columnName, rs.getObject(columnName));// map.put("empno",rs.getObject("empno"));
					}
				}
			}
		} finally {
			closeAll(rs, pstmt, conn);
		}
		return map;
	}

	/**
	 * 获取所有的列名
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<String> getAllColumnNames(ResultSet rs) throws SQLException {
		List<String> list = new ArrayList<String>();
		ResultSetMetaData data = rs.getMetaData();
		// 获取列的个数
		int count = data.getColumnCount();
		for (int i = 1; i <= count; i++) {
			list.add(data.getColumnName(i));
		}
		return list;
	}

	/**
	 * 聚合函数的查询 select count(*) from emp
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public double getPolymer(String sql, Object... params) throws SQLException {
		double result = 0;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);// 获取第一列的值
			}
		} finally {

		}
		return result;
	}

	/**
	 * 更新sql语句 delete update insert 单条sql语句
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int update(String sql, Object... params) throws SQLException {
		int result = 0;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			result = pstmt.executeUpdate();
		} finally {
			closeAll(null, pstmt, conn);
		}
		return result;
	}

	/**
	 * 批量处理 insert update delete
	 * 
	 * @param sqls
	 *            多条sql语句 sql-->param参数的list集合是一一对应
	 * @param params
	 *            sql语句对应的参数
	 * @return
	 * @throws SQLException
	 */
	public int updateInfo(List<String> sqls, List<List<Object>> params) throws SQLException {
		int result = 0;
		try {
			conn = getConn();
			// 设置手动提交 事务
			conn.setAutoCommit(false);
			if (null != sqls && sqls.size() > 0) { // 不能为空
				// 循环每条sql语句
				for (int i = 0; i < sqls.size(); i++) {
					pstmt = conn.prepareStatement(sqls.get(i));
					// 设置参数
					this.setParams(pstmt, params.get(i));
					result = pstmt.executeUpdate();
				}
			}
			// 更新操作全部完成 事务提交
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			// 如果发生异常，事务必须回滚
			conn.rollback();
		} finally {
			// 还原事务的状态
			conn.setAutoCommit(true);
			closeAll(null, pstmt, conn);
		}

		return result;
	}

	/**
	 * 更新单条sql语句
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int updateInfo(String sql, List<Object> params) throws SQLException {
		int result = 0;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			result = pstmt.executeUpdate();
		} finally {
			closeAll(null, pstmt, conn);
		}
		return result;
	}

	// 设置参数
	public void setParams(PreparedStatement pstmt, Object... params) throws SQLException {
		if (null != params && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]); // ? 从1开始
			}
		}
	}

	// 关闭资源
	public void closeAll(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (null != pstmt) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查看多条记录 重载时，假设传入null 在编译时无法确定调用findMutil哪个查询方法
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findMutilList(String sql, List<Object> params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			this.setParams(pstmt, params);
			// 执行
			rs = pstmt.executeQuery();
			// 获取所有的列名
			List<String> columnNames = this.getAllColumnNames(rs);
			while (rs.next()) {
				map = new HashMap<String, Object>();
				// 循环所有的列
				for (String columnName : columnNames) { // 封装到map中的键都是大写字母
														// oralce数据库默认都是大写
					if (null != rs.getObject(columnName)) {
						// 获取值的类型
						String typeName = rs.getObject(columnName).getClass().getName();
						if ("oracle.sql.BLOB".equals(typeName)) {
							// 对其进行额外的处理
							// 获取值
							Blob blob = (Blob) rs.getObject(columnName);
							// 获取二进制流
							InputStream in = blob.getBinaryStream();
							byte[] bt = new byte[(int) blob.length()];
							try {
								in.read(bt);
								map.put(columnName, bt); // map中存储的字节数组
							} catch (IOException e) {
								e.printStackTrace();
							}

						} else {
							map.put(columnName, rs.getObject(columnName));
						}

					} else {
						map.put(columnName, rs.getObject(columnName));// map.put("empno",rs.getObject("empno"));
					}
				}

				list.add(map);// 添加到List集合中
			}
		} finally {
			closeAll(rs, pstmt, conn);
		}

		return list;
	}

	// 设置参数
	public void setParams(PreparedStatement pstmt, List<Object> params) throws SQLException {
		if (null != params && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i)); // ? 从1开始
			}
		}
	}

	/**
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            传入参数
	 * @param c
	 *            封装到那个对象的class实例
	 * @return
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public <T> List<T> select(String sql, List<Object> params, Class<T> c) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, SQLException, InstantiationException {
		List<T> list = new ArrayList<T>();
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			setParams(pstmt, params);
			// 执行
			rs = pstmt.executeQuery();
			// 获取结果集所有的列名
			List<String> columnNames = getAllColumnNames(rs);
			// 获取传入的Class实例的所有的属性和方法
			Field[] fields = c.getDeclaredFields();
			// 获取所有的方法
			Method[] methods = c.getDeclaredMethods();
			// 调用setXXX 循环方法和属性，将setXXX 方法存到List集合
			List<Method> setters = new ArrayList<Method>();
			for (Method m : methods) {
				if (m.getName().startsWith("set")) {
					setters.add(m);
				}
			}
			Class<?>[] types;
			T t = null;
			String mname = null;
			String typeName = null;
			// Class实例对象的类中的所有属性名必须和rs中的列名相同1，属性名 必须和表的字段名相同
			while (rs.next()) {// 每循环一次就是一行记录，需要创建对象
				t = c.newInstance();// 调用对象默认的构造方法创建对象 new UserPO();
				// 循环结果集的列名
				for (String colmName : columnNames) {
					Object obj = rs.getObject(colmName);
					if (null == obj) {
						continue;
					}
					// 循环setters方法
					for (Method m : setters) {
						// 获取m方法的参数类型
						types = m.getParameterTypes();
						// setters方法是只有一个参数，获取数组的第一个元素
						if (null != types && types.length > 0) {
							typeName = types[0].getSimpleName();// 类型名称
							System.out.println(typeName);
						}
						// 判断该方法名称与set+列名
						if (m.getName().equalsIgnoreCase("set" + colmName)) {
							if ("int".equals(typeName) || "Integer".equals(typeName)) {
								int i = Integer.parseInt(obj.toString());
								System.out.println(i);
								m.invoke(t, i);// t.setU_id;
							} else if ("float".equals(typeName) || "Float".equals(typeName)) {
								m.invoke(t, rs.getFloat(colmName));
							} else if ("double".equals(typeName) || "Double".equals(typeName)) {
								m.invoke(t, rs.getDouble(colmName));
							} else {
								m.invoke(t, rs.getString(colmName));
							}
						}
					}
				}
				list.add(t);// 将对象添加到集合
			}
		} finally {
			closeAll(rs, pstmt, conn);
		}
		return list;
	}

}
