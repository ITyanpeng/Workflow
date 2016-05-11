<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
</head>
<body>
<form  method="post" id="taskForm">
  <label>选择下一环节处理人：
  <select name="assign2User">
  <c:forEach items="${users2Assign}" var="item">
    <option value="${item.loginName}">${item.name}</option>
  </c:forEach>
  </select>
  </label>
  <p>
  <!-- <img src="${ctx}/images/agree.png" border="0" title="继续"/>
  <img src="${ctx}/images/drop.png" border="0" title="跳过"/>
   -->
   <input type='hidden' id='bizKey' name='bizKey' value='${bizKey}'>
 	<a href="###" onClick="go();" >继续</a>&nbsp;
 	<a href="###" onClick="skip();">跳过</a>&nbsp;
  </p>
</form>

<script type="text/javascript">
	function skip()
	{
		$("#taskForm").attr('action',"${ctx}/workflow/listTask.do");
		$("#taskForm").submit();
	}
	
	function go()
	{
		$("#taskForm").attr('action',"${ctx}/workflow/assignTask.do");
		$("#taskForm").submit();
	}
</script>
</body>
</html>