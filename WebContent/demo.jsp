<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
<title>流程组件示例</title>
</head>
<body>
<table width="900" height="665" border="1">
  <tr>
    <td width="200" valign="top">
    <p><a target='show' href="workflow/deploy.jsp">发布流程定义</a></p>
    <p><a target='show' href="workflow/listProcessDef.do">查询流程定义</a></p>
    <p><a target='show' href="workflow/startAProcess.jsp">发起流程</a></p>
    <p><a target='show' href="workflow/showProcess.jsp">查询/处理流程</a></p>
    <p><a target='show' href="workflow/listTask.jsp">查询待办</a></p>
    <p><a target='show' href="workflow/listDoneTask.jsp">查询已办</a></p>
    </td>
    <td width="700"><iframe name='show' width="700" height="660"></iframe>
      <div align="left"></div></td>
  </tr>
</table>
</body>
</html>