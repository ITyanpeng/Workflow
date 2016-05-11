<script type="text/javascript" defer="defer">
$(document).ready(function(){
	iframe_auto();
	$('.secondpage_content_all .click_expansion').click(function(){
		iframe_auto();
	});
	$('.secondpage_content_all .click_expansion_1').click(function(){
		iframe_auto();
	});
	$('.table_option_third ul li').click(function(){
		iframe_auto();
	});
	$('.panel-tool-close').click(function(){
		iframe_auto();
	});
	$('.table_data tr td a').click(function(){
		iframe_auto();
	});
	
});
function iframe_auto(){
	var newHeight = document.body.clientHeight + 10 + "px";
	if(window.parent.document.getElementById("mainSubframe")){
		window.parent.document.getElementById("mainSubframe").style.height = newHeight;
	}
}
</script>