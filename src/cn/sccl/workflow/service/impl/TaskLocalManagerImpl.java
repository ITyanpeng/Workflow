package cn.sccl.workflow.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import cn.sccl.common.util.ConfigPropertiesUtil;
import cn.sccl.common.util.DatabaseUtil;
import cn.sccl.workflow.service.TaskLocalManager;

/**
 * 自定义从数据库获取待办任务
 * @author Gun
 *
 */
public class TaskLocalManagerImpl implements TaskLocalManager
{

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public JSONArray queryTodoTasks(String loginName, int pageNo, int pageSize) throws Exception {
		// TODO Auto-generated method stub
		String sql=ConfigPropertiesUtil.getString("workflow.task.todo.sql");
//		System.out.println("SQL to execute : "+sql);

		int paramsCount=sql.split("\\?").length - 1;
		String[] ps=new String[paramsCount];
		int i=0;
		for(String p:ps)
		{
			ps[i++]=loginName;
		}
		Connection conn=null;
		try {
			conn=dataSource.getConnection();
			return DatabaseUtil.queryOfPagingWithParams(sql, pageNo, pageSize, conn, ps);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public JSONArray queryDoneTasks(String loginName, int pageNo, int pageSize)
			throws Exception {
		// TODO Auto-generated method stub
		String sql=ConfigPropertiesUtil.getString("workflow.task.done.sql");
//		System.out.println("SQL to execute : "+sql);

		int paramsCount=sql.split("\\?").length - 1;
		String[] ps=new String[paramsCount];
		int i=0;
		for(String p:ps)
		{
			ps[i++]=loginName;
		}
		Connection conn=null;
		try {
			conn=dataSource.getConnection();
			return DatabaseUtil.queryOfPagingWithParams(sql, pageNo, pageSize, conn, ps);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
}

