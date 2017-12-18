<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<script>
$(function(){
	
	var p_product_id = ""; 
	var p_row = "";
	
	var validator_insert = $("#insert-dialog-form-post").validate({
		rules : {
		}
	});
	var validator_update = $("#update-dialog-form-post").validate({
		rules : {
		}
	});	
	
	// 查詢商品資料 事件聆聽
	$("#btn_query").click(function(e) {
		$.ajax({
			type : "POST",
			url : "productService.do",
			data : {
				action : "selectByProductSpec",
				product_spec : $("#search_product_spec").val() 
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				var result_table = "";
				
				$.each(json_obj,function(i, item) {
					result_table 
						+= "<tr>"
						+ "<td id='product_spec_"+i+"'>" + item.product_spec + "</td>"
						+ "<td id='agent_name_"+i+"'>"+ item.agent_name + "</td>"
						+ "<td>"+ item.service_id + "</td>"
						+ "<td><div href='#' class='table-row-func btn-in-table btn-gray'><i class='fa fa-ellipsis-h'></i>"
						+ "<div class='table-function-list'>"
						+ "<button href='#' name='"+i+"' value='" + item.product_id + "' class='btn-update btn-in-table btn-green'><i class='fa fa-pencil'></i></button>"
						+ "<button href='#' name='" + item.product_spec + "' value='" + item.product_id + "' class='btn-delete btn-in-table btn-orange'><i class='fa fa-trash'></i></button>"
						+ "</div></div></td></tr>";								
				});	
				
				//判斷查詢結果
				var resultRunTime = 0;
				$.each (json_obj, function (i) {
					resultRunTime+=1;
				});
				
				$("#table_product tbody").html(result_table);
			}
		});
	});
	

	//處理 product spec 的autocomplete查詢
	$("#search_product_spec").autocomplete({
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
// 	     		 $(this).val("");
	             $(this).attr("placeholder","請輸入查詢商品名稱");
	          }
	     },
	     response: function(e, ui) {
	         if (ui.content.length == 0) {
// 	             $(this).val("");
	             $(this).attr("placeholder","請輸入查詢商品名稱");
	         }
	     }
	 });
	
})
</script>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">服務識別碼查詢作業</h2>
		<div class="input-field-wrap">
			<div class="form-wrap">
				<div class="form-row">
					<label for="">
						<span class="block-label">商品查詢</span>
						<input type="text" id="search_product_spec">
					</label>
					<a href="#" id="btn_query" class="btn btn-darkblue">查詢</a>
				</div>
			</div>
		</div>
		
		<div class="search-result-wrap">
			<div class="result-table-wrap">
				<table id="table_product" class="result-table">
					<thead>
						<tr>
							<th>商品規格</th>
							<th>授權代理商</th>
							<th>服務識別碼</th>
							<th>功能</th>
						</tr>
					</thead>
					<tbody style="text-align:center">
					</tbody>
				</table>
			</div>
		</div>
		
	</div>
<jsp:include page="footer.jsp" flush="true"/>