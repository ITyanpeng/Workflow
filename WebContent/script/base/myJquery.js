
//----------------------------------------------自定义alert,confirm开始--------------------------------------
window.alert = function(msg) {
	escmAlert(msg);
};

escmAlert=function(msg){
	if(msg!=null && msg.trim!=''){
		parent.$.messager.alert('提示信息', msg, 'info');
	}
	//有提示信息时关闭进度条
	escmProgressClose();
};

//开启进度条
escmProgress=function(msg){
	if(window.opener) {
		window.opener.top.$.messager.progress({
			 title:'请等待',
			 msg:'正在'+msg+',请稍候...'
		});
	}
	top.$.messager.progress({
		 title:'请等待',
		 msg:'正在'+msg+',请稍候...'
	});
};

//提供用于强制关闭进度条
escmProgressClose=function(){
	top.$.messager.progress('close');
	if(window.opener)
		window.opener.top.$.messager.progress('close');
};

//刷新页面后关闭进度条
escmProgressClose();

//escmConfirm=function(msg){
//	parent.$.messager.confirm('请确认', msg, function(r){
//		return r;
//	});
//};
//----------------------------------------------自定义alert,confirm结束----------------------------------------------

//----------------------------------------------弹出DIV仿模态窗口开始----------------------------------------------
application.window.showDetailDiv = function(url,divWidth,divHeight,openerFunction) {
	lockScreen();	//锁定背景
	divOpen(url,divWidth,divHeight);
	
	//交换X图片
	top.$("#x").hover(
		function(){
			top.$(this).attr("src","../images/Close-2.gif");
		},
		function(){
			top.$(this).attr("src","../images/Close-1.gif");
		}
	);
	
	//关闭DIV窗口
	top.$("#x").click(
		function(){
			top.$("#detailDiv").remove();
			top.$("#divLock").remove();
			//if (openerFunction) {
			//	openerFunction();
			//}
		}
	);
};

//锁定背景屏幕
function lockScreen(){
	var height=window.screen.height;
	var width=window.screen.width;
	if(!top.document.getElementById('divLock')){//判断DIV是否存在
		top.$("body").append("<div id='divLock'></div>");//增加DIV
	}
	top.$("#divLock").height(height);
	top.$("#divLock").width(width);
	top.$("#divLock").css({
		"position":"absolute",
		"display":"block",
		"background-color":"#000000",
		"position":"fixed",
		"z-index":"886",
		"top":"0px",
		"left":"0px",
		"opacity":"0.5"
	});
}

//返回弹出的DIV的坐标
function divOpen(url,divWidth,divHeight){
	var height=window.screen.height;
	var width=window.screen.width;
	
	if(!top.document.getElementById('detailDiv')){
		var detailDiv = "<div id='detailDiv'>";
		detailDiv += "<div id='divTitle'><img src='../images/Close-1.gif' id='x' /></div>";
		detailDiv += "<iframe id='detailFrame' name='detailFrame' width='100%' height='0' src='"+url+"' ></iframe>";
		detailDiv += "</div>";
		top.$("body").append(detailDiv);
	}

	var DivTop=Number(height*0.1);
	var DivLeft=Number(Number(width-divWidth)/2);
	//detailDiv的样式
	top.$("#detailDiv").css({
		"position":"fixed",
		"z-index":"888",
		'top':DivTop,
		'left':DivLeft,
		"opacity":"1.0",//透明度
		"background-color":"#FFFFFF",
		"border":"solid 1px #333333",
		"width":Number(divWidth)
	});
	//divTitle的样式
	top.$("#divTitle").css({
		"height":"20px",
		"width":Number(divWidth-6),
		"line-height":"20px",
		"background-color":"#DEDEDE",
		"padding":"3px 5px 1px 5px",
		"color":"#FFFFFF",
		"font-weight":"bold"
	});
	//detailFrame的样式
	top.$("#detailFrame").css({
		"height":Number(divHeight),
		"overflow":"auto"
	});
	//x的样式
	top.$("#x").css({
		"float":"right",
		"cursor":"pointer"
	});
}

//----------------------------------------------弹出DIV仿模态窗口结束----------------------------------------------

//----------------------------------------------弹出DIV仿模态窗口开始----------------------------------------------
application.window.showImageDiv = function(url) {
	lockScreen();	//锁定背景
	imageDivOpen(url);

	top.$("#divLock").click(function(){
		top.$("#jqImg").remove();
		top.$("#divLock").remove();
	}); 

};

