package cn.sccl.workflow.express;

import org.activiti.engine.delegate.DelegateExecution;

public class MyWorkflowBean {
	public boolean needUpgrade(DelegateExecution de)
	{
		System.out.println("BizKey ----------> "+de.getProcessBusinessKey());
		//to do 可以根据业务单号获取业务数据进行判断
		System.out.println("processInstanceId ----------> "+de.getProcessInstanceId());
		return true;
	}
}
