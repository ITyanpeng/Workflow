package cn.sccl.workflow.ext;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展用于任务拒绝或者回退的命令
 * @author Gun
 *
 */
public class RejectTaskCmd extends NeedsActiveTaskCmd<Void>{
	   private static final long serialVersionUID = 1L;  
		private static Logger	log	= LoggerFactory.getLogger(RejectTaskCmd.class);
	      
	    /** 
	     * 目标任务的定义Id  
	     */  
	    private String toActivity;  
	    /** 
	     * 参数 
	     */  
	    protected Map variables;  
	    /** 
	     * jump跳跃 ，turnback 退回（） 
	     */  
	    protected String type;    
	    
	/**
	 * 初始化
	 * @param _taskId  任务id
	 * @param _toActivity  要退回的节点
	 * @param _type  退回的类型：reject：驳回；revoke: 召回
	 * @param _variables
	 */
	public RejectTaskCmd(String _taskId, String _toActivity,String _type,Map _variables) {  
        super(_taskId);  
        this.toActivity = _toActivity;  
        this.type = _type;  
        this.variables = _variables;  
          
    }  

	@Override
	protected Void execute(CommandContext commandContext,
			TaskEntity task) {
		if(variables != null) task.setExecutionVariables(variables);  
        
        ExecutionEntity execution = task.getExecution();  
        //流程定义id        
        String procDefId = execution.getProcessDefinitionId();     
        //获取服务         
        RepositoryServiceImpl repositoryService =  (RepositoryServiceImpl)execution.getEngineServices().getRepositoryService();   
        //获取流程定义的所有节点  
        ProcessDefinitionImpl processDefinitionImpl =  (ProcessDefinitionImpl)repositoryService.getDeployedProcessDefinition(procDefId);  
        //获取需要提交的节点  
        ActivityImpl toActivityImpl = processDefinitionImpl.findActivity(this.toActivity);  
           
        if(toActivityImpl == null ){   
        	log.error(this.toActivity+" to ActivityImpl is null!");  
        }else{  
        	List<Task> tasks=execution.getEngineServices().getTaskService().createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        	for(Task t: tasks)
        	{ //存在多个待办任务时回退
        		TaskEntity te = commandContext.getTaskEntityManager().findTaskById(t.getId());
        		te.fireEvent("complete");  
        		commandContext.getTaskEntityManager().deleteTask(te,this.type, false);   
            	execution.removeTask(te);//执行规划的线  
        	}
            execution.executeActivity(toActivityImpl);  
            
//    		task.fireEvent("complete");  
//        	Context.getCommandContext().getTaskEntityManager().deleteTask(task,this.type, false);   
//        	execution.removeTask(task);//执行规划的线  

        }  
        return null;
 	}

}
