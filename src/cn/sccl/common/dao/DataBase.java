package cn.sccl.common.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.sccl.common.util.ContextUtil;
import cn.sccl.common.util.DevLog;
import cn.sccl.common.util.JsonUtil;

public class DataBase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7407243572217122599L;

	
	public static <T> List<T> Query4Model(String strSQL, Class<T> cls, Object...args)
	{
		//判断sql中是否含有*，如果是，不必严格匹配，否则model和查询数据集严格匹配
		Boolean bStrictMaching = SqlNotContainsAsteriskChar(strSQL);		
		return Query4Model(strSQL, cls, bStrictMaching, args);
	}
	
	
	
	private static <T> List<T> Query4Model(String strSQL, Class<T> cls, Boolean bStrictMaching, Object...args)
	{
		
		org.apache.commons.dbcp.BasicDataSource ds=(org.apache.commons.dbcp.BasicDataSource) ContextUtil.getBean("dataSource");
		PreparedStatement stat = null;
		ResultSet rs = null;
		Connection conn=null;
		List<T> list = null;
		try
		{	
			conn = ds.getConnection();
			//初始化PrepareStatement参数
			stat = initPrepareStatement(conn, strSQL, args);
			rs = stat.executeQuery();
			
			// 填充Dataset对象
			list = DataSet.FillModel(cls, rs, bStrictMaching);
			
			// 输出分类SQL日志文件
			// LogSQL(controller.getClass().getName(), strSQL) ;
		}
		catch (Exception ex)
		{
//			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		finally
		{
			closeAllOpenObject(rs, stat, conn);
		}
		if(list==null){
			list=new ArrayList<T>();
		}
		return list;
	}
	
	
	public static PreparedStatement initPrepareStatement(Connection conn,  String strSQL, Object...args) throws SQLException{
		PreparedStatement stat = null;
		// 打印查询语句
		DevLog.debug(strSQL+"," + JsonUtil.toJSONString(args));

		//针对模糊查询，预先格式化
		strSQL = strSQL.replaceAll("'%\\?%'", "'%'||?||'%'");
		
		// 查询
//		stat = conn.prepareStatement(strSQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		stat = conn.prepareStatement(strSQL);
		if(args != null  ){
			for(int index=0; index < args.length; index++){
				stat.setObject(index+1, args[index]);
			}
		}
		return stat;
	}
	
	
	private static void closeAllOpenObject(java.sql.ResultSet rs, Statement stat, Connection conn)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
				rs = null;
			}
			catch (SQLException e)
			{
			}
		}
		if (stat != null)
		{
			try
			{
				stat.close();
				stat = null;
			}
			catch (SQLException e)
			{
			}
		}
		if (conn != null)
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch (SQLException e)
			{
			}
		}
	}
	
	public static boolean SqlNotContainsAsteriskChar(String strSQL)
	{
		int iFromPos = -1, iFindAsteriskPos = -1, iFromCurLen = 0;
		char cCurChar = ' ';
		for (int i = 0; i < strSQL.length(); i ++)
		{
			cCurChar  = strSQL.charAt(i);
			
			if (cCurChar == ' ')
			{
				iFromPos = i;
				iFromCurLen = 1;
			}
			
			if (cCurChar == 'f' || cCurChar == 'F')
			{
				if (i == iFromPos +1) {
					iFromCurLen = 2;
				}
				else 
				{
					iFromCurLen = 0;							
				}
			}
			if ((cCurChar == 'r' || cCurChar == 'R') )
			{
				if (i == iFromPos +2) {
					iFromCurLen = 3;
				}
				else 
				{
					iFromCurLen = 0;							
				}
			}
			if ((cCurChar == 'o' || cCurChar == 'O') )
			{
				if (i == iFromPos +3) {
					iFromCurLen = 4;
				}
				else 
				{
					iFromCurLen = 0;										
				}
			}
			if ((cCurChar == 'm' || cCurChar == 'M') )
			{
				if (i == iFromPos +4) {
					iFromCurLen = 5;
					break ;
				}
				else 
				{
					iFromCurLen = 0;										
				}
			}
			
			
			if (strSQL.charAt(i) == '*') 
			{
				iFindAsteriskPos = i;
			}
			
		}
		//判断*的位置是否小于from的位置，包含*
		if (iFindAsteriskPos != -1 && iFromCurLen == 5 && iFindAsteriskPos < iFromPos)
			return false;
		else return true;
		
	}
}
