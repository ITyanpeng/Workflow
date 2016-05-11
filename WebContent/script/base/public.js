$(document).ready(function(){
	/*表格中复选框垂直居中*/
	var checkbox_height = $('.table_list input[type=checkbox]').height();
	var checkbox_th_middle = (31-checkbox_height)/2;
	$('.table_list th b input').css('margin-top',checkbox_th_middle);
	$('.table_list').each(function() {
        $(this).find('th:last b').css('border-right','solid 0px #b9dcef');
    });
	$('.table_list tr').each(function() {
        $(this).find('td:last').css('border-right','solid 0px #b9dcef')
    });
	$('.table_list tr').not(":first").hover(function(){
		$(this).css('background-color','#f6fbff');
	},
	function(){
		$(this).css('background-color','#fff');
	}
	);
	
	
	
	/*退出按钮交互效果*/
	$('#general_head .sign_out').hover(function(){
		$(this).find('img').animate({paddingLeft:"10px"});
	},
	function(){
		$(this).find('img').animate({paddingLeft:"0px"});
	}
	);
	
	/*导航hover后的效果*/
	$('#general_nav ul>li>a').hover(function(){
		//判断元素是否正处于动画状态
		if($(this).is(":animated")){    
		}else{
			$(this).animate({paddingLeft:"10px"});
		}
	},
	function(){
		$(this).animate({paddingLeft:"0px"});
	}
	);
	
	/*设置左侧导航高度*/
	var nav_height = document.documentElement.clientHeight;
	$('#general_nav').css('height',nav_height-75);
	
	/*二三级导航呈现*/
	$('#general_nav ul li .second_nav').hide();
	$('#general_nav ul li .second_nav .second_nav_content div').each(function(){
		$(this).find('a:first').css('background-image','none');
	});
	
	
	
	
	var timer_data;
	function startTime(){
		timer_data = window.setInterval(function(){ 
			second_hide(); 
		}, 500);
	}
	
	function stopTime(){
		if(timer_data){
			clearInterval(timer_data);
		}
	}
	function second_hide(){
		$('.second_nav').hide('normal');
	};
	$('#general_nav ul li>a').hover(function(){
		$('#general_nav ul li .second_nav').hide();/*此处和下面都必须同时加动画效果或不加*/
		$(this).next('.second_nav').show();
		stopTime();
	},function(){
		startTime();
	});
	
	$('.second_nav').hover(function(){
		stopTime();
	},function(){
		$('.lan_float').hide('normal');
		startTime();
	});
	
	$('.float_close_icon').live("click",function(){
		float_close();
	});
	
	/*复选框全选*/
	$('.all_change').click(function(){
		if($(this).attr('checked') == 'checked'){
			$(this).parent().parent().parent().parent().find('tr').not(':first').find('.checkbox').attr('checked','checked');
		}else{
			$(this).parent().parent().parent().parent().find('tr').not(':first').find('.checkbox').removeAttr('checked');
		}
	});
	
	$('.checkbox').click(function(){
		if($(this).attr('checked') == 'checked'){
		}else{
			$('.all_change').removeAttr('checked');
		}
	});
	
	/*首页底部伪下拉框*/
	
	$('.drop_down_box_content .select_display').click(function(){
		$('.drop_down_box_content ul').hide();
		$(this).next('ul').slideDown();
	});
	$('.drop_down_box_content ul li').click(function(){
		$(this).parent().hide();
		var select_now_data = $(this).text();
		$(this).parent().prev('.select_display').text(select_now_data);
	});
	$(document).click(function(event){ 
		if($(event.target).is(".drop_down_box_content .select_display")||$(event.target).is(".drop_down_box_content ul")){
		}else{
			$('.drop_down_box_content').each(function() {
				$(this).find('ul').hide();
			});
		}
	});
	
	$('.drop_down_box_content ul li').hover(function(){
		$(this).css('background-color','#eaf5ff');
	},
	function(){
		$(this).css('background-color','transparent');
	}
	);
	
	/*tab切换*/
	$('.tab_change_div').hide().eq(0).show();
	$('.general_table_title_iframe .tab_change ul li').click(function(){
		$('.general_table_title_iframe .tab_change ul li').removeClass('checked');
		$(this).addClass('checked');
		var tab_data = $(this).index();
		$('.tab_change_div').hide().eq(tab_data).show();
		iframe_auto();
	});
	
	/*2015-04-02新增邮件搜索框*/
	$('.email_search .text_box').focus(function(){
		if($(this).val() == "--请输入关键字--"){
			$(this).val('');
		}
	});
	$('.email_search .text_box').blur(function(){
		if($(this).val() == "--请输入关键字--" || $(this).val() == ""){
			$(this).val('--请输入关键字--');
		}
	});
	
	/*新增右下角浮动窗公告代码*/
	$('.float_notice').slideDown();
	$('.float_notice .float_notice_close').click(function(){
		$('.float_notice').slideUp();
	});
	
	/*新增表格th样式添加*/
	$('.table_list').each(function(){
		$(this).find('th:first').addClass('left_title_bg');
		$(this).find('th:last').addClass('right_title_bg');
	});
	
});

