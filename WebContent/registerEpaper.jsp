<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/styles_Epaper.css" />
<script>
$(function(){
	prepare();
	reset();
	$("#register").click(function() {
		if(!$("#register").attr("disabled")){
			var error_msg = "";
			error_msg += $("#nickname").val() == ''?((error_msg.length>0?', ':'')+'暱稱'):'';
			error_msg += $("#email").val() == ''?((error_msg.length>0?', ':'')+'email'):'';
			error_msg += $("#occupation").val() == ''?((error_msg.length>0?', ':'')+'職業'):'';
			
			if( error_msg.length>0){
				error_msg = "請填寫: " + error_msg;
				warningMsg("提示",error_msg);
				return ;
			}
			if($("#email").val().indexOf("@") == -1){
				error_msg = '<br>email格式不符規格，請修改。';
				warningMsg("提示",error_msg);
				return ;
			}
			$("#nickname").attr("disabled","true");
			$("#email").attr("disabled","true");
			$("#occupation").attr("disabled","true");
			$("#register").attr("disabled","true");
			
			$.ajax({
				type : "POST",
				url : "registerEpaper.do",
				data : {
					action : "insertNewRegister",
					nickname : $("#nickname").val(),
					email : $("#email").val(),
					occupation : $("#occupation").val()
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					if (json_obj.strMessage == "success"){
						warningMsg("提示","恭喜您完成電子報訂閱!!");
					}else if(json_obj.strMessage == "isduplicate"){
						warningMsg("警告","您已正在訂閱，請勿重複訂閱");
						$("#nickname").removeAttr("disabled","true");
						$("#email").removeAttr("disabled","true");
						$("#occupation").removeAttr("disabled","true");
						$("#register").removeAttr("disabled","true");
					}else if(json_obj.strMessage == "isexist"){
						warningMsg("警告","您訂閱過本電子報，無法再度訂閱，請聯絡系統管理員。");
					}else{
						warningMsg("警告","訂閱發生錯誤，請稍候再嘗試");
					}
				}
			});
		}
	});
	$("#reset").click(function(){
		reset();
	});
});
</script>
<%-- <%if( session.getAttribute("user_id") !=null){%> --%>
<%-- 	<jsp:include page="header.jsp" flush="true"/> --%>
<!-- 	<style>.search-result-wrap{padding:20px 30px 20px 160px;}</style> -->
<%-- <%}else{%> --%>
	<style>.search-result-wrap{padding:20px 30px 20px 30px;}</style>
<%-- <%}%> --%>
<div class="content-wrap">
	<h2 class="page-title">訂閱電子報</h2>
	<div class="search-result-wrap">
		<div class='paper_container'>
			<div class='paper_container_title'>訂閱電子報</div>
			<form id='serviceregister' style='margin:20px auto;'>
				<table class='form-table'>
				  	<colgroup>
					  <col width="20%">
					  <col width="80%">
					</colgroup>
					<tr>
						<td>　暱稱：</td>
						<td>
							<input type='text' id='nickname' class='if_too_short_for_parent' placeholder='請輸入暱稱'>
						</td>
					</tr>
					<tr>
						<td>E-mail：</td>
						<td>
							<input type='text' id='email' class='if_too_short_for_parent' placeholder='請輸入e-mail'>
						</td>
					</tr>
					<tr>
						<td>　職業：</td>
						<td id='occupation_container' >
							<select id='occupation' class='occupation'>
							<option value=''>請選擇職業</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan='2' class='center_content'>
							<a id='register' class='btn btn-wide btn-darkblue' >訂閱</a> 
							<a id='reset' class='btn btn-wide btn-gray' >重填</a>
						</td>
					</tr>
					<tr>
						<td colspan='2' class='center_content'>
							<a href="./login.jsp" class='' >返回系統</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</div>
<script>
function reset(){
	$("#nickname").val('');
	$("#email").val('');
	$("#occupation").val('');
	
	$("#nickname").removeAttr("disabled","true");
	$("#email").removeAttr("disabled","true");
	$("#occupation").removeAttr("disabled","true");
	$("#register").removeAttr("disabled","true");
}
function prepare(){
	var result = '[{"key":"主管職類","value":"主管職類"},'
				+'{"key":"經營與財務運作相關職類","value":"經營與財務運作相關職類"},'
				+'{"key":"資訊與數學相關職類","value":"資訊與數學相關職類"},'
				+'{"key":"建築與工程相關職類","value":"建築與工程相關職類"},'
				+'{"key":"生命、物理與社會科學相關職類","value":"生命、物理與社會科學相關職類"},'
				+'{"key":"社區與社會服務職類","value":"社區與社會服務職類"},'
				+'{"key":"法律相關職類","value":"法律相關職類"},'
				+'{"key":"教育、訓練、圖書館相關職類","value":"教育、訓練、圖書館相關職類"},'
				+'{"key":"藝術、設計、娛樂、運動、媒體相關職類","value":"藝術、設計、娛樂、運動、媒體相關職類"},'
				+'{"key":"健康照護醫療相關職類","value":"健康照護醫療相關職類"},'
				+'{"key":"健康照護支援相關職類","value":"健康照護支援相關職類"},'
				+'{"key":"保護服務相關職類","value":"保護服務相關職類"},'
				+'{"key":"餐飲相關職類","value":"餐飲相關職類"},'
				+'{"key":"環境清潔與維護相關職類","value":"環境清潔與維護相關職類"},'
				+'{"key":"個人照護與生活服務相關職類","value":"個人照護與生活服務相關職類"},'
				+'{"key":"業務相關職類","value":"業務相關職類"},'
				+'{"key":"行政支援相關職類","value":"行政支援相關職類"},'
				+'{"key":"農林漁牧相關職類","value":"農林漁牧相關職類"},'
				+'{"key":"營造與鑽探相關職類","value":"營造與鑽探相關職類"},'
				+'{"key":"設備裝修相關職類","value":"設備裝修相關職類"},'
				+'{"key":"生產製造相關職類","value":"生產製造相關職類"},'
				+'{"key":"交通運輸相關職類","value":"交通運輸相關職類"},'
				+'{"key":"軍職","value":"軍職"}]';
	
	var json_obj = $.parseJSON(result);
	
	$("#occupation").html(
		$("<option>", {
              value: "",
              html: "請選擇"
         })
	);
	
	$.each(json_obj,function(i,item){
		$("#occupation").append(
			$("<option/>", {
	              value: item.value,
	              html: item.key
	         })
		);
	});
}
</script>
<jsp:include page="footer.jsp" flush="true"/>