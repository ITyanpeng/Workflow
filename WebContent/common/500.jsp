<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.Enumeration,java.util.Iterator"%>
<script type="text/javascript" language="javascript">
function showErr(){
var isHidde = document.all.isHidde.value;
//alert(isHidde);
if( isHidde == "true" ){
   document.all.errdiv.style.display='block';
   document.all.isHidde.value= 'false';
   document.all.showbtn.value="隐藏错误信息";
}else{
   document.all.errdiv.style.display='none';
   document.all.isHidde.value= 'true';
   document.all.showbtn.value="显示错误信息";
}
}
</script>
<html>
<head>
<title>this is failure</title>
</head>
<body onload="showErr()">
出错啦。。。。。。。。。。。。。。。
<br />
<c:set value="${exception}" var="ee" />
<jsp:useBean id="ee" type="java.lang.Exception" />
<%=ee.getMessage()%><br />
<input type="hidden" id="isHidde" value="false" />
<input type="button" id="showbtn" onclick="showErr();" />
<br>
<table id="errdiv" align="center" bgcolor="black">
	<tr>
		<td><font color="green"> <%
 	ee.printStackTrace(new java.io.PrintWriter(out));
 %> </font></td>
	</tr>
</table>
</body>
</html>
