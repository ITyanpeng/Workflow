<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
<title>发布流程</title>
</head>
<body>
<form action="${ctx}/workflow/upload.do" method="post" enctype="multipart/form-data" name="form1">
  <label>选择流程部署文件：
  <input type="file" name="file">
  </label>
  <p>
    <label>部署名称：
    <input type="text" name="deployName">
    </label>
  </p>
  <p>&nbsp;</p>
  <p>
    <label>
    <input type="submit" name="Submit" value="发布">
    </label>
    <input name="button" type="reset" id="reset" value="重置">
  </p>
</form>
</body>

</html>