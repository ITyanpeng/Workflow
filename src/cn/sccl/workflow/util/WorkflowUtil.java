package cn.sccl.workflow.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventImpl;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sccl.common.util.ConfigPropertiesUtil;
import cn.sccl.common.util.ContextUtil;
import cn.sccl.common.util.WebUtil;
import cn.sccl.workflow.ext.RejectTaskCmd;
import cn.sccl.workflow.service.TaskLocalManager;

public class WorkflowUtil {

	private static Logger	log	= LoggerFactory.getLogger(WorkflowUtil.class);
	private static RepositoryService repositoryService=null;
	private static RuntimeService runtimeService=null;
	private static TaskService taskService=null;
	private static HistoryService historyService=null;
	private static IdentityService identityService=null;
	private static TaskLocalManager taskLocalManager=null;

	/**
	 * 获取流程服务
	 * @return RuntimeService
	 */
	private static RuntimeService getRuntimeService()
	{
		if (runtimeService != null ) return runtimeService;
		runtimeService = (RuntimeService) ContextUtil.getBean("runtimeService");
		return runtimeService;
	}
	
	/**
	 * 获取流程服务
	 * @return RuntimeService
	 */
	private static IdentityService getIdentityService()
	{
		if (identityService != null ) return identityService;
		identityService = (IdentityService) ContextUtil.getBean("identityService");
		return identityService;
	}
	
	/**
	 * 获取流程历史服务
	 * @return HistoryService
	 */
	private static HistoryService getHistoryService()
	{
		if (historyService != null ) return historyService;
		historyService = (HistoryService) ContextUtil.getBean("historyService");
		return historyService;
	}
	/**
	 * 获取任务服务
	 * @return TaskService
	 */
	private static TaskService getTaskService()
	{
		if (taskService != null ) return taskService;
		taskService = (TaskService) ContextUtil.getBean("taskService");
		return taskService;
	}
	
	/**
	 * 获取流程定义服务
	 * @return RepositoryService
	 */
	private static RepositoryService getRepositoryService()
	{
		if (repositoryService != null ) return repositoryService;
		repositoryService = (RepositoryService) ContextUtil.getBean("repositoryService");
		return repositoryService;
	}
	
	
	/**
	 * 获取流程服务
	 * @return RuntimeService
	 */
	private static TaskLocalManager getTaskLocalManager()
	{
		if (taskLocalManager != null ) return taskLocalManager;
		taskLocalManager = (TaskLocalManager) ContextUtil.getBean("taskLocalManager");
		return taskLocalManager;
	}
	
	
	/**
	 * 流程实例相关
	 * @author Gun
	 *
	 */
	public static class P {
	/**
	 * 发起一个流程
	 * 以流程定义的最新版本发起一个流程。
	 * @param processDefinitionKey 流程定义名
	 * @param businessKey 业务单号
	 * @param variables 用于流程流转的变量，比如金额大小涉及到审批级别的不同。
	 * @return ProcessInstance
	 */
	public static ProcessInstance startProcess(String processDefinitionKey, String businessKey, String businessName,  String loginName, Map<String, Object> variables)
	{
		RuntimeServiceImpl rsi=(RuntimeServiceImpl)getRuntimeService();
		ProcessInstanceBuilderImpl pib = new ProcessInstanceBuilderImpl(rsi);
		//写入业务单名称
		pib.processInstanceName(businessName);
		pib.processDefinitionKey(processDefinitionKey);
		pib.businessKey(businessKey);
		if(variables!=null)
			for(String variableName:variables.keySet())
			{
				pib.addVariable(variableName, variables.get(variableName));
			}
		
		//这步会在流程历史中写入发起者
		Authentication.setAuthenticatedUserId(loginName);
		ProcessInstance pi= rsi.startProcessInstance(pib);
		
		//将第一个节点任务自动以当前发起流程用户完成
		Task task=getTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		getTaskService().claim(task.getId(), loginName);
		getTaskService().complete(task.getId());
		return pi;
	}

