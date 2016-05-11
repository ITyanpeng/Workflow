<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="title" value="基础项目（To Do）"></c:set>
<c:set var="conTel" value=""></c:set>
<!-- DEBUG INFO START-->
<!-- <%=request.getHeader("Referer") %> -->
<!-- ${pageContext.request.servletPath} -->
<!-- DEBUG INFO END-->
<!-- style sheet files -->

<link href="${ctx}/css/base/css.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/themes/icon.css" />
<link href="${ctx}/css/ztree/zTreeStyle/zTreeStyle.css" rel="stylesheet" type="text/css" />

<!-- JavaScript files -->
<script type="text/javascript" src="${ctx}/script/base/jquery-1.7.1.min.js"></script>

<!-- 扩展EASYUI校验功能 
<script type="text/javascript">
$.extend($.fn.validatebox.defaults.rules, {
	minLength: {
		validator: function(value, param){ 
			return value.length >= param[0]; 
		}, 
		message: '最少输入{0}位数.'
	},
	maxLength: {
		validator: function(value, param){ 
			return value.length <= param[0]; 
		}, 
		message: '最多输入{0}位数.'
	},
    CHS: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5]+$/.test(value);
        },
        message: '请输入汉字.'
    },
    ZIP: {
        validator: function (value, param) {
            return /^[0-9]\d{5}$/.test(value);
        },
        message: '邮政编码为6位数字.'
    },
    QQ: {
        validator: function (value, param) {
            return /^[1-9]\d{4,10}$/.test(value);
        },
        message: 'QQ号码不正确.'
    },
    mobile: {
        validator: function (value, param) {
            return /^13\d{9}$/g.test(value)||(/^15\d{9}$/g.test(value)) ||(/^18\d{9}$/g.test(value));
        },
        message: '手机号码不正确.'
    },
    ctccMobile: {
        validator: function (value, param) {
            return /^133\d{8}$/g.test(value)||(/^153\d{8}$/g.test(value)) ||(/^180\d{8}$/g.test(value))||(/^189\d{8}$/g.test(value))||(/^181\d{8}$/g.test(value))||(/^177\d{8}$/g.test(value));
        },
        message: '请填写电信手机号码.'
    },
    number: {
        validator: function (value, param) {
            return /^\d+$/.test(value);
        },
        message: '请输入数字.'
    },
    alpha:{   
        validator:function(value,param){   
            if (value){   
                return /^[a-zA-Z\u00A1-\uFFFF]*$/.test(value);   
            } else {   
                return true;   
            }   
        },   
        message:'只能输入字母.'  
    },   
    alphanum:{   
        validator:function(value,param){   
            if (value){   
                return /^([a-zA-Z\u00A1-\uFFFF0-9])*$/.test(value);   
            } else {   
                return true;   
            }   
        },   
        message:'只能输入字母和数字.'  
    },   
    positive_int:{   
        validator:function(value,param){   
            if (value){   
                return /^[0-9]*[1-9][0-9]*$/.test(value);   
            } else {   
                return true;   
            }   
        },   
        message:'只能输入正整数.'  
    },
    process_rate:{   
        validator:function(value,param){   
            if (value){   
                if(/^-?[1-9]\d*$/.test(value)){
                	if(-100<=Number(value) && Number(value)<=100)
              			return true;
                  	else return false;
                }else{
                	return false;
                }
                
            } else {   
                return true;   
            }   
        },   
        message:'只能输入-100到100之间的整数.'  
    },
    bizLicence:{   
        validator:function(value,param){   
    		if(value){
        	    return /^[0-9]{12,50}$/.test(value);   
            } else {   
                return true;   
            }   
        },   
        message:'营业执照注册号格式错误,长度12~50位，只能有数字.'  
    },
    setupCode:{   
        validator:function(value,param){   
    		if(value){
            	return /^[0-9A-Z]{8}-[0-9a-zA-Z]{1}$/.test(value);   
            } else {   
                return true;   
        	}   
    	},   
    	message:'组织机构代码格式错误，例如：70074380-9、70074380-X.'  
	},
	idcardV:{// 验证身份证 
		validator: function (value, param) {
	        return idCard(value);
	    },
	    message:'请输入正确的身份证号码'
	}, 
    stateTaxNo:{   
        validator:function(value,param){   
    		if(value){
            	return /^[A-Za-z0-9]{1,20}$/.test(value);   
            } else {   
                return true;   
        	}   
        },   
        message:'税务登记号码格式错误,应为字母和数字的组合,且长度不超过20位.'  
    },
    notZero:{   
        validator:function(value,param){   
    		if(value){
            	return value!='0';   
            } else {   
                return true;   
        	}   
        },   
        message:'不能为零.'  
    },
    taxRate: {
       	validator: function(value, param) {
         	if(0<=Number(value) && Number(value)<=100)
      			return true;
          	else return false;
       	},
       	message: '请输入不大于100且不小于0的数字.'
    },
    bankAccount: {
    	validator: function(value, param) {
    		return /^[A-Z,a-z,0-9]{1,10}-*[A-Z,a-z,0-9]{1,30}$|[A-Z,a-z,0-9]{1,32}$/.test(value);
    	},
       	message: '账号只能是字母或数字.'
    },
    email: {
    	validator: function(value, param) {
			return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(value);
   		},
   		message: '邮箱格式不正确.'
    },
    telephone: {
    	validator: function(value, param) {
    		if(/^[+]{0,1}(\d){1,4}[ ]?([-]?((\d)|[ ]){1,12})+$/.test(value) && 7<value.length && value.length<16)
        		return true;
    		else return false;
   		},
   		message: '电话格式不正确.'
    },
    fax: {
    	validator: function(value, param) {
		if(/^[+]{0,1}(\d){1,4}[ ]?([-]?((\d)|[ ]){6,12})+$/.test(value) && value.length<16)
    		return true;
		else return false;
		},
		message: '传真格式不正确.'
    },
    equalTo: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '两次输入的字符不一致.'
    },
    money: {
        	validator: function(value, param) {
             var p =/^[1-9](\d+(\.\d{1,2})?)?$/; 
             var p1=/^[0-9](\.\d{1,2})?$/;
             if(p.test(value) || p1.test(value))
                	return true;
             else
                 return false;
    		},
    		message: '格式不正确.'
        },

   moneynew: {
       	validator: function(value, param) {
            var p =/^\-?(0|[1-9]\d{0,})(\.\d{1,2})?$/; 
            
            if(p.test(value))
               	return true;
            else
                return false;
   		},
   		message: '格式不正确.'
        },
    moneyfour: {
     	validator: function(value, param) {
          var p =/^[1-9](\d+(\.\d{1,4})?)?$/; 
         var p1=/^[0-9](\.\d{1,4})?$/;
          if(p.test(value) || p1.test(value))
             	return true;
          else
              return false;
 		},
 		message: '格式不正确.'
     },
   moneysix: {
    	validator: function(value, param) {
         var p =/^[1-9](\d+(\.\d{1,6})?)?$/; 
        var p1=/^[0-9](\.\d{1,6})?$/;
         if(p.test(value) || p1.test(value))
            	return true;
         else
             return false;
		},
		message: '格式不正确.'
    },
   invoice:{   
       validator:function(value,param){   
           if (value){   
               return /^[,\uFFFF]*$/.test(value);   
           } else {   
               return true;   
           }   
       },   
       message:'只能输入数字和逗号.'  
   },
   roleCode:{   
       validator:function(value,param){   
               if (value){   
            	   return value.match('ROLE'); 
               } else {   
                   return true;   
               }     
       },   
       message:'需要输入ROLE开头的角色编码，如ROLE_PEOPLE.'  
   }
        
});

