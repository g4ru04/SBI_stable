<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="import.jsp" flush="true"/>
<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<!--  css for d3js -->
<style>
.date2 {
    background-image: url('./images/icon-datepicker.svg') !important;
    background-repeat: no-repeat !important;
    background-position: right center !important;
}
table.form-table{
	border-collapse: separate;
	border-spacing: 10px 20px;
	margin-right: 30px;
	font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
}
table.form-table tr td:nth-child(2n+1) {
    text-align: right;
    padding-left: 4px;
    padding-bottom: 5px;
}
table.form-table tr td:nth-child(2n) {
    text-align: left;
}
input[type=text].error{
    border: 1px solid #e92500;
    background: rgb(255, 213, 204);
}
</style>
 
<%-- <jsp:include page="header.jsp" flush="true"/> --%>
<div class="content-wrap">
	<h2 class="page-title">商品售後服務註冊</h2>
	<div class="search-result-wrap">
		<div id='view' style='background:#f8f8f8;padding:20px;border: 3px solid #666;margin:20px auto;width:860px;	border-radius: 8px;box-shadow: 10px 10px 5px #999;'>
			<div align='center' style='font-size:36px;color:#888;'>商品售後服務註冊　&nbsp;</div>
			<form id='serviceregister' style='margin:20px;'>
				<table class='form-table'>
					<tr>
						<td>服務識別碼：</td>
						<td colspan='3'>
							<input type='text' id='service_id_1' style='width:125px;' name='service_id_1'  placeholder='識別碼1'>－<input type='text' id='service_id_2' style='width:64px;' name='service_id_2' placeholder='識別碼2'>－<input type='text' id='service_id_3' style='width:64px;' name='service_id_3' placeholder='識別碼3'>－<input type='text' id='service_id_4' style='width:64px;' name='service_id_4' placeholder='識別碼4'>－<input type='text' id='service_id_5' style='width:196px;' name='service_id_5' placeholder='識別碼5'>
						</td>
					</tr>
					<tr>
						<td>商品規格：</td>
						<td colspan='3'>
							<input type='text' id='product_id' name='product_id' style='width:627px;' placeholder='請填入商品規格'>
						</td>
					</tr>
					<tr>
						<td>顧客姓名：</td>
						<td>
							<input type='text' name='cust_name' placeholder='請填入顧客姓名'>
						</td>
						<td>顧客電話：</td>
						<td>
							<input type='text' name='cust_tel' placeholder='請填入顧客電話'>
						</td>
					</tr>
					<tr>
						<td>顧客手機：</td>
						<td>
							<input type='text' name='cust_mobile' placeholder='請填入顧客手機'>
						</td>
						<td>購買日期：</td>
						<td>
							<input type='text' name='purchase_date' class='date2' placeholder='請填入購買日期'>
						</td>
					</tr>
					<tr>
						<td>顧客地址：</td>
						<td colspan='3'>
							<input type='text' name='cust_address' style='width:627px;' placeholder='請填入顧客地址'>
						</td>
					</tr>
				</table>
				
				<div align='center'>
					<a id='register' class='btn btn-primary'>註冊</a>　　　
				</div>
			</form>
		</div>
	</div>
