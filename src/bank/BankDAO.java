package bank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankDAO {
	DbHelper db=new DbHelper();
	public Map<String, Object> find(String cardno) throws SQLException{
		String sql="select balance from account where accountid=? ";
		return db.findSingle(sql, cardno);
	}
	
	public void update(String cardno,float money) throws SQLException{
		String sql="update account set balance=balance+? where accountid=? ";
		db.update(sql,money,cardno);
	}
	public void add(String cardno) throws SQLException{
		String sql="insert into account values(?,0)";
		db.update(sql, cardno);
	}
	public void withDraw(String cardno,float money) throws SQLException{
		String sql="update account set balance=balance-? where accountid=? ";
		db.update(sql, money,cardno);
	}
	public void transfer(String card,String tCard,float money) throws SQLException{
		List<String> sqls=new ArrayList<String>();
		String sql1="update account set balance=balance-? where accountid=?";
		String sql2="update account set balance=balance+? where accountid=?";
		sqls.add(sql1);
		List<List<Object>> params=new ArrayList<List<Object>>();
		List<Object> param01=new ArrayList<Object>();
		param01.add(money);
		param01.add(card);
		params.add(param01);
		sqls.add(sql2);
		List<Object> param02=new ArrayList<Object>();
		param02.add(money);
		param02.add(tCard);
		params.add(param02);
		db.updateInfo(sqls, params);
	}
}