//返回弹出的DIV的坐标
function imageDivOpen(url){
	var winHeight =  window.screen.availHeight ;
	var windowH = $(window.parent.window).height();
	var winWidth = document.documentElement.clientWidth;
	var imgHeight = 0;
	var imgWidth = 0;
	var imgTop = 0;
	var imgLeft = 0;
	var scale = 0.7;
	
	if(top.document.getElementById('jqImg')){
		top.$("#jqImg").remove();
	}
	var img = new Image();
	img.src = url;
	img.onload = function(){
		imgHeight = img.height;
		imgWidth = img.width;
		
		if(Number(imgHeight)>=Number(winHeight*scale) || Number(imgWidth)>=Number(winWidth*scale)){
			if(Number(winHeight*scale)/Number(imgHeight)*Number(imgWidth)>=Number(winWidth*scale)) {
				imgHeight = Number(winWidth*scale)/Number(imgWidth)*Number(imgHeight);
				imgWidth = Number(winWidth*scale);
			} else {
				imgWidth = Number(winHeight*scale)/Number(imgHeight)*Number(imgWidth);
				imgHeight = Number(winHeight*scale);
			}
		}
		

		imgLeft = (winWidth - imgWidth)/2;
		imgTop = (windowH - imgHeight)/2;
		
		 
		var imageDiv = "<img id='jqImg' src='"+url+"' />";
		top.$("body").append(imageDiv);
		
		//alert('imgHeight='+imgHeight+',imgWidth='+imgWidth);
		
		top.$("#jqImg").height(imgHeight);	
		top.$("#jqImg").width(imgWidth);
		
		//imageDiv的样式
		top.$("#jqImg").css({
			"position":"fixed",
			"z-index":"999",
			'top':imgTop,
			'left':imgLeft,
			"margin":"0 auto",
			"opacity":"1",//透明度
			"background-color":"#FFFFFF",
			"border":"solid 1px #333333"
		});

		
		top.$("#jqImg").click(function(){
			top.$("#jqImg").remove();
			top.$("#divLock").remove();
		}); 
	};

}
	

//----------------------------------------------弹出DIV仿模态窗口结束----------------------------------------------



//自定义map
function getMap() {//初始化map_,给map_对象增加方法，使map_像Map    
    var map_ = new Object();    
    map_.put = function(key, value) {    
        map_[key+'_'] = value;    
    };    
    map_.get = function(key) {    
        return map_[key+'_'];    
    };    
    map_.remove = function(key) {    
        delete map_[key+'_'];    
    };    
    map_.keyset = function() {    
        var ret = "";    
        for(var p in map_) {    
            if(typeof p == 'string' && p.substring(p.length-1) == "_") {    
                ret += ",";    
                ret += p.substring(0,p.length-1);    
            }    
        }    
        if(ret == "") {    
            return ret.split(",");    
        } else {    
            return ret.substring(1).split(",");    
        }    
    };
	map_.size = function() {
		var i=0;
        for(var p in map_) {
        	if(typeof p == 'string' && p.substring(p.length-1) == "_") {  
        		i++;
        	}
        }
        return i;
	};
    return map_;    
}


function ForDight(Dight, How) {
	Dight = Math.round(Dight * Math.pow(10, How)) / Math.pow(10, How);
	return Dight;
};

//除法函数，用来得到精确的除法结果
//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
//调用：accDiv(arg1,arg2)
//返回值：arg1除以arg2的精确结果
function accDiv(arg1,arg2){ 
	var t1=0,t2=0,r1,r2; 
	try{
		t1=arg1.toString().split(".")[1].length
	}
	catch(e){} 
	try{
		t2=arg2.toString().split(".")[1].length
	}
	catch(e){} 
	with(Math){ 
		r1=Number(arg1.toString().replace(".",""));
		r2=Number(arg2.toString().replace(".",""));
		return (r1/r2)*pow(10,t2-t1); 
	} 
};

//给Number类型增加一个div方法，调用起来更加方便。
Number.prototype.div = function (arg){ 
	return accDiv(this, arg); 
};

//乘法函数，用来得到精确的乘法结果
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
//调用：accMul(arg1,arg2)
//返回值：arg1乘以arg2的精确结果
function accMul(arg1,arg2){ 
	var m=0,s1=arg1.toString(),s2=arg2.toString(); 
	try{
		m+=s1.split(".")[1].length;
	}catch(e){} 
	try{
		m+=s2.split(".")[1].length;
	}catch(e){
	} 
	return 
		Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
};

//给Number类型增加一个mul方法，调用起来更加方便。
Number.prototype.mul = function (arg){ 
	return accMul(arg, this); 
};

//加法函数，用来得到精确的加法结果
//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
//调用：accAdd(arg1,arg2)
//返回值：arg1加上arg2的精确结果
function accAdd(arg1,arg2){ 
	var r1,r2,m; 
	try{
		r1=arg1.toString().split(".")[1].length;
	}catch(e){
		r1=0;
	}
  	try{
  		r2=arg2.toString().split(".")[1].length;
  	}catch(e){
  		r2=0;
  	} 
  	m=Math.pow(10,Math.max(r1,r2)); 
  	return (arg1*m+arg2*m)/m;
};

//给Number类型增加一个add方法，调用起来更加方便。
Number.prototype.add = function (arg){ 
	return accAdd(arg,this); 
};

//浮点数减法运算
function Subtr(arg1,arg2){
	var r1,r2,m,n;
	try{
		r1=arg1.toString().split(".")[1].length;
	}catch(e){
		r1=0;
	}
	try{
		r2=arg2.toString().split(".")[1].length;
	}catch(e){
		r2=0;
	}
	m=Math.pow(10,Math.max(r1,r2));
	//last modify by deeka
	//动态控制精度长度
	n=(r1>=r2)?r1:r2;
	return ((arg1*m-arg2*m)/m).toFixed(n);
};

//给Number类型增加一个dec方法，调用起来更加方便。 
Number.prototype.dec = function (arg){ 
	return Subtr(this,arg);
};

String.prototype.endWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substring(this.length-s.length)==s)
		return true;
	else
		return false;
	return true;
};

String.prototype.startWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substr(0,s.length)==s)
	    return true;
	else
	    return false;
	return true;
};

function setStart() {
	if(document.getElementById('paging.start'))
		document.getElementById('paging.start').value='0';
}