	/**
	 * 中止一个流程
	 * @param businessKey 业务单号
	 * @param terminalReason 中止原因
	 * @return 
	 */
	public static void cancelProcess(String businessKey, String cancelReason)
	{
		ProcessInstanceQuery piq=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey);
		String processInstanceId=piq.singleResult().getProcessInstanceId();
		getRuntimeService().deleteProcessInstance(processInstanceId, cancelReason);
	}
	
	/**
	 * 撤回一个流程。
	 * 便于修改后重新发
	 * @param businessKey 业务单号
	 * @param revokeReason 撤回原因
	 * @return 
	 * @throws Exception 
	 */
	public static void revokeProcess(String businessKey, String revokeReason) throws Exception
	{
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		if (pi==null)
		{
			//也可以自定义一个判定类，判定超过某个环节时不能追回
			throw new Exception("已完成任务不能追回！");
		}
//		Task t= getTaskService().createTaskQuery().taskId(taskid).singleResult();
		
//		ProcessDefinitionEntity processDefinitionImpl =  (ProcessDefinitionEntity) ((RepositoryServiceImpl)getRepositoryService()).getDeployedProcessDefinition(t.getProcessDefinitionId()).;  
        //获取需要提交的节点  
         String loginName=null;
		List<HistoricTaskInstance> htis=getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(pi.getProcessInstanceId()).finished().orderByTaskCreateTime().asc().list();
		if(htis.size()==0) throw new Exception("第一个环节不能回退");
		HistoricTaskInstance hti=htis.get(0);
		
    	String ta=hti.getTaskDefinitionKey();
    	loginName=hti.getAssignee();
        log.debug("Use Default 1st Activity Node : "+ta);

    	if (loginName ==null )
    	{
    		throw new Exception("不能回退到指定环节："+ ta);
    	}
    	
    	Task task=getTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		TaskServiceImpl tsi=(TaskServiceImpl)getTaskService();
		RejectTaskCmd rtc=new RejectTaskCmd(task.getId(), ta, "revoke", null);
		tsi.getCommandExecutor().execute(rtc);
 		Task t1=tsi.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		tsi.setAssignee(t1.getId(), loginName);

	}
	

	
	/**
	 * 挂起一个流程
	 * @param businessKey 业务单号
	 * @return 
	 */
	public static void suspendProcess(String businessKey)
	{
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		String processInstanceId=pi.getProcessInstanceId();
		getRuntimeService().suspendProcessInstanceById(processInstanceId);
	}

	/**
	 * 恢复一个流程
	 * @param businessKey 业务单号
	 * @return 
	 */
	public static void resumeProcess(String businessKey)
	{
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		String processInstanceId=pi.getProcessInstanceId();
		getRuntimeService().activateProcessInstanceById(processInstanceId);
	}
	
	/**
	 * 查询一个流程的运行图.
	 * 它需要和getActivityCoordinate配合起来展示.
	 * web层直接把InputStream写入response中。
	 * @param businessKey 业务单号
	 * @return 
	 * @throws Exception 
	 */
	public static InputStream getProcessDiagram(String businessKey) throws Exception
	{
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		if (pi==null)
		{
			HistoricProcessInstance hpi=getHistoryService().createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
			return getRepositoryService().getProcessDiagram(hpi.getProcessDefinitionId());
			
		}
		return getRepositoryService().getProcessDiagram(pi.getProcessDefinitionId());
	}
	
	/**
	 * 获取流程运行图当前活动坐标
	 * @param businessKey 业务单号
	 * @return 
	 * @throws Exception 
	 */
	public static JSONObject getActivityCoordinate(String businessKey) throws Exception
	{
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		if (pi==null)
		{
			return new JSONObject();
		}
		ProcessDefinitionEntity pd =  (ProcessDefinitionEntity) getRepositoryService().getProcessDefinition(pi.getProcessDefinitionId());

		JSONObject jo=new JSONObject();
		jo.put("deploymentId", pd.getDeploymentId());
		jo.put("imageName",pd.getDiagramResourceName());	
		
		// 1. 获取到当前活动的ID
		String currentActivitiId = pi.getActivityId();
		ActivityImpl activity = pd.findActivity(currentActivitiId);
		// 4. 获取活动的坐标
		JSONObject coordinates=new JSONObject();
		coordinates.put("x", activity.getX());
		coordinates.put("y", activity.getY());
		coordinates.put("width", activity.getWidth());
		coordinates.put("height", activity.getHeight());
		jo.put("coordinate", coordinates);
		
		return jo;
	}
	
