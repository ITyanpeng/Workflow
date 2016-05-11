<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/common/taglibs.jsp"%>
<title>定义的流程</title>
</head>
<body>
                <table width="100%" border="0" cellpadding="0" cellspacing="0" class="table_list">
                    <tr>
                        <th><b>流程代码</b></th>
                        <th><b>流程名</b></th>
                        <th><b>分类</b></th>
                        <th><b>版本</b></th>
                        <th><b>发布名</b></th>
                        <th><b>发布时间</b></th>
                        <th class="right_title_bg"><b>操作</b></th>
                    </tr>
                    
                    <c:forEach items="${processDefs}" var="item">
						<tr>
							<td height="30px">${item.key}</td>
							<td>${item.name}</td>
							<td>${item.category}</td>
							<td>${item.version}</td>
							<td>${item.deployName}</td>
							<td>${item.deployTime}</td>
							<td>
							<c:if test="${!item.suspensionState}">
								<a href="###" onclick="disableProcessDef('${item.id}');"><img src="${ctx}/images/table_operation_enable.png" border="0" title="点击禁用"/></a>&nbsp;
                            </c:if>
							<c:if test="${item.suspensionState}">
                            	<a href="###" onclick="enableProcessDef('${item.id}');"><img src="${ctx}/images/table_operation_disable.png" border="0" title="点击启用"/></a>
                            </c:if>
							</td>
					</tr>
					</c:forEach>
</table>          
</body>
</html>