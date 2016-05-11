package cn.sccl.workflow.service;

import net.sf.json.JSONArray;


public interface TaskLocalManager 
{
	
	abstract public JSONArray queryTodoTasks(String loginName, int pageNo, int pageSize) throws Exception;

	abstract public JSONArray queryDoneTasks(String loginName, int pageNo, int pageSize) throws Exception;
}

