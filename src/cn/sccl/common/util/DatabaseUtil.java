package cn.sccl.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DatabaseUtil {
	
	public static JSONArray query(String sql, Connection conn) throws Exception
	{
//		DataSource ds=(DataSource) WebContextUtil.getBean("dataSource");
//		Connection conn=ds.getConnection();
		Statement st=null;
		try {
			System.out.println("SQL to execute : "+sql);
			st = conn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			
			return resultToJson(rs);
		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception(e.getMessage());
		}
		finally
		{
			if ( st != null)
				st.close();
//			conn.close();
		}
	}
	
	public static int getCount(String sql, Connection conn) throws Exception
	{
		String strSQLPage = String.format("SELECT COUNT(1) FROM (%1$s)", sql);
//		Connection conn=ds.getConnection();
		Statement st=null;
		try {
			System.out.println("SQL to execute : "+strSQLPage);
			st = conn.createStatement();
			ResultSet rs=st.executeQuery(strSQLPage);
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception(e.getMessage());
		}
		finally
		{
			if ( st != null)
				st.close();
//			conn.close();
		}	
		
	}
	
	/**
	 * 分页查询
	 * @param sql
	 * @param pageNo 页号从1开始
	 * @param pageSize
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static JSONArray queryOfPaging(String sql, int pageNo, int pageSize, Connection conn) throws Exception
	{
//		DataSource ds=(DataSource) WebContextUtil.getBean("dataSource");
		String strSQLPage = String.format("SELECT CCDD.* FROM ("
				 +
				 "SELECT AAABB.*, ROWNUM ROWNONUM FROM (%1$s) AAABB WHERE ROWNUM < ?"
				 + ")CCDD WHERE CCDD.ROWNONUM >= ?", sql);		
		int start=(pageNo-1)*pageSize+1;
		int end=pageNo*pageSize;

//		Connection conn=ds.getConnection();
		PreparedStatement stat = null;
		try {
			System.out.println("SQL to execute : "+strSQLPage);
			stat = conn.prepareStatement(strSQLPage);
			stat.setObject(1, end);
			stat.setObject(2, start);

			ResultSet rs=stat.executeQuery();
			
			return resultToJson(rs);
		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception(e.getMessage());
		}
		finally
		{
			if ( stat != null)
				stat.close();
//			conn.close();
		}
	}

	
	/**
	 * 分页查询
	 * @param sql
	 * @param pageNo 页号从1开始
	 * @param pageSize
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static JSONArray queryOfPagingWithParams(String sql, int pageNo, int pageSize, Connection conn, String[] params) throws Exception
	{
//		DataSource ds=(DataSource) WebContextUtil.getBean("dataSource");
		String strSQLPage = String.format("SELECT CCDD.* FROM ("
				 +
				 "SELECT AAABB.*, ROWNUM ROWNONUM FROM (%1$s) AAABB WHERE ROWNUM < ?"
				 + ")CCDD WHERE CCDD.ROWNONUM >= ?", sql);		
		int start=(pageNo-1)*pageSize+1;
		int end=pageNo*pageSize;
		
//		Connection conn=ds.getConnection();
		PreparedStatement stat = null;
		try {
			System.out.println("SQL to execute : "+strSQLPage);
			stat = conn.prepareStatement(strSQLPage);
			int i=1;
			for(String p:params)
			{
				stat.setObject(i++, p);
			}
			stat.setObject(i++, end);
			stat.setObject(i++, start);

			ResultSet rs=stat.executeQuery();
			
			return resultToJson(rs);
		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception(e.getMessage());
		}
		finally
		{
			if ( stat != null)
				stat.close();
//			conn.close();
		}
	}

	
	private static JSONArray resultToJson(ResultSet rs) throws SQLException  
	{
		ResultSetMetaData rsmd = null;
		JSONArray ja=new JSONArray();;
			// 初始化查询结果中的列名
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			
			String[] colsNames = new String[iColumnCount];
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				colsNames[iIdx] = rsmd.getColumnLabel(iIdx + 1);
			}

			// 初始化每行
			long lRowCount = 0;
			while (rs.next())
			{
				JSONObject jo=new JSONObject();
				for (int iColIdx = 1; iColIdx <= iColumnCount; iColIdx++)
				{
					Object o=rs.getObject(iColIdx);
					String value=(o==null)?"":o.toString();
					jo.put(colsNames[iColIdx-1], value);
				}
				ja.add(jo);
			}

			return ja;
		}
	
}
