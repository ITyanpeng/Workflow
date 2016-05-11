package cn.sccl.workflow.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.sccl.common.util.ConfigPropertiesUtil;
import cn.sccl.common.util.WebUtil;
import cn.sccl.workflow.util.WorkflowUtil;


/**
 * 本例中直接在Controller中调用WorkflowUtil相关方法，正式使用时请封装到manager中，以便控制和业务逻辑处理事务一致性。
 * @author Gun
 *
 */
public class WorkflowController extends MultiActionController {
	
	/**
	 * 展示流程。
	 * 
	 * 运行状态需和getActivityCoordinate配合使用
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void showDiagram(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String businessKey=request.getParameter("bizKey");
		InputStream in=WorkflowUtil.P.getProcessDiagram(businessKey);
		WebUtil.returnStream(response, in,"image/jpeg");
	}
	

	/**
	 * 上传并发布流程定义的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView upload(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv = new ModelAndView("forward:/workflow/listProcessDef.do");
		String deployName = request.getParameter("deployName");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Set<MultipartFile> fileSet = new LinkedHashSet<MultipartFile>();
		for (Iterator it = multipartRequest.getFileNames(); it.hasNext();) {
			String key = (String) it.next();
			MultipartFile file = multipartRequest.getFile(key);
			if (file.getOriginalFilename().length() > 0) {
				fileSet.add(file);
			}
		}
		saveFile(fileSet);
		String fileName=fileSet.iterator().next().getOriginalFilename();
		WorkflowUtil.DEF.deployProcessDef(deployName, fileName);
		mv.addObject("pageNo", 1);
		mv.addObject("pageSize", 10);
		
		return mv;
	}

	
	public void saveFile(Set<MultipartFile> set) throws IOException
	{
		String path = ConfigPropertiesUtil.getString("workflow.bar.path");
		File dir = new File(path);
		if (!dir.exists())
		{
			dir.mkdirs();
			String os = System.getProperty("os.name");
			if (os.equals("Linux"))
			{
				Runtime.getRuntime().exec("chmod 775 " + path);
			}
	
		}
		for (MultipartFile item : set)
		{
			FileCopyUtils.copy(item.getBytes(), new File(path+"/"+item.getOriginalFilename()));
	
		}
	}
	
	/**
	 * 查询流程定义的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listProcessDef(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/listProcessDef");
		String pn=request.getParameter("pageNo");
		int pageNo;
		int pageSize ;
		if(pn==null)
		{
			pageNo=1;
			pageSize=20;
			
		}
		else
		{
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		 	pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		
		mv.addObject("processDefs",WorkflowUtil.DEF.queryProcessDef(pageNo, pageSize));
		return mv;
	}
	
	/**
	 * 发起一个流程的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView startAProcess(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/assignTo");
		String loginName=request.getParameter("loginName");
		String processDefKey=request.getParameter("processDefKey");
		String bizKey=request.getParameter("bizKey");
		String BizName=request.getParameter("bizName");
		WorkflowUtil.P.startProcess(processDefKey, bizKey, BizName, loginName, null);
		
		//为当前环节获取可分派的人，可以根据实际情况进行调整
		List<JSONObject> users=WorkflowUtil.P.getUsersForAssignee(bizKey);
		if (users==null || users.size()==0 )
		{
			return listTask(request, response );
		}
		else if(users.size()==1)
		{//当只有一个待选人的时候，自动分派
			
			WorkflowUtil.T.assignTask(bizKey, users.get(0).getString("loginName"));
			return listTask(request, response );
		}
		else
		{
			mv=new ModelAndView("workflow/assignTo");
			mv.addObject("users2Assign",users);
			mv.addObject("bizKey",bizKey);
		}
//		mv.addObject("action","发起流程");
		return mv;
		
	}
	
	/**
	 * 查询待办任务的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/listTask");
		String loginName=request.getParameter("loginName");
		String pn=request.getParameter("pageNo");
		int pageNo;
		int pageSize ;
		if(pn==null)
		{
			pageNo=1;
			pageSize=20;
			
		}
		else
		{
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		 	pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		
//		mv.addObject("tasks",WorkflowUtil.T.queryTasks(loginName, pageNo, pageSize));
		mv.addObject("tasks",WorkflowUtil.T.queryTodoTasks(loginName, pageNo, pageSize));
		return mv;
	}
	
	
	/**
	 * 查询待办任务的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listDoneTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/listDoneTask");
		String loginName=request.getParameter("loginName");
		String pn=request.getParameter("pageNo");
		int pageNo;
		int pageSize ;
		if(pn==null)
		{
			pageNo=1;
			pageSize=20;
			
		}
		else
		{
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		 	pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		
//		mv.addObject("tasks",WorkflowUtil.T.queryTasks(loginName, pageNo, pageSize));
		mv.addObject("tasks",WorkflowUtil.H.queryDoneTasks(loginName, pageNo, pageSize));
		return mv;
	}
	
	/**
	 * 展示一个指定任务的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/doTask");
		String taskid=request.getParameter("taskid");
		String loginName=request.getParameter("curUser");
	
		JSONObject task=WorkflowUtil.T.getTask(taskid);
		mv.addObject("task",task);
		mv.addObject("loginName",loginName);
		mv.addObject("coordinate",WorkflowUtil.P.getActivityCoordinate(task.getString("bizKey")).get("coordinate"));
		mv.addObject("activityHis", WorkflowUtil.H.getProcessTaskHis(task.getString("bizKey")));
		
		mv.addObject("users2Shift",WorkflowUtil.P.getUsersForShift(task.getString("bizKey")));
		return mv;
	}
	
	/**
	 * 显示流程信息的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showProcess(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/showProcess");
		String bizKey=request.getParameter("businessKey");
	
		mv.addObject("bizKey",bizKey);
		mv.addObject("coordinate",WorkflowUtil.P.getActivityCoordinate(bizKey).get("coordinate"));
		mv.addObject("activityHis", WorkflowUtil.H.getProcessTaskHis(bizKey));
		
		return mv;
	}
	
	/**
	 * 追回流程的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView revokeProcess(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv=new ModelAndView("workflow/showProcess");
		String bizKey=request.getParameter("businessKey");
		String revokenReason=request.getParameter("revokenReason");
		WorkflowUtil.P.revokeProcess(bizKey, revokenReason);
		mv.addObject("bizKey",bizKey);
		mv.addObject("coordinate",WorkflowUtil.P.getActivityCoordinate(bizKey).get("coordinate"));
		mv.addObject("activityHis", WorkflowUtil.H.getProcessTaskHis(bizKey));
		
		return mv;
	}
	
	
	/**
	 * 接收任务的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView todoTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		//ModelAndView mv=new ModelAndView("forward:doTask.do");
		String taskid=request.getParameter("taskid");
		String loginName=request.getParameter("loginName");
		
		WorkflowUtil.T.todoTask(taskid, loginName);
		//mv.addObject("taskid", taskid);
		return showTask(request, response );
	}
	
	/**
	 * 完成任务的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doneTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		ModelAndView mv;
		String taskid=request.getParameter("taskid");
		String loginName=request.getParameter("loginName");
		
		JSONObject task=WorkflowUtil.T.getTask(taskid);
		WorkflowUtil.T.doneTask(taskid);
		List<JSONObject> users=WorkflowUtil.P.getUsersForAssignee(task.getString("bizKey"));
		if (users==null || users.size()==0 )
		{
			return listTask(request, response );
		}
		else if(users.size()==1)
		{//当只有一个待选人的时候，自动分派
			
			WorkflowUtil.T.assignTask(task.getString("bizKey"), users.get(0).getString("loginName"));
			return listTask(request, response );
		}
		else
		{
			mv=new ModelAndView("workflow/assignTo");
			mv.addObject("users2Assign",users);
			mv.addObject("bizKey",task.getString("bizKey"));
		}
		
		
		return mv;
	}
	
	/**
	 * 驳回的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView rejectTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		//ModelAndView mv=new ModelAndView("forward:listTask.do");
		String taskid=request.getParameter("taskid");
		String bizKey=request.getParameter("bizKey");
		String rejectReason=request.getParameter("dealInfo");
		
		WorkflowUtil.T.rejectTask(taskid, null, rejectReason);
		return listTask(request, response );
	}
	
	/**
	 * 放弃任务，将它返回待处理池的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dropTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		//ModelAndView mv=new ModelAndView("forward:listTask.do");
		String taskid=request.getParameter("taskid");
		
		WorkflowUtil.T.dropTask(taskid);
		return listTask(request, response );
	}
	
	/**
	 * 转派给其它人的例子， 没有使用activiti自带的delegate方法。直接修改分派人。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView shiftTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		//ModelAndView mv=new ModelAndView("forward:listTask.do");
		String taskid=request.getParameter("taskid");
		String loginName=request.getParameter("loginName");
		
		WorkflowUtil.T.shiftTask(taskid, loginName);
		return listTask(request, response );
	}
	
	/**
	 * 分派任务给某人的例子
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView assignTask(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		//ModelAndView mv=new ModelAndView("forward:listTask.do");
		String loginName=request.getParameter("assign2User");
		String bizKey=request.getParameter("bizKey");
		
		WorkflowUtil.T.assignTask(bizKey, loginName);
		return listTask(request, response );
	}
}
