<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/styles_Epaper.css" />
<script>
$(function(){
	prepare();
	$("#cancelEpaper").click(function() {
		$.ajax({
			type : "POST",
			url : "registerEpaper.do",
			data : {
				action : "cancelRegister",
				email : $("#whose_email").val(),
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				if (json_obj.strMessage == "cancelSuccess"){
					$("#paper_content").html("您已成功取消訂閱電子報。<br><br>感謝您一直以來對的 <b>\"SBI每日產業重點新聞電子報\"</b>支持<br>並期待能再有機會為您服務。");
				}else if (json_obj.strMessage == "haveCancelBefore" || json_obj.strMessage == "haveNeverRegister"){
					$("#paper_content").html("您目前並沒有訂閱<b>\"SBI每日產業重點新聞電子報\"</b>，故無法取消訂閱，若有疑問	請洽系統管理員。");
					setTimeout(function(){ 
						window.location.href = "./login.jsp";
					}, 20*1000);
				}else{
					$("#paper_content").html("取消訂閱電子報失敗，請洽系統管理員。");
					setTimeout(function(){ 
						window.location.href = "./login.jsp";
					}, 20*1000);
				}
			}
		});
	});
	$("#reset").click(function(){
		window.location.href = "./login.jsp";
	});
	
});
</script>
<div class="content-wrap">
	<h2 class="page-title">電子報</h2>
	<div class="search-result-wrap">
		<div class='paper_container'>
			<div class='paper_container_title'>取消訂閱電子報</div>
			<form id='paper_content' style=''>
				<table class='form-table'>
					<tr>
						<td>親愛的用戶 <b id='whose_email'></b> 您好：</td>
					</tr>
					<tr>
						<td>
							　　此頁面為取消訂閱電子報 <b>"SBI每日產業重點新聞電子報"</b> ，若您並未意圖取消訂閱請忽略。
							對於您不再訂閱我們的電子報我們深感遺憾，若確定不再訂閱請點擊取消訂閱。<br><br>
							　　再次提醒您，若您確定取消訂閱，表示您將<font class='emphasize-red'>永遠</font>不會再收到
							本 <font class='emphasize-red'>'SBI每日產業重點新聞電子報'</font>，請審慎考慮。 
						</td>
					</tr>
					<tr>
						<td colspan='1' class='center_content'>
							<a id='cancelEpaper' class='btn btn-exec' >取消訂閱</a> 
							<a id='reset' class='btn btn-gray' >回到首頁</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</div>
<script>
function prepare(){
 	var email = window.location.href.split("?")[1];
	if(email!=null){
		try {
			$.ajax({
				type : "POST",
				url : "registerEpaper.do",
				data : {
					action : "selectWithKeyword",
					keyword : atob(email)
				},
				success : function(result) {
					console.log(result);
					var json_obj = JSON.parse(result);
					// var json_obj = $.parseJSON(result);
					console.log(json_obj);
					if(json_obj.list.length>0){
						$("#whose_email").html(json_obj.list[0].order_name);
						$("#whose_email").val(json_obj.list[0].e_mail);
					}else{
						//資料庫沒有這個email也跳回首頁
						window.location.href = "./login.jsp";
					}
				}
			});
			
			
			if(atob(email).indexOf("@")==-1){
				//沒有小老鼠的email也跳回首頁喔
				window.location.href = "./login.jsp";
			}
		}
		catch(err) {
			//非base64的email也跳回首頁喔
			window.location.href = "./login.jsp";
		}
	}else{
// 		直接打網址 跳回首頁喔
		window.location.href = "./login.jsp";
	}
}
</script>
<jsp:include page="footer.jsp" flush="true"/>