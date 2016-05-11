<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
response.setHeader("Pragma" ,"No-cache"); 
response.setHeader("Cache-Control","no-cache");
response.setHeader("Expires","0");
%> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@ include file="/common/taglibs.jsp"%>
<title>系统异常</title>
</head>
<c:set  var="ee"  value="${exception}"/>
<jsp:useBean id="ee" type="java.lang.Exception" />

<body>


 <div id="general_detail">
 
 	<div class="unified_module">
 
  		<div class="module_title_icon">
        	<p class="left_icon"><img src="${ctx }/images/base/unified_module_icon_search.png" border="0"/></p>
            <p class="middle_text_content">错误</p>
            <img src="${ctx }/images/base/unified_module_right_icon_img.png" border="0"/>
        </div>
		<div class="module_content_detail">      
			<ul class="information_search">  
				<li class="textarea_content">
                	<p>报错信息：</p>
                    <textarea   style="height:800px;" class="multi_line_text" id="remark" name="remark"><%ee.printStackTrace(new java.io.PrintWriter(out)); %></textarea>
            	</li>
				<div class="clear"></div>
      		</ul>
		</div>
	</div>
</div>
<script  type="text/javascript">
<c:if test="${messages!=null&&messages!=''}" >
escmAlert("${messages}");
</c:if>
</script>
</body>
</html>