$.extend($.fn.validatebox.methods, {    
    remove: function(jq, newposition){    
        return jq.each(function(){    
            $(this).removeClass("validatebox-text validatebox-invalid").unbind('focus').unbind('blur');  
        });    
    },  
    reduce: function(jq, newposition){    
        return jq.each(function(){    
           var opt = $(this).data().validatebox.options;  
           $(this).addClass("validatebox-text").validatebox(opt);  
        });    
    }     
});  


function formatDatebox(value) {
	if(value==undefined || value==null || value=='') {
		return "";
	} else if(value.indexOf('-')>-1 && value.indexOf('NaN')==-1){
		return value;
	} else {
		var date = new Date(Date.parse(value));
		var yyyy = date.getFullYear();
		var MM = (date.getMonth()+1).toString().length==1?'0'+(date.getMonth()+1):(date.getMonth()+1);
		var dd = date.getDate().toString().length==1?'0'+date.getDate():date.getDate();
		return yyyy+"-"+MM+"-"+dd; 
	}
}

$.extend($.fn.datagrid.defaults.editors, {
    datebox: {
    	init : function(container, options) {
   	      var input = $('<input type="text">').appendTo(container);
   	      input.datebox(options);
   	      return input;
   	    },
   	    destroy : function(target) {
   	      $(target).datebox('destroy');
   	    },
   	    getValue : function(target) {
   	      return $(target).datebox('getValue');//获得旧值
   	    },
   	    setValue : function(target, value) {
   	      console.info(formatDatebox(value));
   	      $(target).datebox('setValue', formatDatebox(value));//设置新值的日期格式
   	    },
   	    resize : function(target, width) {
   	      $(target).datebox('resize', width);
   	    }
    }
});

