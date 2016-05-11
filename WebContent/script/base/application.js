var application = new Object();
application.constant = new Object();
application.window = new Object();
application.Util = new Object();
//定义查询列表最大显示的行数
application.constant.MAXROWSIZE = 10;
//分页时操作开始数字的域名
application.constant.PAGINATIONBEGINPARAM = 'paging.start';
//分页时操作最大显示行数的域名
application.constant.PAGINATIONLIMITPARAM = 'paging.pageSize';
//分页时获取总记录数的域名
application.constant.PAGINATIONCOUNTPARAM = 'paging.total';
/**
 * 将Grid的值加入鼠标提示函数
 * strTitleValue: 提示值
 * value: 显示的单元格值
 */
function sccl_AddTitleCellValue(strTitleValue, value) {
	if (arguments.length == 1)
	{
		return strTitleValue;
		return '<span title="' + strTitleValue + '">' + strTitleValue + '</span>';
	}
	else 
	{
		return value;
		return '<span title="' + strTitleValue + '">' + value + '</span>';
	}
}
//弹出一个非模态窗口
/*
 * openFunciton:子窗口关闭的时候会调用的父窗口的方法
 * 只有一个参数：obj，具体的内容由子窗口自己定义
 */
application.window.OpenWindowFn = function(url, width, height, openerFunction, left, top) {
	var temp = "channelmode=no,location=no,menubar=no,toolbar=no,directories=no,scrollbars=yes,resizable=yes";
	if (width) {
		temp += ',width=' + width;
	} else {
		width = 0;
	}
	if (height) {
		temp += ',height=' + height;
	} else {
		height = 0;
	}
	if (left) {
		temp += ',left=' + left;
	} else {
		temp += ',left='
				+ Math.round((window.screen.width - parseInt(width)) / 2);
	}
	if (top) {
		temp += ',top=' + top;
	} else {
		temp += ',top='
				+ Math.round((window.screen.height - parseInt(height)) / 2);
	}
	var newwin = window.open(url, '_blank', temp);
	
	if (openerFunction) {
		try{
		newwin.opener.openCallback = openerFunction;
	
		}catch(e){
		}
	}
	newwin.focus();
	return newwin;
};

//打开模式窗口
//dialogHeight:   iHeight   设置对话框窗口的高度。 
//dialogWidth:   iWidth   设置对话框窗口的宽度。   　　 

//dialogLeft:   iXPos   设置对话框窗口相对于桌面左上角的left位置。 
//dialogTop:   iYPos   设置对话框窗口相对于桌面左上角的top位置。 
//center:   {yes   |   no   |   1   |   0   }   指定是否将对话框在桌面上居中，默认值是“yes”。 
//help:   {yes   |   no   |   1   |   0   }   指定对话框窗口中是否显示上下文敏感的帮助图标。默认值是“yes”。   　　 
//resizable:   {yes   |   no   |   1   |   0   }   指定是否对话框窗口大小可变。默认值是“no”。 
//status:   {yes   |   no   |   1   |   0   }   指定对话框窗口是否显示状态栏。对于非模式对话框窗口，默认值是“yes”；对于模式对话框窗口，默认值是   "no”。
application.window.showModelessDialogWindowFn = function(url,width, height,parentWin,openerFunction, left, top) {
	 
	 var temp = "";
	    if (width) {
	        temp += "dialogWidth:" + width + "px;";
	    } else {
	    	 temp += "dialogWidth:0px;";
	    }
	    if (height) {
	        temp += ",dialogHeight:" + height + "px;";
	    } else {
	    	 temp += ",dialogHeight:0px;";
	    }
	    if (left) {
	        temp += ",dialogLeft:" + left + "px;";
	    } else {
	        temp += ',dialogLeft:' + Math.round((window.screen.width - parseInt(width)) / 2) + "px;";
	    }
	    if (top) {
	        temp += ",dialogTop:" + top + "px;";
	    } else {
	        temp += ',dialogTop:' + Math.round((window.screen.height - parseInt(height)) / 2) + "px;";
	    }
	    temp+="center:yes;help:yes;resizable:yes;status:yes";

  var returnValue = window.showModalDialog(url,"win1", temp);
  if (openerFunction) {
	  try{
	  openerFunction.call(this,returnValue);
	  }catch(e){
		  //escmAlert(e);
	  }
  }
    //parentWin.focus();
  return parentWin;
};

/*
 * 将Store中的record数据组装为json格式的字符串
 * date类型的必须要在fieldName要有Date的后缀
 * fields: 可以通过Store.fields.items得到，或者定义的record.fields.items得到
 * records： 数据集
 */
application.Util.toJSONStringFromStore = function(fields, records) {
	if (records.length == 0 || fields.length == 0) return "[]";
	var jsonString = "";
	for (var j = 0, rlen = records.length; j < rlen; j++) {
		var json = "";
		for (var i = 0, len = fields.length; i < len; i++) {
			var field = fields[i].name;
			var mapping = fields[i].mapping || field;
			var type = fields[i].type;
			
			if (type == "int" || type == "bool") {
				json = json == "" ? mapping + ":" + records[j].get(field) : json + "," + mapping + ":" + records[j].get(field);
			}
			else {
				var temp = records[j].get(field);
				if (mapping.indexOf('Date') > -1) {
					temp = Ext.util.Format.date(temp,"Y-m-d");
					if(temp == "NaN-NaN-NaN"){
						temp = Ext.util.Format.date(new Date(records[j].get(field).time),"Y-m-d");
					}
				}
				json = json == "" ? mapping + ":'" + temp + "'" : json + "," + mapping + ":'" + temp + "'";
			}
		}
		json = "{" + json + "}";
		jsonString = jsonString == "" ? json : jsonString + "," + json;
	}
	return "[" + jsonString + "]";
};

String.prototype.trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 
};


//jqgrid 默认属性
var GRID_COMMON_PARAMS = {	
	//	caption: "",//grid的title行，为空串直接不显示title条
		loadtext:"",//grid的查询数据时的文字提示
		altRows:true,//是否允许变换的行
	    prmNames:{page:"paging.currentPage",rows:"paging.pageSize"},//自定义grid请求的参数名（重命名）
	    page:1,//默认起始页	
	    refreshstate: 'current',
	    rownumbers:true,
	    toppager: true,
	    height:'auto',
//	    height:"100%",//grid的高度（设置100%标识自适应不出现滚动条）
	    width:"100%",//grid的宽度（设置100%标识自适应不出现滚动条）
	    autowidth:true,//是否根据父元素自动调整宽度
	    pagerpos:'right',//分页在pager中的显示位置
	    recordpos:'center',//分页记录信息在pager中的显示位置
	    rowNum: 10,  //设置表格可以显示的记录数
	    rowList: [10,20,50,100],  
	    pginput:true,//是否提供文本框录入页码
	    viewrecords: true,  //是否显示记录信息
	    recordtext: " {0} - {1}共 {2} 条",//记录信息显示格式
	    sortable : false //默认不排序
//	    shrinkToFit: false
	};