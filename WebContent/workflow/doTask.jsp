<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>

</head>
<body>
<form id="taskForm" method="post" >
  <table width="748" border="0" cellpadding="0" cellspacing="0" class="table_list">
    <tr>
      <td width="297"><label>业务单号
          <input name="bizKey" type="text" id="bizKey" value='${task.bizKey}'>
      </label></td>
      <td width="453"><label>业务单名
        <input name="bizName" type="text" id="bizName" size="50" maxlength="50" value='${task.bizName}'>
      </label></td>
    </tr>

    <tr>
      <td>业务数据xxx</td>
      <td>业务数据yyy</td>
    </tr>
    <tr>
      <td>业务数据zzz</td>
      <td>业务数据sss</td>
    </tr>
  </table>
  <!-- 
  当前处理人（测试需要）：
    <select name="loginName">
    <option value="finance">finance</option>
    <option value="manager">manager</option>
    <option value="casher">casher</option>
    <option value="gun">gun</option>
  </select>
   -->
    <label>如果要转办，选择：
   <select name="shift2User" id="shift2User" >
  <c:forEach items="${users2Shift}" var="item">
    <option value="${item.loginName}">${item.name}</option>
  </c:forEach>
  </select>
	     </label>
  <p>
    <label>办理意见
    <textarea name="dealInfo" cols="50" rows="3"></textarea>
    </label>
  </p>
  <p>
  <input type='hidden' id='loginName' name='loginName' value='${loginName}'>
  <input type='hidden' id='taskid' name='taskid' value='${task.id}'>
  </p>
  <c:if test="${task.assignee==null}">
  <!-- 
    <input name="doit" type="button" id="doit" value="我来" onclick='doit();'>
     -->
 	<a href="###" onClick="ido();"><img src="${ctx}/images/ido.png" border="0" title="我来处理"/></a>&nbsp;
    </c:if>
  <c:if test="${task.assignee!=null}">
 	<a href="###" onClick="agree();"><img src="${ctx}/images/agree.png" border="0" title="同意"/></a>&nbsp;
 	<a href="###" onClick="reject();"><img src="${ctx}/images/reject.png" border="0" title="拒绝"/></a>&nbsp;
 	<a href="###" onClick="shift();"><img src="${ctx}/images/shift.png" border="0" title="转派"/></a>&nbsp;
 	<a href="###" onClick="drop();"><img src="${ctx}/images/drop.png" border="0" title="放弃任务"/></a>&nbsp;
    <!-- 
    <input name="agree" type="button" id="agree" value="同意" onclick='agree();'>
    <input name="reject" type="button" id="reject" value="拒绝" onclick='reject();'>
     -->
    </c:if>
  

</form>
<br>
流程图:
<br>
<!-- 1.获取到规则流程图 这里是用的strust2的标签得到上面上面放入值栈的值 -->
<img id='diagram' style="position: relative;top: 0px;left: 0px;" src="${ctx}/workflow/showDiagram.do?bizKey=${task.bizKey}">


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

function ido()
{
	//alert("${ctx}");
	$("#taskForm").attr('action',"${ctx}/workflow/todoTask.do");
	$("#taskForm").submit();
}

function agree()
{
	$("#taskForm").attr('action',"${ctx}/workflow/doneTask.do");
	$("#taskForm").submit();
}

function reject()
{
	$("#taskForm").attr('action',"${ctx}/workflow/rejectTask.do");
	$("#taskForm").submit();
}

function shift()
{
	$("#loginName").val($("#shift2User").val());
	$("#taskForm").attr('action',"${ctx}/workflow/shiftTask.do");
	$("#taskForm").submit();
}

function drop()
{
	$("#taskForm").attr('action',"${ctx}/workflow/dropTask.do");
	$("#taskForm").submit();
}
</script>

</body>
</html>