	/**
	 * 查询当前节点可以指派的人
	 * @param businessKey 业务单号
	 * @return 
	 */
	public static List<JSONObject> getUsersForAssignee(String businessKey)
	{
		List<JSONObject> jl=new ArrayList<JSONObject>();
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();

		if (pi==null) return null;
		Task task=WorkflowUtil.getTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
	
		List<IdentityLink> ils=getTaskService().getIdentityLinksForTask(task.getId());
		if (ils!=null && ils.size()>0)
		{
			String groupid=ils.get(0).getGroupId();
			if (groupid!=null && !groupid.isEmpty())
			{
				List<User> users=WorkflowUtil.getIdentityService().createUserQuery().memberOfGroup(groupid).orderByUserFirstName().asc().list();
				for(User u:users)
				{
					JSONObject jo=new JSONObject();
					jo.put("loginName",u.getId());
					jo.put("name",u.getFirstName());
					jl.add(jo);
				}
			}
		}
		return jl;
		
	}
	
	/**
	 * 查询当前节点可以转派的人
	 * @param businessKey 业务单号
	 * @return 
	 */
	public static List<JSONObject> getUsersForShift(String businessKey)
	{
		List<JSONObject> jl=new ArrayList<JSONObject>();
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		
		ProcessDefinitionEntity processDefinitionImpl =  (ProcessDefinitionEntity) ((RepositoryServiceImpl)getRepositoryService()).getDeployedProcessDefinition(pi.getProcessDefinitionId());
		
		ActivityImpl ai=processDefinitionImpl.findActivity(pi.getActivityId());
		//ai.getProperty(name)
		TaskDefinition td=(TaskDefinition) ai.getProperty("taskDefinition");
		Set<Expression> gl=td.getCandidateGroupIdExpressions();
		String groupid=null;
		if(gl!=null && gl.size()>0)
		{
			groupid=gl.iterator().next().getExpressionText();
		}
		if (groupid != null  && !groupid.isEmpty())
		{
			List<User> users=WorkflowUtil.getIdentityService().createUserQuery().memberOfGroup(groupid).orderByUserFirstName().asc().list();
			for(User u:users)
			{
				JSONObject jo=new JSONObject();
				jo.put("loginName",u.getId());
				jo.put("name",u.getFirstName());
				jl.add(jo);
			}
		}
		return jl;
		
	}
	
	}
	
	/**
	 * 流程定义相关
	 * @author Gun
	 *
	 */
	public static class DEF {

	/**
	 * 发布流程定义
	 * @param barFileName bar文件，里面可以包含多个流程定义文件。
	 * @return 
	 * @throws Exception 
	 */
	public static void deployProcessDef(String deployName, String barFileName) throws Exception
	{
		String path=ConfigPropertiesUtil.getString("workflow.bar.path");
		path= (path.trim().endsWith("/"))? path: path + "/";
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(path + barFileName));
		
		getRepositoryService().createDeployment()
				.name(deployName)
				.addZipInputStream(inputStream)
				.deploy();
	}
	
	/**
	 * 分页查询已发布的流程定义
	 * @param pageNo 页号
	 * @param pageSize 每页数量
	 * @return List<Deployment>
	 * @throws Exception 
	 */
	public static List<JSONObject> queryProcessDef(int pageNo, int pageSize) throws Exception
	{  
		int firstResult=(pageNo - 1)*pageSize ;
		List<JSONObject> ja= new ArrayList<JSONObject>();
		List<ProcessDefinition> pds=getRepositoryService().createProcessDefinitionQuery().orderByProcessDefinitionName().asc().listPage(firstResult, pageSize);
		for(ProcessDefinition pd: pds)
		{
			JSONObject jo = new JSONObject();//JSONObject.fromObject(pd);
			PropertyDescriptor[] ppds = PropertyUtils.getPropertyDescriptors(ProcessDefinition.class);
			for (PropertyDescriptor ppd: ppds)
			{
				String p=ppd.getName();
				
				jo.put(p,PropertyUtils.getSimpleProperty(pd,p));
			}
			Deployment d=getRepositoryService().createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult();
			jo.put("suspendState", pd.isSuspended());
			jo.put("deployName", d.getName());
			jo.put("deployTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d.getDeploymentTime()));
			ja.add(jo);
		}
	
		return ja;
		//return getRepositoryService().createDeploymentQuery().orderByDeploymentName().asc().listPage(firstResult, pageSize);
	}
	
	/**
	 * 失效一个流程定义
	 * @param processDefinitionKey 流程定义名
	 * @return 
	 * @throws Exception 
	 */
	public static void disableProcessDef(String processDefinitionKey) throws Exception
	{  
		getRepositoryService().suspendProcessDefinitionByKey(processDefinitionKey);;
	}
	
	/**
	 * 重新生效一个流程定义
	 * @param processDefinitionKey 流程定义名
	 * @return 
	 * @throws Exception 
	 */
	public static void enableProcessDef(String processDefinitionKey) throws Exception
	{  
		getRepositoryService().activateProcessDefinitionByKey(processDefinitionKey);
	}
	
	/**
	 * 删除一个发布
	 * @param processDefinitionKey 流程定义名
	 * @return 
	 * @throws Exception 
	 */
	public static void deleteProcessDef(String deployName) throws Exception
	{  
		getRepositoryService().deleteDeployment(deployName);
	}
	
	}
	
	
	
	/**
	 * 任务相关
	 * @author Gun
	 *
	 */
	public static class T {
		
	/**
	 * 分页查询待办任务
	 * @param loginName 用户登录名
	 * @param pageNo 页号
	 * @param pageSize 每页数量
	 * @return List<Deployment>
	 * @throws Exception 
	 */
	public static List<JSONObject> queryTodoTasks(String loginName, int pageNo, int pageSize) throws Exception
	{  
//		List<JSONObject> lj=new ArrayList<JSONObject>();
		JSONArray ja=getTaskLocalManager().queryTodoTasks(loginName, pageNo, pageSize);
//		for (Object o: ja.toArray())
//		{
//			JSONObject jo=new JSONObject();
//			JSONObject oo=(JSONObject)o;
//			jo.put("bizKey", oo.get("BIZ_KEY"));
//			jo.put("bizName", oo.get("BIZ_NAME"));
//			jo.put("id", oo.get("ID"));
//			jo.put("dueDate", oo.get("DUE_DATE"));
//			jo.put("createTime", oo.get("CREATE_TIME"));
//			jo.put("starter", oo.get("STARTER"));
//			jo.put("owner", oo.get("OWNER"));
//			lj.add(jo);
//		}
		return ja;
	}

	/**
	 * 分页查询待办任务
	 * @param loginName 用户登录名
	 * @param pageNo 页号
	 * @param pageSize 每页数量
	 * @return List<Deployment>
	 * @throws Exception 
	 */
	public static List<JSONObject> queryTasks(String loginName, int pageNo, int pageSize) throws Exception
	{  
		int firstResult=(pageNo - 1)*pageSize ;
		List<Task> taskList=getTaskService().createTaskQuery()
				.taskCandidateOrAssigned(loginName)
				.orderByTaskCreateTime()
				.desc()
				.listPage(firstResult, pageSize);
		List<JSONObject> ja= new ArrayList<JSONObject>();
		for(Task t:taskList)
		{
			JSONObject jo = new JSONObject();//JSONObject.fromObject(pd);
			PropertyDescriptor[] ppds = PropertyUtils.getPropertyDescriptors(TaskInfo.class);
			for (PropertyDescriptor ppd: ppds)
			{
				String p=ppd.getName();
				Object o=PropertyUtils.getSimpleProperty(t,p);
				if ( o instanceof Date)
					jo.put(p, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(o));
				else
					jo.put(p, o);
			}
			HistoricProcessInstance hpi=getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
			ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
			jo.put("bizKey", pi.getBusinessKey());
			jo.put("bizName", pi.getName());
			if(hpi!=null)
				jo.put("starter", hpi.getStartUserId());
			else
				jo.put("starter", "");
			ja.add(jo);
			
		}
		;
		return ja;
	}
	
	/**
	 * 根据taskid查询一个任务
	 * @param taskid 任务ID
	 * @return 
	 * @throws Exception 
	 */
	public static JSONObject getTask(String taskid) throws Exception
	{  
		Task t= getTaskService().createTaskQuery().taskId(taskid).singleResult();
		if (t==null) throw new Exception("没有找到任务信息。");
		JSONObject jo = new JSONObject();//JSONObject.fromObject(pd);
		PropertyDescriptor[] ppds = PropertyUtils.getPropertyDescriptors(TaskInfo.class);
		for (PropertyDescriptor ppd: ppds)
		{
			String p=ppd.getName();
			Object o=PropertyUtils.getSimpleProperty(t,p);
			if ( o instanceof Date)
				jo.put(p, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(o));
			else
				jo.put(p, o);
		}
		HistoricProcessInstance hpi=getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
		jo.put("bizKey", pi.getBusinessKey());
		jo.put("bizName", pi.getName());
		if(hpi!=null)
			jo.put("starter", hpi.getStartUserId());
		else
			jo.put("starter", "");
		return jo;
	}

	
	/**
	 * 接受一个任务
	 * @param taskid 任务ID
	 * @param loginName 登录用户名
	 * @return 
	 * @throws Exception 
	 */
	public static void todoTask(String taskid, String loginName) throws Exception
	{  
		log.debug("to do a task : " + taskid);
		getTaskService().claim(taskid, loginName);
	}
	
	/**
	 * 放弃接受的任务
	 * @param taskid 任务ID
	 * @param loginName 登录用户名
	 * @return 
	 * @throws Exception 
	 */
	public static void dropTask(String taskid) throws Exception
	{  
		log.debug("drop a task : " + taskid);
		getTaskService().unclaim(taskid);
	}
	
	/**
	 * 转办一个任务
	 * @param taskid 任务ID
	 * @param loginName 登录用户名
	 * @return 
	 * @throws Exception 
	 */
	public static void shiftTask(String taskid, String loginName) throws Exception
	{  
		log.debug("shift a task : " + taskid);
		getTaskService().setAssignee(taskid, loginName);
	}
	
	/**
	 * 分派一个任务
	 * @param businessKey 业务单号
	 * @param loginName 分派的用户
	 * @return 
	 * @throws Exception 
	 */
	public static void assignTask(String businessKey, String loginName) throws Exception
	{  
		ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		Task task=getTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		log.debug("assign a task : " + task.getId());
		getTaskService().setAssignee(task.getId(), loginName);
	}
	
	/**
	 * 驳回任务。
	 * 
	 * @param taskid 业务单号
	 * @param toActivity 退回的环节
	 * @param rejectReason 驳回原因
	 * @return 
	 * @throws Exception 
	 */
	public static void rejectTask(String taskid, String toActivity, String rejectReason) throws Exception
	{
		Task t= getTaskService().createTaskQuery().taskId(taskid).singleResult();
		
//		ProcessDefinitionEntity processDefinitionImpl =  (ProcessDefinitionEntity) ((RepositoryServiceImpl)getRepositoryService()).getDeployedProcessDefinition(t.getProcessDefinitionId()).;  
        //获取需要提交的节点  
        String ta=toActivity;
        String loginName=null;
		List<HistoricTaskInstance> htis=getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(t.getProcessInstanceId()).finished().orderByTaskCreateTime().asc().list();
		if(htis.size()==0) throw new Exception("第一个环节不能回退");
		if (toActivity==null || toActivity.isEmpty())
        {
    		HistoricTaskInstance hti=htis.get(0);
    		ta=hti.getTaskDefinitionKey();
    		loginName=hti.getAssignee();
         	log.debug("Use Default 1st Activity Node : "+ta);
        }
        else
        {
        	for(HistoricTaskInstance hti: htis)
        	{
        		
        		if (toActivity.equals(hti.getTaskDefinitionKey()))
        		{
        			loginName=hti.getAssignee();
        			break;
        		}
        	}
        }
    	if (loginName ==null )
    	{
    		throw new Exception("不能回退到指定环节："+ ta);
    	}
		TaskServiceImpl tsi=(TaskServiceImpl)getTaskService();
		RejectTaskCmd rtc=new RejectTaskCmd(taskid, ta, "reject", null);
		tsi.getCommandExecutor().execute(rtc);
		Task t1=tsi.createTaskQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
		tsi.setAssignee(t1.getId(), loginName);


	}

	/**
	 * 回退一个任务
	 * @param taskid 任务ID
	 * @return 
	 * @throws Exception 
	 */
	public static void doneTask(String taskid) throws Exception
	{  
		getTaskService().complete(taskid);
	}
	
	/**
	 * 转派一个任务
	 * @param taskid 任务ID
	 * @param toUser 登录用户名
	 * @return 
	 * @throws Exception 
	 */
	public static void delegateTask(String taskid, String toUser) throws Exception
	{  
		getTaskService().delegateTask(taskid, toUser);
	}
	
	
	/**
	 * 完成转派任务
	 * @param taskid 任务ID
	 * @return 
	 * @throws Exception 
	 */
	public static void resolveTask(String taskid) throws Exception
	{  
		getTaskService().resolveTask(taskid, null);
	}
	}
	
	/**
	 * 历史相关
	 * @author Gun
	 *
	 */
	public static class H {
		
		/**
		 * 获取流程任务处理过程
		 * @param bizKey
		 * @return
		 * @throws Exception
		 */
		public static List<JSONObject> getProcessTaskHis(String bizKey) throws Exception
		{
			ProcessInstance pi=getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(bizKey).singleResult();
			String processInstanceId;
			if (pi==null)
			{
				HistoricProcessInstance hpi=getHistoryService().createHistoricProcessInstanceQuery().processInstanceBusinessKey(bizKey).singleResult();
				if (hpi==null) return null;
				processInstanceId=hpi.getId();
			}
			else
			{
				processInstanceId=pi.getProcessInstanceId();
			}
			List<JSONObject> jList=new ArrayList<JSONObject>();
			List<HistoricTaskInstance> hais=getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().list();
			for(HistoricTaskInstance hai: hais)
			{
				JSONObject jo = new JSONObject();//JSONObject.fromObject(pd);
				PropertyDescriptor[] ppds = PropertyUtils.getPropertyDescriptors(HistoricTaskInstance.class);
				for (PropertyDescriptor ppd: ppds)
				{
					String p=ppd.getName();
					Object o=PropertyUtils.getSimpleProperty(hai,p);
					if ( o instanceof Date)
						jo.put(p, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(o));
					else
						jo.put(p, o);
				}
				ppds = PropertyUtils.getPropertyDescriptors(TaskInfo.class);
				for (PropertyDescriptor ppd: ppds)
				{
					String p=ppd.getName();
					Object o=PropertyUtils.getSimpleProperty(hai,p);
					if ( o instanceof Date)
						jo.put(p, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(o));
					else
						jo.put(p, o);
				}
				jList.add(jo);
			}
					
			return jList;
		}

		/**
		 * 查询已办任务
		 * @param loginName 用户登录名
		 * @param pageNo 页号
		 * @param pageSize 每页数量
		 * @return List<Deployment>
		 * @throws Exception 
		 */
		public static List<JSONObject> queryDoneTasks(String loginName, int pageNo, int pageSize) throws Exception
		{  
			JSONArray ja=getTaskLocalManager().queryDoneTasks(loginName, pageNo, pageSize);

			return ja;
		}
	}
}