$(window).load(function(){
	$('.table_list td').each(function(){
		var duration_num = $(this).find('.progress_content b').width();
		if(duration_num == '122'){
			$(this).find('.progress_content b').css('background-image','url(images/table_progress_normal.png)');
		}
	});
	
	/*首页底部伪下拉框*/
	$('.drop_down_box_content').each(function() {
        var select_data = $(this).find('ul li:first').text();
		$(this).find('.select_display').text(select_data);
		$(this).find('ul li:first').css('border-radius','4px 4px 0px 0px');
		$(this).find('ul li:last').css('border-radius','0px 0px 5px 5px');
    });
	
});


/*浮动窗口打开和关闭*/
function float_open(width_num,height_num,iframe_src,iframe_title){
	var width_determine = !width_num ? '666' : width_num; 
	var height_determine = !height_num ? '470' : height_num; 
	var window_width = document.documentElement.clientWidth;
	var window_height = document.documentElement.clientHeight;
	if(iframe_src == null){
		iframe_src = "iframe.html";
	}
	
	var float_data = '<div class="float_window_bg"></div>'
					+'<div class="float_window">'
						+'<p class="float_title">'
							+'<span class="float_title_text">'+iframe_title+'</span>'
							+'<span class="float_close_icon"><img src="../images/base/float_close_icon.png"/></span>'
						+'</p>'
    					+'<div class="float_window_detail">'
							+'<iframe src="'
							+ iframe_src 
							+ '"  id="float_iframe" style="border:0px;overflow:auto;" width="'
							+ width_determine
							+'" height="'
							+ height_determine
							+'">'
    						/*+'<a href="#" class="btn_style1 btn_general">保存<img src="images/btn_icon1.png" border="0"/></a>'*/
    					+'</div>'
					+'</div>';
	$('body').append(float_data);
	float_left_num = (window_width-parseInt(width_determine))/2;
	float_top_num = (window_height-parseInt(height_determine))/2;
	$('.float_window_detail .btn_general').css({'right':'30px','bottom':'20px'});
	$('.float_window_detail').css('height',height_determine-50);
	$('.float_window').css({'width':width_determine,'height':height_determine,'left':float_left_num,top:float_top_num});
	$('.float_window_bg,.float_window').animate({opacity: 'show'});
}
function float_close(){
	$('.float_window_bg,.float_window').animate({opacity: 'hide'},function(){
		$(this).remove();
	});
}
/*2015.03.17新增代码*/
function float_btn_close(){
	$(window.parent.document).find('.float_window_bg,.float_window').animate({opacity: 'hide'},function(){
		$(this).remove();
	});
}
/*function float_iframe_auto(){
	var float_window_width = document.body.clientWidth + "px";
	var float_window_height = document.body.clientHeight + "px";
	window.parent.document.getElementById("float_iframe").style.width = float_window_width;
	window.parent.document.getElementById("float_iframe").style.height = float_window_height;
}
*/




