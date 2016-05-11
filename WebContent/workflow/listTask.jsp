<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
<title>定义的流程</title>
</head>
<body><form action="${ctx}/workflow/listTask.do" method="post">
  <label>选择用户
  <select name="loginName" id="loginName">
    <option value="finance">xx财务</option>
    <option value="finance2">yy财务</option>
    <option value="manager">部门经理</option>
    <option value="casher">出纳</option>
    <option value="general">总经理</option>
    <option value="gun">gun</option>
  </select>
  </label>
  <p>
    <input type="submit" name="Submit" value="查询">
  </p>
</form>
<br>
                <table width="100%" border="0" cellpadding="0" cellspacing="0" class="table_list">
                    <tr>
                        <th><b>业务单号</b></th>
                        <th><b>业务单名称</b></th>
                        <th><b>发起者</b></th>
                        <th><b>任务到期日期</b></th>
                        <th><b>任务所有者</b></th>
                        <th><b>任务创建时间</b></th>
                        <th class="right_title_bg"><b>操作</b></th>
                    </tr>
                    
                    <c:forEach items="${tasks}" var="item">
						<tr>
						<!-- 根据返回的不同而设置
							<td height="30px">${item.bizKey}</td>
							<td>${item.bizName}</td>
							<td>${item.starter}</td>
							<td>${item.dueDate}</td>
							<td>${item.owner}</td>
							<td>${item.createTime}</td>
							<td>
								<a href="###" onClick="doit('${item.id}');"><img src="${ctx}/images/table_operation_icon1.png" border="0" title="办理"/></a>&nbsp;
							</td>
							-->
							<td height="30px">${item.BIZ_KEY}</td>
							<td>${item.BIZ_NAME}</td>
							<td>${item.STARTER}</td>
							<td>${item.DUE_DATE}</td>
							<td>${item.OWNER}</td>
							<td>${item.CREATE_TIME}</td>
							<td>
								<a href="###" onClick="doit('${item.ID}');"><img src="${ctx}/images/table_operation_icon1.png" border="0" title="办理"/></a>&nbsp;
							</td>
					</tr>
					</c:forEach>
</table>    
<form id='doform' method='post'>
<input type='hidden' id='taskid' name='taskid' value=''>
<input type='hidden' id='curUser' name='curUser' value=''>
</form>   
<script type="text/javascript">
function doit(task)
{
	$("#taskid").val(task);
	//alert($("#user").val());
	$("#curUser").val($("#loginName").val());
	$("#doform").attr('action',"${ctx}/workflow/showTask.do");
	$("#doform").submit();
}   
</script>
</body>
</html>