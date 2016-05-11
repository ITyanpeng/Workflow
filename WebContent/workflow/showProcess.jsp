<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>

</head>
<body>
<form  method="post" id="pForm">
业务单号
  <input type="text" name="businessKey" value="${bizKey}">
追回原因  <input type="text" name="revokenReason">
  <br>
 	<a href="###" onClick="query();" >查询</a>&nbsp;
 	<a href="###" onClick="revoke();">追回</a>&nbsp;
</form>

流程图:
<br>
<!-- 1.获取到规则流程图 这里是用的strust2的标签得到上面上面放入值栈的值 -->
<c:if test="${bizKey!=null}">
<img id='diagram' style="position: relative;top: 0px;left: 0px;" src="${ctx}/workflow/showDiagram.do?bizKey=${bizKey}">
</c:if>

<!-- 2.根据当前活动的坐标，动态绘制DIV -->
<div id='curPos' style="display:block;z-index=10">
</div>
<br>
历史环节：
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="table_list">
  <tr>
    <th><b>环节</b></th>
    <th><b>办理人</b></th>
    <th><b>创建时间</b></th>
    <th><b>办理时间</b></th>
  </tr>
  
  <c:forEach items="${activityHis}" var="item">
	  <tr>
	    <td  height="30px">${item.name}</td>
	    <td>${item.assignee}</td>
	    <td>${item.startTime}</td>
	    <td>${item.endTime}</td>
	  </tr>
  </c:forEach>

</table>
<script type="text/javascript">

function showCurNode(e)
{  
//	alert(e)
    var t=e.offsetTop;  
    var l=e.offsetLeft;  
    while(e=e.offsetParent)
    { 
             t+=e.offsetTop; 
             l+=e.offsetLeft; 
    }   
	t+=eval("${coordinate.y}")-2;
	l+=eval("${coordinate.x}")-2;
//    var style = "position: absolute;border:1px solid red;z-index:1;top:"+t+"px;left:"+l+"px;width:${coordinate.width}px;height:${coordinate.height}px;";
//	alert(style);
	//$("#curPos").css("border","1px solid red"); 
	$('#curPos').css({"position":"absolute","border":"2px solid red","z-index":"1","top":t+"px","left":l+"px",
		"width":"${coordinate.width}px","height":"${coordinate.height}px"});
	
}
showCurNode(document.getElementById('diagram'));


	function query()
	{
		$("#pForm").attr('action',"${ctx}/workflow/showProcess.do");
		$("#pForm").submit();
	}
	
	function revoke()
	{
		$("#pForm").attr('action',"${ctx}/workflow/revokeProcess.do");
		$("#pForm").submit();
	}


</script>

</body>
</html>