</div>
<script>
	$(function(){
		$("#serviceregister").validate({ rules : { 
			service_id_1 : { required : true }, 
			service_id_2 : { required : true }, 
			service_id_3 : { required : true }, 
			service_id_4 : { required : true }, 
			service_id_5 : { required : true }, 
			product_id : { required : true , maxlength : 40 }, 
			cust_name : { required : true , maxlength : 20 }, 
			cust_tel:{ required : true , maxlength : 20 },
			cust_mobile:{ maxlength : 20 },
			cust_address:{ maxlength : 200 },
			purchase_date:{ date : true }
		}}); 
		
		$("#serviceregister").keypress(function(e){
			if(e.which==13){
				$("#register").trigger("click");
			}
		});
		
		$("#product_id").autocomplete({
		     minLength: 1,
		     source: function (request, response) {
		         $.ajax({
		             url : "product.do",
		             type : "POST",
		             cache : false,
		             delay : 1500,
		             data : {
		             	action : "autocomplete_spec",
		             	term : request.term
		             },
		             success: function(data) {
		             	var json_obj = $.parseJSON(data);
		             	response($.map(json_obj, function (item) {
							return {
								label: item.product_spec,
			                    value: item.product_spec
			               	}
		             	}));
		             },
		             error: function(XMLHttpRequest, textStatus, errorThrown) {
		                 alert_dialog(textStatus);
		             }
		         });
		     },
		     change: function(e, ui) {
		     	 if (!ui.item) {
//	 	     		 $(this).val("");
		             $(this).attr("placeholder","請輸入查詢商品名稱");
		          }
		     },
		     response: function(e, ui) {
		         if (ui.content.length == 0) {
//	 	             $(this).val("");
		             $(this).attr("placeholder","請輸入查詢商品名稱");
		         }
		     }           
		 });
		
		$("#service_id_1").keypress(function(e){
			if($(this).val().length==8) {
				$("#service_id_2").focus();
			}
			if($(this).val().length>7 &&!e.ctrlKey &&  e.keyCode!=8 && e.keyCode!=9) {
				e.preventDefault();
				$(this).val($(this).val().substring(0,8));
			}
		});
		$("#service_id_2").keypress(function(e){
			if($(this).val().length==4){
				$("#service_id_3").focus();
			}
			if($(this).val().length>3 &&!e.ctrlKey &&  e.keyCode!=8 && e.keyCode!=9){
				e.preventDefault();
				$(this).val($(this).val().substring(0,4));
			}
		});
		$("#service_id_3").keypress(function(e){
			if($(this).val().length==4){
				$("#service_id_4").focus();
			}
			if($(this).val().length>3 &&!e.ctrlKey && e.keyCode!=8 && e.keyCode!=9){
				e.preventDefault();
				$(this).val($(this).val().substring(0,4));
			}
		});
		
		$("#service_id_4").keypress(function(e){
			if($(this).val().length==4){
				$("#service_id_5").focus();
			}
			if($(this).val().length>3 &&!e.ctrlKey && e.keyCode!=8 && e.keyCode!=9){
				e.preventDefault();
				$(this).val($(this).val().substring(0,4));
			}
		});
		
		$("#service_id_5").keypress(function(e){
			if($(this).val().length>11 &&!e.ctrlKey && e.keyCode!=8 && e.keyCode!=9){
				e.preventDefault();
				$(this).val($(this).val().substring(0,12));
			}
		});
		
		$("#register").click(function(){
			if($("#serviceregister").valid()){
				
				var service_id = $("#serviceregister input[name='service_id_1']").val()
				+ '-' + $("#serviceregister input[name='service_id_2']").val()
				+ '-' + $("#serviceregister input[name='service_id_3']").val()
				+ '-' + $("#serviceregister input[name='service_id_4']").val()
				+ '-' + $("#serviceregister input[name='service_id_5']").val();
				
				$.ajax({
					type : "POST",
					url : "serviceregister.do",
					data : {
						action : "insert_reg",
						service_id : service_id,
						product_id : $("#serviceregister input[name='product_id']").val(),
						cust_name : $("#serviceregister input[name='cust_name']").val(),
						cust_tel : $("#serviceregister input[name='cust_tel']").val(),
						cust_mobile : $("#serviceregister input[name='cust_mobile']").val(),
						cust_address : $("#serviceregister input[name='cust_address']").val(),
						purchase_date : $("#serviceregister input[name='purchase_date']").val()
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						console.log(json_obj);
						if(json_obj.success){
							$('.search-result-wrap').fadeOut(function(){
								$("input").prop('disabled', true);
								$("input").css('background-color', '#ddd');
								$("#register").after("<div style='font-size:36px;'>" + json_obj.info + "　&nbsp;</div>");
								$("#register").remove();
								$('.search-result-wrap').fadeIn();
							});
						};
					}
				});
				
			}
		});
		
		$( ".date2" ).datepicker({
		   dayNamesMin : ["日","一","二","三","四","五","六"],
		   monthNames : ["1","2","3","4","5","6","7","8","9","10","11","12"],
		   monthNamesShort:["1","2","3","4","5","6","7","8","9","10","11","12"],
		   prevText: "上月", nextText: "次月", weekHeader: "週", dateFormat: "yy-mm-dd",
		   showMonthAfterYear: true, changeYear: true,  changeMonth: true
		});
	});
</script>
<jsp:include page="footer.jsp" flush="true"/>