var idCard = function (value) {
    if (value.length == 18 && 18 != value.length) return false;
    var number = value.toLowerCase();
    var d, sum = 0, v = '10x98765432', w = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2], a = '11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91';
    var re = number.match(/^(\d{2})\d{4}(((\d{2})(\d{2})(\d{2})(\d{3}))|((\d{4})(\d{2})(\d{2})(\d{3}[x\d])))$/);
    if (re == null || a.indexOf(re[1]) < 0) return false;
    if (re[2].length == 9) {
        number = number.substr(0, 6) + '19' + number.substr(6);
        d = ['19' + re[4], re[5], re[6]].join('-');
    } else d = [re[9], re[10], re[11]].join('-');
    if (!isDateTime.call(d, 'yyyy-MM-dd')) return false;
    for (var i = 0; i < 17; i++) sum += number.charAt(i) * w[i];
    return (re[2].length == 9 || number.charAt(17) == v.charAt(sum % 11));
};

var isDateTime = function (format, reObj) {
    format = format || 'yyyy-MM-dd';
    var input = this, o = {}, d = new Date();
    var f1 = format.split(/[^a-z]+/gi), f2 = input.split(/\D+/g), f3 = format.split(/[a-z]+/gi), f4 = input.split(/\d+/g);
    var len = f1.length, len1 = f3.length;
    if (len != f2.length || len1 != f4.length) return false;
    for (var i = 0; i < len1; i++) if (f3[i] != f4[i]) return false;
    for (var i = 0; i < len; i++) o[f1[i]] = f2[i];
    o.yyyy = s(o.yyyy, o.yy, d.getFullYear(), 9999, 4);
    o.MM = s(o.MM, o.M, d.getMonth() + 1, 12);
    o.dd = s(o.dd, o.d, d.getDate(), 31);
    o.hh = s(o.hh, o.h, d.getHours(), 24);
    o.mm = s(o.mm, o.m, d.getMinutes());
    o.ss = s(o.ss, o.s, d.getSeconds());
    o.ms = s(o.ms, o.ms, d.getMilliseconds(), 999, 3);
    if (o.yyyy + o.MM + o.dd + o.hh + o.mm + o.ss + o.ms < 0) return false;
    if (o.yyyy < 100) o.yyyy += (o.yyyy > 30 ? 1900 : 2000);
    d = new Date(o.yyyy, o.MM - 1, o.dd, o.hh, o.mm, o.ss, o.ms);
    var reVal = d.getFullYear() == o.yyyy && d.getMonth() + 1 == o.MM && d.getDate() == o.dd && d.getHours() == o.hh && d.getMinutes() == o.mm && d.getSeconds() == o.ss && d.getMilliseconds() == o.ms;
    return reVal && reObj ? d : reVal;
    function s(s1, s2, s3, s4, s5) {
        s4 = s4 || 60, s5 = s5 || 2;
        var reVal = s3;
        if (s1 != undefined && s1 != '' || !isNaN(s1)) reVal = s1 * 1;
        if (s2 != undefined && s2 != '' && !isNaN(s2)) reVal = s2 * 1;
        return (reVal == s1 && s1.length != s5 || reVal > s4) ? -10000 : reVal;
    }
};
//格式化日期时间
Date.prototype.format = function(format) {
    var o = {
      "M+": this.getMonth() + 1, // month
      "d+": this.getDate(), // day
      "h+": this.getHours(), // hour
      "m+": this.getMinutes(), // minute
      "s+": this.getSeconds(), // second
      "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
      "S": this.getMilliseconds()
      // millisecond
    }
    if (/(y+)/.test(format))
      format = format.replace(RegExp.$1, (this.getFullYear() + "")
        .substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    return format;
  };

  var UPPER_NUM = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
  var MAX_UNIT = ['元', '拾', '佰', '仟', '万', '拾万', '佰万', '仟万','亿'];
  var MIN_UNIT = ['角', '分'];
  function numToCny(n){     
	        var _html = '', integer = n, fract;
	        if(n.indexOf('.') != -1) {
	             integer = n.split('.')[0];fract = n.split('.')[1];
	        }
	        for(var i = 0, j = integer.length, len = j; i < len; i++) {
	            var _index = integer.charAt(i); --j;
	            _html += UPPER_NUM[_index] + (_index > 0 ? MAX_UNIT[j] : '');
	        }
	        _html = _html.replace(/零{2,}/g,'零').replace(/零$/,'元');
	        if(fract !== undefined) {
	            for(var i = 0, j = 0, len = fract.length; i < len; i++) {
	                _html += UPPER_NUM[fract.charAt(i)] + MIN_UNIT[j++];
	            }
	        } else {
	            _html += '整';
	        }
	        return _html;
  }
  
  function fmoney(s, n) //s:传入的float数字 ，n:希望返回小数点几位 
  { 
  n = n > 0 && n <= 20 ? n : 2; 
  s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + ""; 
  var l = s.split(".")[0].split("").reverse(), 
  r = s.split(".")[1]; 
  t = ""; 
  for(i = 0; i < l.length; i ++ ) 
  { 
  t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : ""); 
  } 
  return t.split("").reverse().join("") + "." + r; 
  } 
</script> -->