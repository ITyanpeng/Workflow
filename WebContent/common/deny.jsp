<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title>由于安全控制，拒绝访问！</title>
</head>

<body  style="height:100%;">
<table width="100%" height="350" border="0" align="center" cellpadding="5" cellspacing="5" bgcolor="#DFE0E3">
  <tr>
    <td height="217" align="center" bgcolor="#F3F3F3"><table width="80%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td width="35%" rowspan="2" align="center"><img src="${ctx}/images/images27.gif" width="120" height="250" title="由于安全控制，拒绝访问！"/></td>
        <td width="65%" height="168" align="left"><img src="${ctx}/images/images28.gif" width="402" height="110" /></td>
      </tr>
      <tr>
        <td align="left">
        	<a href="javascript:history.go(-1)"><img src="${ctx}/images/images29.gif" width="112" height="32" border="0" /></a> 
        	<a href="javascript:window.close()"><img src="${ctx}/images/images30.gif" width="112" height="32" border="0" /></a>
        </td>
      </tr>
    </table></td>
  </tr>
</table>

</body>
</html>
<%@ include file="/common/commonFootScripts.jsp"%>