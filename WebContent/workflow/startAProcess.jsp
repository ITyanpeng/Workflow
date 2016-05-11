<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
</head>
<body>
<form action="${ctx}/workflow/startAProcess.do" method="post">
  <label>流程代码
  <input type="text" name="processDefKey" value="myProcess">
  </label>
  <p>
    <label>用户
    <input type="text" name="loginName" value="gun">
    </label>
  </p>
  <p>
    <label>单号
    <input type="text" name="bizKey" value="ch-1">
    </label>
  </p>
  <p>
    <label>单名
    <input type="text" name="bizName" value="审核1">
    </label>
</p>
  <p>
    <input type="submit" name="Submit" value="提交">
    <input name="reset" type="reset" id="reset" value="重置">
  </p>
</form>
</body>
</html>