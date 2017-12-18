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
.axis path,
.axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}
.grid path,
.grid line {
  fill: none;
  stroke: rgba(0, 0, 0, 0.25);
  shape-rendering: crispEdges;
}
.x.axis path {
  display: none;
}
.line {
  fill: none;
  stroke-width: 2.5px;
}
</style>
	
<style>
.text-center{
	text-align: center;
}
.margin-center{
	margin: 0px auto;
}
h2.ui-list-title {
	border-bottom: 1px solid #ccc;
	color: #307CB0;
}
</style>	
	
<!-- /**************************************  以下共用JS區塊    *********************************************/ 	-->
<script>
$(function(){
	$( "#tabs" ).tabs();
});
function convertAction (action){
	var temp = "";
	if(action){
		temp = "<font color='red'><b>實際</b></font>";
	} else {
		temp = "模擬";
	}
	return temp;
}
function convertType (type){
	var temp = "";
	switch(type){
		case 1 :
			temp = "已發生";
			break;
		case 2 :
			temp = "應收/應付";
			break;
		default: 
			temp = "default";
			break;
	}
	return temp;
}
function convertKind (kind){
	var temp = "";
	switch(kind){
		case 1 :
			temp = "營業收入";
			break;
		case 2 :
			temp = "業務支出";
			break;
		case 3 :
			temp = "固定資產支出";
			break;
		case 4 :
			temp = "管銷費用";
			break;
		case 5 :
			temp = "薪資";
			break;
		case 6 :
			temp = "研發費用";
			break;
		case 7 :
			temp = "行銷費用";
			break;
		case 8 :
			temp = "投資收入/支出";
			break;
		case 9 :
			temp = "其他收入/支出";
			break;
		case 10 :
			temp = "期初值";
			break;
		case 11 :
			temp = "投資收入";
			break;
		case 12 :
			temp = "投資支出";
			break;
		case 13 :
			temp = "其他收入";
			break;
		case 14 :
			temp = "其他支出";
			break;
		default: 
			temp = "default";
			break;
	}
	return temp;
}
function genResultTable(index, jsonobj, action, f_type, f_kind, resultTable){
	var tempAmount = "";
	
	if (jsonobj.amount < 0 ){
		tempAmount = "<font color='red'><b>" + jsonobj.amount + "</b></font>";
	} else {
		tempAmount = jsonobj.amount;
	}
		
	resultTable 
	+= "<tr>"
	+ "<td id='f_date_"+index+"'>"+ jsonobj.f_date+ "</td>"
	+ "<td id='f_type_"+index+"'>"+ f_type+ "<input type='hidden' id='hidden_f_type_"+index+"' value='"+ jsonobj.f_type +"' ></td>"
	+ "<td id='action_"+index+"'>"+ action+ "<input type='hidden' id='hidden_action_"+index+"' value='"+ jsonobj.action +"' ></td>"
	+ "<td id='amount_"+index+"' hidden='true'>"+ jsonobj.amount + "</td>"
	+ "<td>"+ tempAmount + "</td>"
	+ "<td id='f_kind_"+index+"'>"+ f_kind+ "<input type='hidden' id='hidden_f_kind_"+index+"' value='"+ jsonobj.f_kind +"' ></td>"
	+ "<td id='description_"+index+"'>"+ jsonobj.description+ "</td>"
	+ "<td id='strategy_"+index+"'>"+ jsonobj.strategy+ "</td>"
	+ "<td><button id='"+index+"' value='"+ jsonobj.simulation_id+"' name='"+ jsonobj.case_id + "' "
	+ "class='btn_query btn_update btn btn-wide btn-primary'>修改</button>&nbsp;&nbsp;"
	+ "<button value='"+ jsonobj.simulation_id+"' name='"+ jsonobj.case_id+ "' "
	+ "class='btn_delete btn btn-wide btn-primary'>刪除</button></td></tr>";
	return resultTable;
}
</script>
<!-- /**************************************  以上共用JS區塊    **********************************************/ 		-->	

<!-- /**************************************  以下管理者JS區塊    *********************************************/		-->
<c:if test="${sessionScope.role==1}">
	<script>
		$(function(){
			var uuid = "";
			var p_simulation_id = "";
			var p_case_id = "";
			var g_create_date = "";
				
			var validator_create = $("#create-dialog-form-post").validate({
				rules : {
					case_name : {
						required : true
					},
					amount : {
						required : true,
						number : true
					},
					safety_money : {
						required : true,
						number : true
					}
				}
			});
			
			var validator_insert = $("#insert-dialog-form-post").validate({
				rules : {
					f_date : {
						required : true,
						dateISO : true
					},
					amount : {
						number : true
					}
				}
			});
			
			var validator_update = $("#update-dialog-form-post").validate({
				rules : {
					f_date : {
						required : true,
						dateISO : true
					},
					amount : {
						number : true
					}
				}
			});
			
			//建立模型 事件聆聽
			$("#create-model-button").click( function(e) {
				e.preventDefault();		
				create_dialog.dialog("open");
			});	
			
			//建立模型Dialog相關設定
			create_dialog = $("#dialog-form-create").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				width : 'auto',
				modal : true,
				buttons : [{
					id : "create",
					text : "建立",
					click : function() {
						
						if ($('#create-dialog-form-post').valid()) {
							$.ajax({
								type : "POST",
								url : "finModel.do",
								data : {
									action : "create",
									case_name : $("#dialog-form-create input[name='case_name']").val(),
									amount : $("#dialog-form-create input[name='amount']").val(),
									safety_money : $("#dialog-form-create input[name='safety_money']").val()
								},
								success : function(result) {
									var json_obj = $.parseJSON(result);
									var result_table = "";
									
									$.each(json_obj,function(i, item) {
										result_table += "<tr>"
											+ "<td>"+json_obj[i].case_name+"</td>"
											+ "<td class='create_date'>"+json_obj[i].create_date+"</td>"
											+ "<td><button value='" + json_obj[i].case_id + "' name='" + json_obj[i].case_id + "' "
											+ "class='btn_query btn btn-primary'>查看</button></td>"
											+ "<td><button value='" + json_obj[i].case_id + "' name='user_query'"
											+ "class='btn-simu btn-bath btn btn-primary'>產生</button></td>"
											+ "</tr>";											
									});
									
									//判斷查詢結果
									var resultRunTime = 0;
									$.each (json_obj, function (i) {
										resultRunTime+=1;
									});
									
									if(resultRunTime!=0){
										$("#fincase-table-admin tbody").html(result_table);
									}
								}
							});
							
							create_dialog.dialog("close");
						}
					}
				}, {
					text : "取消",
					click : function() {
						validator_create.resetForm();
						$("#create-dialog-form-post").trigger("reset");
						create_dialog.dialog("close");
					}
				}],
				close : function() {
					validator_create.resetForm();
					$("#create-dialog-form-post").trigger("reset");
					create_dialog.dialog("close");
				}
			}); 
				
			//onload 時帶入fincase資料
			$.ajax({
				type : "POST",
				url : "finModel.do",
				data : {
					action : "onload"
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					var result_table = "";
					$.each(json_obj,function(i, item) {
						result_table += "<tr>"
							+ "<td>"+json_obj[i].case_name+"</td>"
							+ "<td class='create_date'>"+json_obj[i].create_date+"</td>"
							+ "<td><button value='"+ json_obj[i].case_id+"' name='"+ json_obj[i].case_id + "' "
							+ "class='btn_query btn btn-primary'>查看</button></td>"
							+ "<td><button value='"+ json_obj[i].case_id+"' name='user_query'"
							+ "class='btn-simu btn-bath btn btn-primary'>產生</button></td>"
							+ "</tr>";
					});					
					//判斷查詢結果
					var resultRunTime = 0;
					$.each (json_obj, function (i) {
						resultRunTime+=1;
					});
					if(resultRunTime!=0){
						$("#fincase-table-admin tbody").html(result_table);
					}
				}
			});
			
			//查看財務計畫 事件聆聽
			$("#fincase-table-admin").delegate(".btn_query", "click", function() {
				uuid = $(this).val();
				$("#gen_d3js_button").val($(this).val());
				g_create_date = $("[name='" + uuid + "']").parent().parent().children( ".create_date" ).text();
				
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "case_query",
						case_id: uuid
					},
					success : function(result) {
						$("#fincase-div-admin").hide();
						$("#finsimu-div-admin").show();
						$("#switch-simu-button").prop('name', uuid);
						$("#insert-simu-button").prop('name', uuid);
						$("#hidden_case_id").val(uuid);
						
						var json_obj = $.parseJSON(result);
						var result_table = "";
						
						$.each(json_obj,function(i, item) {
							var str_f_type = "";
							var str_action = "";
							var str_f_kind = "";
							str_action = convertAction(json_obj[i].action);
							str_f_type = convertType(json_obj[i].f_type);
							str_f_kind = convertKind(json_obj[i].f_kind);
							result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
						});
						
						//判斷查詢結果
						var resultRunTime = 0;
						$.each (json_obj, function (i) {
							resultRunTime+=1;
						});
						
						if(resultRunTime!=0){
							$("#finsimu-table-admin tbody").html(result_table);
						}
					}
				});					
			});
			//新增事件聆聽
			$("#insert-simu-button").click(function(e) {
				e.preventDefault();		
				uuid = $(this).attr('name');
				insert_dialog.dialog("open");
			});
			
			//"新增" Dialog相關設定
			insert_dialog = $("#dialog-form-insert").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				width : 'auto',
				modal : true,
				buttons : [{
					id : "insert",
					text : "新增",
					click : function() {
						if ($('#insert-dialog-form-post').valid()) {
							
							if (new Date(g_create_date) > new Date($("#insert_f_date").val())) {
								warningMsg("警告","您輸入的資金動態日期小於案件產生日期(" + g_create_date + ")");
								return; 
							}
							
							$.ajax({
								type : "POST",
								url : "finModel.do",
								data : {
									action : "insert",
		 							case_id: uuid,
		 							f_date : $("#insert_f_date").val(),
		 							f_type : $("#insert_f_type").val(),
		 							p_action : $("#insert_action").val(),
		 							amount : $("#insert_amount").val(),
		 							f_kind : $("#insert_f_kind").val(),
									description : $("#insert_description").val(),
									strategy : $("#insert_strategy").val()
								},
								success : function(result) {
									var json_obj = $.parseJSON(result);
									var result_table = "";
									
									$.each(json_obj,function(i, item) {
										var str_f_type = "";
										var str_action = "";
										var str_f_kind = "";
										
										str_action = convertAction(json_obj[i].action);
										str_f_type = convertType(json_obj[i].f_type);
										str_f_kind = convertKind(json_obj[i].f_kind);
										
										result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
									});	
									
									//判斷查詢結果
									var resultRunTime = 0;
									$.each (json_obj, function (i) {
										resultRunTime+=1;
									});
									
									if(resultRunTime!=0){
										$("#finsimu-table-admin tbody").html(result_table);
									}
								}
							});
							
							insert_dialog.dialog("close");
						}
					}
				}, {
					text : "取消",
					click : function() {
						validator_insert.resetForm();
						$("#insert-dialog-form-post").trigger("reset");
						insert_dialog.dialog("close");
					}
				}],
				close : function() {
					validator_insert.resetForm();
					$("#insert-dialog-form-post").trigger("reset");
					insert_dialog.dialog("close");
				}
			});
			
			//修改 事件聆聽
			$("#finsimu-table-admin").delegate(".btn_update", "click", function(e) {
				e.preventDefault();
				p_simulation_id = $(this).val();
				p_case_id = $(this).attr('name');
				row = $(this).attr("id");
				$("#dialog-form-update input[name='f_date']").val($('#f_date_'+row).html());
				$("#dialog-form-update select#edit_f_type option[value="+$('#hidden_f_type_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update select#edit_action option[value="+$('#hidden_action_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update input[name='amount']").val($('#amount_'+row).html());
				$("#dialog-form-update select#edit_f_kind option[value="+$('#hidden_f_kind_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update input[name='description']").val($('#description_'+row).html());
				$("#dialog-form-update input[name='strategy']").val($('#strategy_'+row).html());
				$("#dialog-form-update input[name='simulation_id']").val(p_simulation_id);
				$("#dialog-form-update input[name='case_id']").val(p_case_id);
				update_dialog.dialog("open");
			});
			
			//"修改" Dialog相關設定
			update_dialog = $("#dialog-form-update").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				width : 'auto',
				modal : true,
				buttons : [{
					id : "update",
					text : "修改",
					click : function() {
						if ($('#update-dialog-form-post').valid()) {
							
							if (new Date(g_create_date) > new Date($("#edit_f_date").val())) {
								warningMsg("警告","您輸入的資金動態日期小於案件產生日期(" + g_create_date + ")");
								return; 
							}
							
							$.ajax({
								type : "POST",
								url : "finModel.do",
								data : {
		 							action : "update",
		 							case_id: p_case_id,
		 							simulation_id: p_simulation_id, 
		 							f_date : $("#edit_f_date").val(),
		 							f_type : $("#edit_f_type").val(),
		 							p_action : $("#edit_action").val(),
		 							amount : $("#edit_amount").val(),
		 							f_kind : $("#edit_f_kind").val(),
									description : $("#edit_description").val(),
									strategy : $("#edit_strategy").val()
								},
								success : function(result) {
									var json_obj = $.parseJSON(result);
									var len=json_obj.length;
									var result_table = "";
									
									$.each(json_obj,function(i, item) {
										var str_f_type = "";
										var str_action = "";
										var str_f_kind = "";
										
										str_action = convertAction(json_obj[i].action);
										str_f_type = convertType(json_obj[i].f_type);
										str_f_kind = convertKind(json_obj[i].f_kind);
											
										result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
									});			
									
									//判斷查詢結果
									var resultRunTime = 0;
									$.each (json_obj, function (i) {
										resultRunTime+=1;
									});
									
									if(resultRunTime!=0){
										$("#finsimu-table-admin tbody").html(result_table);
									}
								}
							});
							
							update_dialog.dialog("close");
						}
					}
				}, {
					text : "取消",
					click : function() {
						validator_update.resetForm();
						$("#update-dialog-form-post").trigger("reset");
						update_dialog.dialog("close");
					}
				}],
				close : function() {
					$("#update-dialog-form-post").trigger("reset");
					validator_update.resetForm();
					update_dialog.dialog("close");
				}
			});
			
			//刪除事件聆聽 : 因為聆聽事件動態產生，所以採用delegate來批量處理，節省資源
			$("#finsimu-table-admin").delegate(".btn_delete", "click", function() {
				var p_simulation_id = $(this).val();
				var p_case_id = $(this).attr('name');
				$("#delete_case_id").val(p_case_id);
				$("#delete_simu_id").val(p_simulation_id);
				confirm_dialog.dialog("open");
			});
			
			//"刪除" Dialog相關設定
			confirm_dialog = $("#dialog-confirm").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				height : 'auto',
				modal : true,
				buttons : {
					"確認刪除" : function() {
						$("#finsimu-table-admin tbody").html("");
						
						$.ajax({
							type : "POST",
							url : "finModel.do",
							data : {
								action : "delete",
								case_id : $("#delete_case_id").val(),
								simulation_id : $("#delete_simu_id").val()
							},
							success : function(result) {
								var json_obj = $.parseJSON(result);
								var len=json_obj.length;
								var result_table = "";
								
								$.each(json_obj,function(i, item) {
									var str_f_type = "";
									var str_action = "";
									var str_f_kind = "";
									
									str_action = convertAction(json_obj[i].action);
									str_f_type = convertType(json_obj[i].f_type);
									str_f_kind = convertKind(json_obj[i].f_kind);
									result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
								});	
								
								//判斷查詢結果
								var resultRunTime = 0;
								$.each (json_obj, function (i) {
									resultRunTime+=1;
								});
								
								if(resultRunTime!=0){
									$("#finsimu-table-admin tbody").html(result_table);
								}
							}
						});
						
						$(this).dialog("close");
					},
					"取消刪除" : function() {
						$(this).dialog("close");
					}
				}
			});
			
			//切換至"產生模擬資料"頁面
			$("#switch-simu-button").click( function() {
				uuid = $(this).attr('name');
				simu_dialog.dialog("open");
			});
			
			//"產生模擬資料" Dialog相關設定
			simu_dialog = $("#dialog-gen-simu").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				height : 'auto',
				width : 'auto',
				modal : true,
				buttons : [{
					text : "確認",
					click : function() {
						
						$('body').css('cursor', 'progress');
						degree = $("#degree").val();
						blndel = document.querySelector('input[name="blndel"]:checked').value;
						$.ajax({
							type : "POST",
							url : "finModel.do",
							data : {
								action : "gen_simu_data",
								case_id: uuid,
								degree: degree, 
								blndel:	blndel
							},
							success : function(result) {
								$("body").css("cursor", "default");	
								var json_obj = $.parseJSON(result);
								var result_table = "";
								
								$.each(json_obj,function(i, item) {
									var str_f_type = "";
									var str_action = "";
									var str_f_kind = "";
									
									str_action = convertAction(json_obj[i].action);
									str_f_type = convertType(json_obj[i].f_type);
									str_f_kind = convertKind(json_obj[i].f_kind);
									
									result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
								});	
								
								//判斷查詢結果
								var resultRunTime = 0;
								$.each (json_obj, function (i) {
									resultRunTime+=1;
								});
								
								if(resultRunTime!=0){
									$("#finsimu-table-admin tbody").html(result_table);
								}
							}
						});	
						
 						$(this).dialog("close");
					}
				}, {
					text : "取消",
					click : function() {
						$(this).dialog("close");
					}
				}]
			});
			
			//產生模擬圖
			$("#gen_d3js_button").click( function() {
				uuid = $("#hidden_case_id").val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js",
						case_id: uuid
					},
					success : function(result) {
						//alert("1");
						console.log(result);
						var obj = JSON.parse(result);
						console.log("go genSimuGraph3");
						genSimuGraph3(obj[0].Income_True, obj[1].Income_False, obj[2].Outcome_True, obj[3].Outcome_False, obj[4].Income_Total, obj[5].Outcome_Total,obj[6].BalanceDate);
						//genSimuGraph2(obj[0].Income, obj[1].Outcome, obj[2].Income_Total, obj[3].Outcome_Total);
						return;//draw_line_1
						
						var obj = JSON.parse(result);
						var income = obj[0].income;
						var outlay = obj[1].outlay;
						var detailData = obj[2].detailData;
						var fincase = obj[3].fincase;
						var totalIncome = obj[4].totalIncome;
						var totalOutlay = obj[5].totalOutlay;
						genSimuGraph(income, outlay, detailData, fincase, totalIncome, totalOutlay);
					}
				});	
			});
				
			//產生模擬圖
			$("#fincase-table-admin").delegate(".btn-simu", "click", function() {
				uuid = $(this).val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js",
						case_id: uuid
					},
					success : function(result) {
						//alert("2");
						var obj = JSON.parse(result);
						genSimuGraph3(obj[0].Income_True, obj[1].Income_False, obj[2].Outcome_True, obj[3].Outcome_False, obj[4].Income_Total, obj[5].Outcome_Total,obj[6].BalanceDate);
						//genSimuGraph2(obj[0].Income, obj[1].Outcome, obj[2].Income_Total, obj[3].Outcome_Total);
						return;//draw_line_2
						
						var obj = JSON.parse(result);
						var income = obj[0].income;
						var outlay = obj[1].outlay;
						var detailData = obj[2].detailData;
						var fincase = obj[3].fincase;
						var totalIncome = obj[4].totalIncome;
						var totalOutlay = obj[5].totalOutlay;
						genSimuGraph(income, outlay, detailData, fincase, totalIncome, totalOutlay);
					}
				});	
			});
			$("body").delegate(".btn-bath", "click", function() {
				uuid = $(this).val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js_bath",
						case_id: uuid
					},
					success : function(result) {
						//take a bath
						var json_obj = JSON.parse(result);
						draw_bath(json_obj);
						//################################################################
						return;
					}
				});	
			});
			
		});
	</script>
</c:if>
<!-- /**************************************  以上管理者JS區塊    *********************************************/	-->
 
<!-- /**************************************  以下使用者JS區塊    *********************************************/ 	-->
<c:if test="${sessionScope.role==0}">
	<script>
		$(function(){
			var uuid = "";
			var p_simulation_id = "";
			var p_case_id = "";
			var g_create_date = "";
			
			var validator_insert = $("#insert-dialog-form-post").validate({
				rules : {
					f_date : {
						required : true,
						dateISO : true
					},
					amount : {
						number : true
					}
				}
			});
			
			var validator_update = $("#update-dialog-form-post").validate({
				rules : {
					f_date : {
						required : true,
						dateISO : true
					},
					amount : {
						number : true
					}
				}
			});
			
			//onload時帶入fincase資料
			$.ajax({
				type : "POST",
				url : "finModel.do",
				data : {
					action : "onload"
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					var result_table = "";
					$.each(json_obj,function(i, item) {
						result_table += "<tr>" 
							+ "<td>"+json_obj[i].case_name+"</td>"
							+ "<td class='create_date'>"+json_obj[i].create_date+"</td>"
							+ "<td><button value='"+ json_obj[i].case_id+"' name='user_query' "
							+ "class='btn_query btn btn-primary'>查看</button></td>"
							+ "<td><button value='"+ json_obj[i].case_id+"' name='"+ json_obj[i].case_id + "' "
							+ "class='btn-simu btn-bath btn btn-primary'>產生</button></td>"
							+ "</tr>";
					});					
					//判斷查詢結果
					var resultRunTime = 0;
					$.each (json_obj, function (i) {
						resultRunTime+=1;
					});
					if(resultRunTime!=0){
						$("#fincase-table-user tbody").html(result_table);
					}
				}
			});
			
			//查看財務計畫 事件聆聽
			$("#fincase-table-user").delegate(".btn_query", "click", function() {
				uuid = $(this).val();
				$("#gen_d3js_button").val($(this).val());
				g_create_date = $("[name='" + uuid + "']").parent().parent().children( ".create_date" ).text();
				
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "case_query",
						case_id: uuid
					},
					success : function(result) {
						$("#fincase-div-user").hide();
						$("#finsimu-div-user").show();
						$("#switch-simu-button").prop('name', uuid);
						$("#insert-simu-button").prop('name', uuid);
						$("#hidden_case_id").val(uuid);
						
						var json_obj = $.parseJSON(result);
						var result_table = "";
						
						$.each(json_obj,function(i, item) {
							var str_f_type = "";
							var str_action = "";
							var str_f_kind = "";
							
							str_action = convertAction(json_obj[i].action);
							str_f_type = convertType(json_obj[i].f_type);
							str_f_kind = convertKind(json_obj[i].f_kind);
							result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
						});
						
						//判斷查詢結果
						var resultRunTime = 0;
						$.each (json_obj, function (i) {
							resultRunTime+=1;
						});
						if(resultRunTime!=0){
							$("#finsimu-table-user tbody").html(result_table);
						}
					}
				});					
			});
			
			//新增事件聆聽
			$("#insert-simu-button").click(function(e) {
				e.preventDefault();		
				uuid = $(this).attr('name');
				insert_dialog.dialog("open");
			});
			
			//"新增" Dialog相關設定
			insert_dialog = $("#dialog-form-insert").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				width : 'auto',
				modal : true,
				buttons : [{
					id : "insert",
					text : "新增",
					click : function() {
						if ($('#insert-dialog-form-post').valid()) {
							
							if (new Date(g_create_date) > new Date($("#insert_f_date").val())) {
								warningMsg("警告","您輸入的資金動態日期小於案件產生日期(" + g_create_date + ")");
								return; 
							}
							
							$.ajax({
								type : "POST",
								url : "finModel.do",
								data : {
									action : "insert",
		 							case_id: uuid,
		 							f_date : $("#insert_f_date").val(),
		 							f_type : $("#insert_f_type").val(),
		 							p_action : $("#insert_action").val(),
		 							amount : $("#insert_amount").val(),
		 							f_kind : $("#insert_f_kind").val(),
									description : $("#insert_description").val(),
									strategy : $("#insert_strategy").val()
								},
								success : function(result) {
									var json_obj = $.parseJSON(result);
// 									var len=json_obj.length;
									var result_table = "";
									
									$.each(json_obj,function(i, item) {
										var str_f_type = "";
										var str_action = "";
										var str_f_kind = "";
										
										str_action = convertAction(json_obj[i].action);
										str_f_type = convertType(json_obj[i].f_type);
										str_f_kind = convertKind(json_obj[i].f_kind);
										result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
									});
									
									//判斷查詢結果
									var resultRunTime = 0;
									$.each (json_obj, function (i) {
										resultRunTime+=1;
									});
									
									if(resultRunTime!=0){
										$("#finsimu-table-user tbody").html(result_table);
									}
								}
							});
							
							insert_dialog.dialog("close");
						}
					}
				}, {
					text : "取消",
					click : function() {
						validator_insert.resetForm();
						$("#insert-dialog-form-post").trigger("reset");
						insert_dialog.dialog("close");
					}
				}],
				close : function() {
					validator_insert.resetForm();
					$("#insert-dialog-form-post").trigger("reset");
					insert_dialog.dialog("close");
				}
			});
			
			//修改 事件聆聽
			$("#finsimu-table-user").delegate(".btn_update", "click", function(e) {
				e.preventDefault();
				p_simulation_id = $(this).val();
				p_case_id = $(this).attr('name');
				row = $(this).attr("id");
				$("#dialog-form-update input[name='f_date']").val($('#f_date_'+row).html());
				$("#dialog-form-update select#edit_f_type option[value="+$('#hidden_f_type_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update select#edit_action option[value="+$('#hidden_action_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update input[name='amount']").val($('#amount_'+row).html());
				$("#dialog-form-update select#edit_f_kind option[value="+$('#hidden_f_kind_'+row).val()+"]").prop("selected", true);
				$("#dialog-form-update input[name='description']").val($('#description_'+row).html());
				$("#dialog-form-update input[name='strategy']").val($('#strategy_'+row).html());
				$("#dialog-form-update input[name='simulation_id']").val(p_simulation_id);
				$("#dialog-form-update input[name='case_id']").val(p_case_id);
				update_dialog.dialog("open");
			});
			
			//"修改" Dialog相關設定
			update_dialog = $("#dialog-form-update").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				width : 'auto',
				modal : true,
				buttons : [{
					id : "update",
					text : "修改",
					click : function() {
						if ($('#update-dialog-form-post').valid()) {
							
							if (new Date(g_create_date) > new Date($("#edit_f_date").val())) {
								warningMsg("警告","您輸入的資金動態日期小於案件產生日期(" + g_create_date + ")");
								return; 
							}
							
							$.ajax({
								type : "POST",
								url : "finModel.do",
								data : {
		 							action : "update",
		 							case_id: p_case_id,
		 							simulation_id: p_simulation_id, 
		 							f_date : $("#edit_f_date").val(),
		 							f_type : $("#edit_f_type").val(),
		 							p_action : $("#edit_action").val(),
		 							amount : $("#edit_amount").val(),
		 							f_kind : $("#edit_f_kind").val(),
									description : $("#edit_description").val(),
									strategy : $("#edit_strategy").val()
								},
								success : function(result) {
									var json_obj = $.parseJSON(result);
									var len=json_obj.length;
									var result_table = "";
									
									$.each(json_obj,function(i, item) {
										var str_f_type = "";
										var str_action = "";
										var str_f_kind = "";
										
										str_action = convertAction(json_obj[i].action);
										str_f_type = convertType(json_obj[i].f_type);
										str_f_kind = convertKind(json_obj[i].f_kind);
										result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
									});		
									
									//判斷查詢結果
									var resultRunTime = 0;
									$.each (json_obj, function (i) {
										resultRunTime+=1;
									});
									
									if(resultRunTime!=0){
										$("#finsimu-table-user tbody").html(result_table);
									}
								}
							});
							
							update_dialog.dialog("close");
						}
					}
				}, {
					text : "取消",
					click : function() {
						validator_update.resetForm();
						$("#update-dialog-form-post").trigger("reset");
						update_dialog.dialog("close");
					}
				}],
				close : function() {
					$("#update-dialog-form-post").trigger("reset");
					validator_update.resetForm();
					update_dialog.dialog("close");
				}
			});
			
			//刪除事件聆聽 : 因為聆聽事件動態產生，所以採用delegate來批量處理，節省資源
			$("#finsimu-table-user").delegate(".btn_delete", "click", function() {
				var p_simulation_id = $(this).val();
				var p_case_id = $(this).attr('name');
				$("#delete_case_id").val(p_case_id);
				$("#delete_simu_id").val(p_simulation_id);
				confirm_dialog.dialog("open");
			});
			
			//"刪除" Dialog相關設定
			confirm_dialog = $("#dialog-confirm").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				height : 'auto',
				modal : true,
				buttons : {
					"確認刪除" : function() {
						$("#finsimu-table-user tbody").html("");
						
						$.ajax({
							type : "POST",
							url : "finModel.do",
							data : {
								action : "delete",
								case_id : $("#delete_case_id").val(),
								simulation_id : $("#delete_simu_id").val()
							},
							success : function(result) {
								var json_obj = $.parseJSON(result);
								var len=json_obj.length;
								var result_table = "";
								
								$.each(json_obj,function(i, item) {
									var str_f_type = "";
									var str_action = "";
									var str_f_kind = "";
									
									str_action = convertAction(json_obj[i].action);
									str_f_type = convertType(json_obj[i].f_type);
									str_f_kind = convertKind(json_obj[i].f_kind);
									result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
								});
								
								//判斷查詢結果
								var resultRunTime = 0;
								$.each (json_obj, function (i) {
									resultRunTime+=1;
								});
								
								if(resultRunTime!=0){
									$("#finsimu-table-user tbody").html(result_table);
								}
							}
						});
						
						$(this).dialog("close");
					},
					"取消刪除" : function() {
						$(this).dialog("close");
					}
				}
			});
			//切換至"產生模擬資料"頁面
			$("#switch-simu-button").click( function() {
				uuid = $(this).attr('name');
				simu_dialog.dialog("open");
			});
			
			//"產生模擬資料" Dialog相關設定
			simu_dialog = $("#dialog-gen-simu").dialog({
				draggable : false,//防止拖曳
				resizable : false,//防止縮放
				autoOpen : false,
				show : {
					effect : "clip",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				},
				height : 'auto',
				width : 'auto',
				modal : true,
				buttons : [{
					text : "確認",
					click : function() {
						$('body').css('cursor', 'progress');
						degree = $("#degree").val();
						blndel = document.querySelector('input[name="blndel"]:checked').value;
						$.ajax({
							type : "POST",
							url : "finModel.do",
							data : {
								action : "gen_simu_data",
								case_id: uuid,
								degree: degree, 
								blndel:	blndel
							},
							success : function(result) {
								$("body").css("cursor", "default");	
								var json_obj = $.parseJSON(result);
								var result_table = "";
								$.each(json_obj,function(i, item) {
									var str_f_type = "";
									var str_action = "";
									var str_f_kind = "";
									
									str_action = convertAction(json_obj[i].action);
									str_f_type = convertType(json_obj[i].f_type);
									str_f_kind = convertKind(json_obj[i].f_kind);
									result_table = genResultTable(i, json_obj[i], str_action, str_f_type, str_f_kind, result_table);
								});
								
								//判斷查詢結果
								var resultRunTime = 0;
								$.each (json_obj, function (i) {
									resultRunTime+=1;
								});
								
								if(resultRunTime!=0){
									$("#finsimu-table-user tbody").html(result_table);
								}
							}
						});	
						
 						$(this).dialog("close");
					}
				}, {
					text : "取消",
					click : function() {
						$(this).dialog("close");
					}
				}]
			});
						
			//產生模擬圖
			$("#gen_d3js_button").click( function() {
				uuid = $("#hidden_case_id").val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js",
						case_id: uuid
					},
					success : function(result) {
						//alert("3");
						var obj = JSON.parse(result);
						genSimuGraph3(obj[0].Income_True, obj[1].Income_False, obj[2].Outcome_True, obj[3].Outcome_False, obj[4].Income_Total, obj[5].Outcome_Total,obj[6].BalanceDate);
						//genSimuGraph2(obj[0].Income, obj[1].Outcome, obj[2].Income_Total, obj[3].Outcome_Total);
						return; //draw_line_3
						
						var obj = JSON.parse(result);
						var income = obj[0].income;
						var outlay = obj[1].outlay;
						var detailData = obj[2].detailData;
						var fincase = obj[3].fincase;
						var totalIncome = obj[4].totalIncome;
						var totalOutlay = obj[5].totalOutlay;
						genSimuGraph(income, outlay, detailData, fincase, totalIncome, totalOutlay);
					}
				});	
			});
			
			//產生模擬圖
			$("#fincase-table-user").delegate(".btn-simu", "click", function() {
				uuid = $(this).val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js",
						case_id: uuid
					},
					success : function(result) {
						var obj = JSON.parse(result);
						genSimuGraph3(obj[0].Income_True, obj[1].Income_False, obj[2].Outcome_True, obj[3].Outcome_False, obj[4].Income_Total, obj[5].Outcome_Total,obj[6].BalanceDate);
						//genSimuGraph2(obj[0].Income, obj[1].Outcome, obj[2].Income_Total, obj[3].Outcome_Total);
						return; //draw_line_4
						
						var obj = JSON.parse(result);
						var income = obj[0].income;
						var outlay = obj[1].outlay;
						var detailData = obj[2].detailData;
						var fincase = obj[3].fincase;
						var totalIncome = obj[4].totalIncome;
						var totalOutlay = obj[5].totalOutlay;
						genSimuGraph(income, outlay, detailData, fincase, totalIncome, totalOutlay);
					}
				});	
			});
			$("body").delegate(".btn-bath", "click", function() {
				uuid = $(this).val();
				$.ajax({
					type : "POST",
					url : "finModel.do",
					data : {
						action : "gen_d3js_bath",
						case_id: uuid
					},
					success : function(result) {
						//take a bath
						var json_obj = JSON.parse(result);
						draw_bath(json_obj);
						//################################################################
						return;
					}
				});	
			});
		})
		
		//日期設定
		$(".date").datepicker({
			dayNamesMin:["日","一","二","三","四","五","六"],
			monthNames:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
			dateFormat:"yy-mm-dd",
			changeYear:true
		});
	</script>
</c:if>
<!-- /**************************************  以上使用者JS區塊    *********************************************/	-->

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	<h2 class="page-title">新創公司財務損益平衡評估工具</h2>
	<!--<div id="msgAlert"></div>-->
	
	<div class="search-result-wrap">
		<div id="tabs" style='min-width:1180px;'>
			<ul>
				<li><a href="#finTool">財務評估工具</a></li>
				<li><a href="#simuGraph">模擬圖</a></li>
				<li><a href="#bathtub">資金失效圖</a></li>
			</ul>
		
			<div id="finTool">
				<section>
					<!--==================    財務評估工具 (管理者)    ==================-->
			       	<c:if test="${sessionScope.role==1}">
						<div class="text-center">
				   			<div>
					       		<button id="create-model-button" class="btn btn-primary">建立模型</button>
				        	</div>
				        	<br>
				        	<div>
				        		<div class="text-center" id="fincase-div-admin">
						            <table id="fincase-table-admin" class="result-table">
										<thead>
											<tr>
												<th>案件名稱</th>
												<th>案件產生日期</th>
												<th>財務計畫</th>
												<th>產生模擬圖</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</div>
								<div class="text-center" id="finsimu-div-admin" style="display:none;">
				                	<div class="btn-row">			    
				                		<input type="hidden" id="hidden_case_id" name="hidden_case_id" />                
					                    <button class="btn btn-primary" id="insert-simu-button">新增</button>
					                    <button class="btn btn-primary btn-bath" id="gen_d3js_button">產生模擬圖</button>
					                    <button class="btn btn-primary" id="switch-simu-button">產生模擬資料</button>
					                    <button class="btn btn-exec" onClick="location.reload()">回上頁</button>
				                    </div>
				                    <br>     				                
					                <div>
					                    <table id="finsimu-table-admin" class="result-table">
					                    	<thead>
					                    		<tr>
					                    			<th>資金動態日期</th>
					                    			<th>動態類別</th>
					                    			<th>實際/模擬</th>
					                    			<th>資金金額</th>
					                    			<th>資金動態類別</th>
					                    			<th>資金動態說明</th>
					                    			<th>策略因應說明</th>
					                    			<th>功能</th>
					                    		</tr>
					                    	</thead>
					                    	<tbody>
											</tbody>  
										</table> 
					                </div>
								</div>
							</div>
						</div>
			      	</c:if>
					
					<!--==================    財務評估工具 (使用者)    ==================-->
			       	<c:if test="${sessionScope.role==0}">
					    <div>
					        <div class="text-center" id="fincase-div-user">
				                <table id="fincase-table-user" class="result-table">
									<thead>
										<tr>
											<th>案件名稱</th>
											<th>案件產生日期</th>
											<th>財務計畫</th>
											<th>產生模擬圖</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
					        </div>   
					        <div class="text-center" id="finsimu-div-user" style="display:none;">
			                	<div class="btn-row">			    
			                		<input type="hidden" id="hidden_case_id" name="hidden_case_id" />                
				                    <button class="btn btn-primary" id="insert-simu-button">新增</button>
				                    <button class="btn btn-primary btn-bath" id="gen_d3js_button">產生模擬圖</button>
				                    <button class="btn btn-primary" id="switch-simu-button">產生模擬資料</button>
				                    <button class="btn btn-exec" onClick="location.reload()">回上頁</button>
			                    </div>
			                    <br>     				                
				                <div>
				                    <table id="finsimu-table-user" class="result-table">
				                    	<thead>
				                    		<tr>
				                    			<th>資金動態日期</th>
				                    			<th>動態類別</th>
				                    			<th>實際/模擬</th>
				                    			<th>資金金額</th>
				                    			<th>資金動態類別</th>
				                    			<th>資金動態說明</th>
				                    			<th>策略因應說明</th>
				                    			<th>功能</th>
				                    		</tr>
				                    	</thead>
				                    	<tbody>
										</tbody>  
									</table> 
				                </div>
					        </div>     
					    </div>   		
			      	</c:if>
		      	</section>
			</div>
			
			<div id="simuGraph">         
	      		<div class="content">
	      			<div id="newpic" style="margin-left: 50px;"></div>
	      			<div id="z" style="margin-left: 50px;"></div>
				</div>
			</div>
			
			<div id="bathtub">
	      		<div class="content">
	      			<div id="bath" style="margin-left: 50px;"></div>
				</div>
			</div>
			<!--==================    jquery-ui dialog (管理者)    ==================-->
			<c:if test="${sessionScope.role==1}">
				<!--對話窗樣式- 建立模型 -->
				<div id="dialog-form-create" title="建立模型" style="display:none">
					<form name="create-dialog-form-post" id="create-dialog-form-post">
						<fieldset>
							<table style="border-collapse: separate;border-spacing: 10px 20px;">
								<tr>
									<td><p>案件名稱：</p></td>
									<td><input type="text" id="case_name" name="case_name"></td>
									<td><p>期初資金：</p></td>
									<td><input type="text" id="amount" name="amount"></td>
								</tr>
								<tr>
									<td><p>資金安全餘額： </p></td>
									<td><input type="text" id="safety_money" name="safety_money"></td>
									<td></td>
									<td></td>
								</tr>
							</table>
						</fieldset>
					</form>
				</div>
			</c:if>
			
			<!--==================    jquery-ui dialog (使用者)    ==================-->
			<!--對話窗樣式-新增 -->
			<div id="dialog-form-insert" title="新增資料" style="display:none">
				<form name="insert-dialog-form-post" id="insert-dialog-form-post" style="display:inline">
					<fieldset>
						<table style="border-collapse: separate;border-spacing: 10px 20px;">
							<tbody>
								<tr>
									<td><p>資金動態日期：</p></td>
									<td>
							    		<input type="text" id="insert_f_date" name="f_date" class="input-date" ></td>
									<td><p>動態類別：</p></td>
									<td>
			                			<select id="insert_f_type" name="f_type">
							    			<option value="0">請選擇...</option>
							    			<option value="1">已發生</option>
											<option value="2">應收/應付</option>
							    		</select>
							    	</td>
								</tr>
								<tr>
									<td><p>實際/模擬：</p></td>
									<td>
			                			<select id="insert_action" name="action">
							    			<option value=true>實際</option>
											<option value=false>模擬</option>
							    		</select>
							    	</td>
									<td><p>資金金額：</p></td>
									<td><input type="text" id="insert_amount" name="amount"></td>
								</tr>
								<tr>
									<td><p>資金動態類別：</p></td>
									<td>
			                			<select id="insert_f_kind" name="f_kind">
							    			<option value="0">請選擇...</option>
							    			<option value="1">營業收入</option>
											<option value="2">業務支出</option>
							    			<option value="3">固定資產支出</option>
											<option value="4">管銷費用</option>
							    			<option value="5">薪資</option>
											<option value="6">研發費用</option>
							    			<option value="7">行銷費用</option>
							    			<option value="11">投資收入</option>
							    			<option value="12">投資支出</option>
							    			<option value="13">其他收入</option>
							    			<option value="14">其他支出</option>
							    		</select>
							    	</td>
									<td><p>資金動態說明：</p></td>
									<td><input type="text" id="insert_description" name="description"></td>
								</tr>
								<tr>
									<td><p>策略因應說明：</p></td>
									<td><input type="text" id="insert_strategy" name="strategy"></td>
									<td></td>
									<td><input type="hidden" id="insert_case_id" name="case_id"></td>
								</tr>
							</tbody>
						</table>	
					</fieldset>
				</form>
			</div>	
				
			<!--對話窗樣式-修改 -->
			<div id="dialog-form-update" title="修改資料" style="display:none">
				<form name="update-dialog-form-post" id="update-dialog-form-post">
					<fieldset>
						<table style="border-collapse: separate;border-spacing: 10px 20px;">
							<tbody>
								<tr>
									<td><p>資金動態日期：</p></td>
									<td><input type="text" id="edit_f_date" name="f_date" class="input-date"></td>
									<td><p>動態類別：</p></td>
									<td>
			                			<select id="edit_f_type" name="f_type">
							    			<option value="0">請選擇...</option>
							    			<option value="1">已發生</option>
											<option value="2">應收/應付</option>
							    		</select>
							    	</td>
								</tr>
								<tr>
									<td><p>實際/模擬：</p></td>
									<td>
			                			<select id="edit_action" name="action">
							    			<option value=true>實際</option>
											<option value=false>模擬</option>
							    		</select>
									</td>
									<td><p>資金金額：</p></td>
									<td><input type="text" id="edit_amount" name="amount"></td>
								</tr>
								<tr>
									<td><p>資金動態類別：</p></td>
									<td>
			                			<select id="edit_f_kind" name="f_kind">
							    			<option value="0">請選擇...</option>
							    			<option value="1">營業收入</option>
											<option value="2">業務支出</option>
							    			<option value="3">固定資產支出</option>
											<option value="4">管銷費用</option>
							    			<option value="5">薪資</option>
											<option value="6">研發費用</option>
							    			<option value="7">行銷費用</option>
							    			<option value="11">投資收入</option>
							    			<option value="12">投資支出</option>
							    			<option value="13">其他收入</option>
							    			<option value="14">其他支出</option>							    		</select>
							    	</td>
									<td><p>資金動態說明：</p></td>
									<td><input type="text" id="edit_description" name="description"></td>
								</tr>
								<tr>
									<td><p>策略因應說明：</p></td>
									<td><input type="text" id="edit_strategy" name="strategy"></td>
									<td><input type="hidden" id="edit_simulation_id" name="simulation_id"></td>
									<td><input type="hidden" id="edit_case_id" name="case_id"></td>
								</tr>
							</tbody>
						</table>	
					</fieldset>
				</form>
			</div>	
			
			<!--對話窗樣式-確認 -->
			<div id="dialog-confirm" title="確認刪除資料嗎?" style="display:none">
				<p>是否確認刪除該筆資料</p>
				<input id="delete_simu_id" type="hidden">
				<input id="delete_case_id" type="hidden">
			</div>	
			
			<!--對話窗樣式-產生模擬資料 -->
			<div id="dialog-gen-simu" title="產生模擬資料" style="display:none;">
				<form name="update-dialog-form-post" id="update-dialog-form-post">
					<fieldset>
	               		<input type="hidden" name="user_action" value="sim"/>
						<table class="text-center">
							<tbody>
								<tr>
						    		<td>產生比例:</td>
						    		<td colspan="2">
					                    <select id="degree" name="degree">
							    			<option value="1">1</option>
											<option value="2">2</option>
							    			<option value="3">3</option>
											<option value="4">4</option>
							    			<option value="5">5</option>
											<option value="6">6</option>
							    			<option value="7">7</option>
											<option value="8">8</option>
							    			<option value="9">9</option>
							    		</select> 
						    		</td>
						    	</tr>
						    	<tr>
						    		<td>舊模擬資料是否刪除:</td>
						    		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						    		<td>
						    			<input type="radio" name="blndel" id="delete_simu_yes" value="Y" checked/><label for="delete_simu_yes"><span class="form-label">是</span></label>
						    			<input type="radio" name="blndel" id="delete_simu_no" value="N" /><label for="delete_simu_no"><span class="form-label">否</span></label>
									</td>
						        </tr>
					        </tbody>
				        </table>    
					</fieldset>
				</form>		
			</div>
		</div>
	</div>
</div>

<script>
	function draw_bath(bath_data){
		//take a bath
		
		d3.select("#bath svg").remove();
		var margin = {top: 70, right: 80, bottom: 50, left: 80};
		var width = 960;
		var height = 450;
	//底圖大小
	//alert(new Date(d3.min(bath_data, function(d) { return d.FinanceDate; })));
		var svg = d3.select("#bath").append("svg")
			.attr("width", width + margin.left + margin.right)							
			.attr("height", height + margin.top + margin.bottom);
		
		//var a=(Math.round(d3.min(bath_data, function(d) { return d.FailureRate; })*10)*0.1);
		//var b=((1>Math.round(d3.max(bath_data, function(d) { return d.FailureRate; })*10) * 0.1 )?1:(Math.round(d3.max(bath_data, function(d) { return d.FailureRate; })*10)*0.1));

// 		var tmp=d3.max(bath_data, function(d) { return d.FailureRate; });
// 		alert(tmp);
// 		alert(d3.min(bath_data, function(d) { return d.FailureRate; }));
// 		if(tmp-d3.min(bath_data, function(d) { return d.FailureRate; })<0.5){
// 			tmp=Math.round(d3.min(bath_data, function(d) { return d.FailureRate; })+0.5);
// 		}
		var xScale = d3.time.scale()
			.domain([new Date(d3.min(bath_data, function(d) { return d.FinanceDate; })),new Date(d3.max(bath_data, function(d) { return d.FinanceDate; }))])
// 			.ticks(d3.time.month, 1)
			.range([0, width - margin.left - margin.right]);
		var yScale = d3.scale.linear()
// 			.domain([(1.2*Math.round(d3.min(bath_data, function(d) { return d.FailureRate; })*10)*0.1),tmp])
		    .domain([(1.2*Math.round(d3.min(bath_data, function(d) { return d.FailureRate; })*10)*0.1),((Math.round(1.2*d3.max(bath_data, function(d) { return d.FailureRate; })*10)*0.1))])
// 		    .domain([(1.2*Math.round(d3.min(bath_data, function(d) { return d.FailureRate; })*10)*0.1),((1>Math.round(d3.max(bath_data, function(d) { return d.FailureRate; })*10) * 0.1 )?1:(Math.round(1.2*d3.max(bath_data, function(d) { return d.FailureRate; })*10)*0.1))])
		    .range([height - margin.top - margin.bottom,0]);
		var xAxis;
		if(new Date(d3.max(bath_data, function(d) { return d.FinanceDate; }))-new Date(d3.min(bath_data, function(d) { return d.FinanceDate; }))>7776000000){
			xAxis = d3.svg.axis()
			    .scale(xScale)
			    .ticks(d3.time.month,1)
			    .orient("bottom")
			    .tickFormat(d3.time.format('%Y-%m-%d'));
		}else{
			xAxis = d3.svg.axis()
		    .scale(xScale)
		    .orient("bottom")
		    .tickFormat(d3.time.format('%Y-%m-%d'));
		}
		var yAxis = d3.svg.axis()
		    .scale(yScale)
		    .ticks(5)
		    .tickFormat(function(d){return d;})
		    .orient("left");
		
		svg.append("g")
			.append("text")
			.text("資金失效(浴盆曲線)圖")
			.attr("class","title")
			.attr({'fill':'#222','x':((width * 0.5) - (margin.right*1.5)) ,'y':(margin.top/2) })
			.style({'font-size':'32px','font-family':'Microsoft JhengHei'});
		
		 svg.append("g")
			.append('text')
			.text("時間")
			.attr('transform', 'translate('+(width-margin.right+20)+','+(height-margin.bottom)+')');
		  
		svg.append("g")
		   .attr("class","axis")
		   .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
		   .call(xAxis)
		   .selectAll("text")
		   .attr("transform", "rotate(25)")
		   .style("text-anchor", "start");
		
		svg.append("g")
		  .attr("class","axis")
		  .attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		  .call(yAxis)
		  .append('text')
// 		  .text("比例")
		  .attr('transform', 'translate(-10, -20)');
		
		//產生線條
		var lineGen = d3.svg.line()
			.x(function(data) {return xScale(new Date(data.FinanceDate));})
			.y(function(data) {return yScale(data.FailureRate);})
			.interpolate("basis");
		
		
		var minDate=(Math.random()>0.5?d3.max(bath_data, function(d) { return d.FinanceDate; }):d3.min(bath_data, function(d) { return d.FinanceDate; }));
		var minRate=(Math.random()>0.5?d3.max(bath_data, function(d) { return d.FailureRate; }):d3.min(bath_data, function(d) { return d.FailureRate; }));
		var new_vector=[];
		$.each (bath_data, function (i,item) {
			new_vector[i]={};
			new_vector[i].FinanceDate=minDate;
			new_vector[i].FailureRate=minRate;
		});
		
		svg.append('path')
		    .attr({
		      'd': lineGen(new_vector),
		      'stroke': '#09F',
		      'fill': 'none',
		      'stroke-width':'3'
		    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		    .transition()
		    .delay(300)
		    .duration(1500)
		    .attr({
		      'd': lineGen(bath_data),
		    });	
		
		//$("#tabs").tabs( "option", "active", 2 );
	}

	//###############################################
	//###############################################
	//############## 以上浴盆  #########################
	//###############################################
	//###############################################
	function genSimuGraph3(income_true, income_false, outcome_true, outcome_false, totalIncome, totalOutcome, balance){
		
		var tmp1='in:\n', tmp2='out:\n', tmp3='in_false:\n', tmp4='out_false:\n';
		var maxd='1900-01-01';
		var mind='2300-01-01';
		$.each (income_true, function (i,item) {
			tmp1+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
			mind=item.Date<mind?item.Date:mind;
			maxd=item.Date>maxd?item.Date:maxd;
		});
		$.each (income_false, function (i,item) {
			tmp3+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
			mind=item.Date<mind?item.Date:mind;
			maxd=item.Date>maxd?item.Date:maxd;
		});
		$.each (outcome_true, function (i,item) {
			tmp2+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
			mind=item.Date<mind?item.Date:mind;
			maxd=item.Date>maxd?item.Date:maxd;
		});
		$.each (outcome_false, function (i,item) {
			tmp4+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
			mind=item.Date<mind?item.Date:mind;
			maxd=item.Date>maxd?item.Date:maxd;
		});
		//income=income_true;
		//outcome=outcome_true;
		
		//var mind=(d3.min(income, function(d) { return d.Date; })<d3.min(outcome, function(d) { return d.Date; })?d3.min(income, function(d) { return d.Date; }):d3.min(outcome, function(d) { return d.Date; }));
		//(d3.min(income, function(d) { return d.Date; })>d3.min(outcome, function(d) { return d.Date; })?d3.min(income, function(d) { return d.Date; }):d3.min(outcome, function(d) { return d.Date; })),
		//var maxd=(d3.max(income, function(d) { return d.Date; })>d3.max(outcome, function(d) { return d.Date; })?d3.max(income, function(d) { return d.Date; }):d3.max(outcome, function(d) { return d.Date; }));
		d3.select("#newpic svg").remove();
		var red = balance;//(red_line(income,outcome));
		var margin = {top: 70, right: 80, bottom: 50, left: 80};
		var width = 960;
		var height = 450;
	//底圖大小
		var svg = d3.select("#newpic").append("svg")
		.attr("width", width + margin.left + margin.right)							
		.attr("height", height + margin.top + margin.bottom);
	//X軸Y軸 還有大加的Scaler
		var xScale = d3.time.scale()
	 					.domain([new Date(mind),new Date(maxd)])
	  				.range([0, width - margin.left - margin.right]);		
	  var yScale = d3.scale.linear()
					    .domain([0,160])
					    .range([height - margin.top - margin.bottom, 0]);
	  var xAxis = d3.svg.axis()
					    .scale(xScale)
					    .orient("bottom")
					    .tickFormat(d3.time.format('%Y-%m-%d'));
	//				    .ticks(d3.time.month, 1).ticks(d3.time.week, 2);
	
	  var yAxis = d3.svg.axis()
					    .scale(yScale)
					    //.tickFormat(d3.format(",%"))
					    .tickFormat(function(d){return d+'%';})
					    .orient("left");
	//title 和收入支出 X軸數
	  svg.append("g")
		.append("text")
		.text("財務損益平衡統計圖")
		.attr("class","title")
		.attr({'fill':'#222','x':((width * 0.5) - (margin.right*1.5)) ,'y':(margin.top/2) })
		.style({'font-size':'32px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("█ 總支出:"+totalOutcome[0].Total)
		.attr("class","title")
		.attr({'fill':'#9c0','x':(width-margin.right *0.5) ,'y':(margin.top+30) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("█ 總收入: "+totalIncome[0].Total)
		.attr("class","title")
		.attr({'fill':'#09F','x':(width-margin.right *0.5) ,'y':(margin.top) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("(虛線為模擬，實線為實際)")
		.attr("class","title")
		.attr({'fill':'#777','x':(width-margin.right *0.5-20) ,'y':(margin.top+60) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("安全餘額警示："+(balance=="None"?"　無資料　":balance))
		.attr("class","title")
		.attr({'fill':'#F00','x':(width-margin.right *0.5-18) ,'y':(margin.top+90) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	 svg.append("g")
		.append('text')
		.text("時間")
		.attr('transform', 'translate('+(width-margin.right+20)+','+(height-margin.bottom)+')');
	  
	svg.append("g")
	   .attr("class","axis")
	   .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
	   .call(xAxis)
	   .selectAll("text")
	   .attr("transform", "rotate(25)")
	   .style("text-anchor", "start");
	
	svg.append("g")
	  .attr("class","axis")
	  .attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
	  .call(yAxis)
	  .append('text')
	  .text("比例")
	  .attr('transform', 'translate(-10, -20)');
	
	//產生線條
	var lineGen = d3.svg.line()
		.x(function(data) {return xScale(new Date(data.Date));})
		.y(function(data) {return yScale(data.Percent);})
		.interpolate("line");
	//五條線
	if(income_true.length!=0){
		svg.append('path')
	    .attr({
	      'd': lineGen(zero_v(income_true)),
	      'stroke': '#09F',
	      'fill': 'none',
	      'stroke-width':'3'
	    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
	    .transition()
	    .delay(300)
	    .duration(1500)
	    .attr({
	      'd': lineGen(income_true),
	    });	
	}
	if(income_false.length!=0){
		svg.append('path')
		    .attr({
		      'd': lineGen(zero_v(income_false)),
		      'stroke': '#09c',
		      'fill': 'none',
		      'stroke-width':'3'
		    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		    .transition()
		    .delay(300)
		    .duration(1500)
		    .attr({
		      'd': lineGen(income_false),
		    }).style({'stroke-dasharray':'2'});
	}
	if(outcome_true.length!=0){
		svg.append('path')
			.attr({
			   'd': lineGen(zero_v(outcome_true)),
			   'stroke': '#9c0',
			   'fill': 'none',
			   'stroke-width':'3'
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.duration(1500)
			.attr({
			   'd': lineGen(outcome_true),
			});
	}
	if(outcome_false.length!=0){
		svg.append('path')
		    .attr({
		      'd': lineGen(zero_v(outcome_false)),
		      'stroke': '#9a0',
		      'fill': 'none',
		      'stroke-width':'3'
		    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		    .transition()
		    .delay(300)
		    .duration(1500)
		    .attr({
		      'd': lineGen(outcome_false),
		    }).style({'stroke-dasharray':'2'});
	}
	
	if(red.length>5){
		svg.append('path')
			.attr({
			   'd': lineGen([{"Date": red,"Percent": 0},{"Date": red,"Percent": 0}]),
			   'stroke': '#F00',
			   'fill': 'none',
			   'stroke-width':'3',
			   'stroke-dasharray' : '5,3',
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.duration(1500)
			.attr({
		  		'd': lineGen([{"Date": red,"Percent": 0},{"Date": red,"Percent": 160}]),
			});
	}

//四條線的tool圓圈圈
	svg.append("g").selectAll("circle")
		.data(income_true)
		.enter()
		.append("circle")
		.attr({
			"cx":function(d) {return xScale(new Date(d3.max(income_true, function(d) { return d.Date; })))+margin.right*3+10;},
			"cy":function(d) {return yScale(d3.mean(income_true,function(d){return d.Percent}));},
			"r" : 5,
			"fill": "#09F",//藍色
			"title" : function(d){return "日期: "+d.Date+"<br>收入: "+d.Amount+"<br>比例: "+Math.floor(d.Percent)+"%";},
			"class" : "tool"
		}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		.transition()
		.delay(700)
    	.duration(1500)
    	.attr({
    		"cx":function(d) {return xScale(new Date(d.Date));},
			"cy":function(d) {return yScale(d.Percent);}
   	});
	svg.append("g").selectAll("circle")
		.data(income_false)
		.enter()
		.append("circle")
		.attr({
			"cx":function(d) {return xScale(new Date(d3.max(income_false, function(d) { return d.Date; })))+margin.right*3+10;},
			"cy":function(d) {return yScale(d3.mean(income_false,function(d){return d.Percent}));},
			"r" : 4,
			"fill": "#09c",//藍色
			"title" : function(d){return "日期: "+d.Date+"<br>收入: "+d.Amount+"<br>比例: "+Math.floor(d.Percent)+"%";},
			"class" : "tool"
		}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		.transition()
		.delay(700)
    	.duration(1500)
    	.attr({
    		"cx":function(d) {return xScale(new Date(d.Date));},
			"cy":function(d) {return yScale(d.Percent);}
   	});
	svg.append("g").selectAll("circle")
		.data(outcome_true)
		.enter()
		.append("circle")
		.attr({    
//			"cx":function(d) {return xScale(new Date(d.Date));},
			"cx":function(d) {return xScale(new Date(d3.min(outcome_true,function(d){return d.Date})))-margin.left-10;},
			"cy":function(d) {return yScale(d3.mean(outcome_true,function(d){return d.Percent}));},
			"r" : 5,
			"fill": "#9c0",//綠色
			"title" : function(d){return "日期: "+d.Date+"<br>支出: "+Math.abs(d.Amount)+"<br>比例: "+Math.floor(d.Percent)+"%";},
			"class" : "tool"
		}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		.transition()
		.delay(500)
	  	.duration(1500)
	  	.attr({
		   	"cx":function(d) {return xScale(new Date(d.Date));},
			"cy":function(d) {return yScale(d.Percent);}
 		});
	
	  svg.append("g").selectAll("circle")
		.data(outcome_false)
		.enter()
		.append("circle")
		.attr({
			"cx":function(d) {return xScale(new Date(d3.max(outcome_false, function(d) { return d.Date; })))+margin.right*3+10;},
			"cy":function(d) {return yScale(d3.mean(outcome_false,function(d){return d.Percent}));},
			"r" : 4,
			"fill": "#9a0",//藍色
			"title" : function(d){return "日期: "+d.Date+"<br>收入: "+d.Amount+"<br>比例: "+Math.floor(d.Percent)+"%";},
			"class" : "tool"
		}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		.transition()
		.delay(700)
    	.duration(1500)
    	.attr({
    		"cx":function(d) {return xScale(new Date(d.Date));},
			"cy":function(d) {return yScale(d.Percent);}
   	});
		  
	  $(".tool").mouseover(function(e){
			 $(this).attr("newTitle",$(this).attr("title"));
			 $(this).attr("title","");
			 var tooltip = "<div id='tooltip'style='position:absolute;border:1px solid #333;background:#f7f5d1;padding:5px;color:#333;min-width:140px;display:none;'>"+ $(this).attr("newtitle") +"<\/div>";
			 $("body").append(tooltip);
			 $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show("fast");
			 }).mouseout(function(){
				 $(this).attr("title",$(this).attr("newTitle"));
			         $("#tooltip").remove();
			 }).mousemove(function(e){
			         $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"});
		});
	  
		$("#tabs").tabs( "option", "active", 1 );
	}
	
	//##################################################
	//############### 以上第三版 有四條線  #####################
	//############## 分實際和模擬 收入和支出 ###################
	//##### 以下第二版 Ben畫的可愛彈跳曲線 用百分比取代金額數值 #########
	//##################################################
	
	function red_line(income,outcome){
		if(income.length<1||outcome.length<1){return"X_X";}
		
		var i=0,j=0,amount,move=2;
		var bigger=outcome,smaller=income;
		
		if(income[0].Percent>outcome[0].Percent){
			bigger=income;
			smaller=outcome;
		};
		
		while(i<bigger.length && j< smaller.length){
			if(bigger[i].Percent<smaller[j].Percent){return (move?bigger[i].Date:smaller[j].Date);}
			if(i+1==bigger.length||j+1==smaller.length)break;
			//alert(bigger[i].Date+" "+bigger[i].Percent+"\n"+smaller[j].Date+" "+smaller[j].Percent);
			if(bigger[i+1].Date>smaller[j+1].Date){
				move=0;
				j++;
			}else{
				move=1;
				i++;
			}
		}
		
		return "X_X";
	}
	
	function zero_v(vector){
		var minDate=(Math.random()>0.5?d3.max(vector, function(d) { return d.Date; }):d3.min(vector, function(d) { return d.Date; }));
		var averageDate= vector[Math.floor(vector.length/2)].Date;
		var minAmount=d3.min(vector, function(d) { return d.Amount; });
		var minPercent=(Math.random()>0.5?d3.max(vector, function(d) { return d.Percent; }):d3.min(vector, function(d) { return d.Percent; }));
		var new_vector=[];
		
		$.each (vector, function (i,item) {
			//alert(minDate +" "+vector[i].Date + " "+(minDate + vector[i].Date));
			//tmp1+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
			new_vector[i]={};
			new_vector[i].Date=minDate;//(new Date(minDate)+new Date(vector[i].Date))/2;
			new_vector[i].Amount=minAmount;
			new_vector[i].Percent=minPercent;
		});
		
		return new_vector;
	}
	
	function genSimuGraph2(income, outcome, totalIncome, totalOutcome){
		var tmp1='in:\n', tmp2='out:\n', tmp3='', tmp4='';
		$.each (income, function (i,item) {
			tmp1+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
		});
		$.each (outcome, function (i,item) {
			tmp2+=i+" "+ item.Date+" "+item.Amount+" "+item.Percent+"\n";
		});
		var mind=(d3.min(income, function(d) { return d.Date; })<d3.min(outcome, function(d) { return d.Date; })?d3.min(income, function(d) { return d.Date; }):d3.min(outcome, function(d) { return d.Date; }));
		//(d3.min(income, function(d) { return d.Date; })>d3.min(outcome, function(d) { return d.Date; })?d3.min(income, function(d) { return d.Date; }):d3.min(outcome, function(d) { return d.Date; })),
		var maxd=(d3.max(income, function(d) { return d.Date; })>d3.max(outcome, function(d) { return d.Date; })?d3.max(income, function(d) { return d.Date; }):d3.max(outcome, function(d) { return d.Date; }));
		d3.select("svg").remove();
		var red=(red_line(income,outcome));
		
		var margin = {top: 70, right: 80, bottom: 50, left: 80};
		var width = 960;
		var height = 450;
	//底圖大小
		var svg = d3.select("#newpic").append("svg")
		.attr("width", width + margin.left + margin.right)							
		.attr("height", height + margin.top + margin.bottom);
	//X軸Y軸 還有大加的Scaler
		var xScale = d3.time.scale()
	 					.domain([new Date(mind),new Date(maxd)])
	  				.range([0, width - margin.left - margin.right]);		
	  var yScale = d3.scale.linear()
					    .domain([0,160])
					    .range([height - margin.top - margin.bottom, 0]);
	  var xAxis = d3.svg.axis()
					    .scale(xScale)
					    .orient("bottom")
					    .tickFormat(d3.time.format('%Y-%m-%d'));
	//				    .ticks(d3.time.month, 1).ticks(d3.time.week, 2);
	
	  var yAxis = d3.svg.axis()
					    .scale(yScale)
					    //.tickFormat(d3.format(",%"))
					    .tickFormat(function(d){return d+'%';})
					    .orient("left");
	//title 和收入支出 X軸數
	  svg.append("g")
		.append("text")
		.text("財務損益平衡統計圖")
		.attr("class","title")
		.attr({'fill':'#222','x':((width * 0.5) - (margin.right*1.5)) ,'y':(margin.top/2) })
		.style({'font-size':'32px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("█ 總支出:"+totalOutcome[0].Total)
		.attr("class","title")
		.attr({'fill':'#9c0','x':(width-margin.right *0.5) ,'y':(margin.top+30) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
		.append("text")
		.text("█ 總收入: "+totalIncome[0].Total)
		.attr("class","title")
		.attr({'fill':'#09F','x':(width-margin.right *0.5) ,'y':(margin.top) })
		.style({'font-size':'16px','font-family':'Microsoft JhengHei'});
	  
	  svg.append("g")
	  .append('text')
	  .text("時間")
	  .attr('transform', 'translate('+(width-margin.right+20)+','+(height-margin.bottom)+')');
	  
	  svg.append("g")
	  .attr("class","axis")
	  .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
	  .call(xAxis)
		.selectAll("text")
		.attr("transform", "rotate(25)")
		.style("text-anchor", "start");
	  
		svg.append("g")
	  .attr("class","axis")
	  .attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
	  .call(yAxis)
	  .append('text')
	  .text("比例")
	  .attr('transform', 'translate(-10, -20)');
	//產生線條
		var lineGen = d3.svg.line()
		.x(function(data) {return xScale(new Date(data.Date));})
	  .y(function(data) {return yScale(data.Percent);})
	  .interpolate("linear");
	//三條線
		svg.append('path')
	    .attr({
	      'd': lineGen(zero_v(income)),
	      'stroke': '#09F',
	      'fill': 'none',
	      'stroke-width':'3'
	    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
	    .transition()
	    .delay(300)
	    .duration(1500)
	    .attr({
	      'd': lineGen(income),
	    });
		  
		svg.append('path')
			.attr({
			   'd': lineGen(zero_v(outcome)),
			   'stroke': '#9c0',
			   'fill': 'none',
			   'stroke-width':'3'
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.duration(1500)
			.attr({
			   'd': lineGen(outcome),
			});
		  
		if(false && red.length>5){
			svg.append('path')
			.attr({
			   'd': lineGen([{"Date": red,"Percent": 0},{"Date": red,"Percent": 0}]),
			   'stroke': '#F00',
			   'fill': 'none',
			   'stroke-width':'2',
			   'stroke-dasharray' : '5,3',
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.duration(1500)
			.attr({
		  		'd': lineGen([{"Date": red,"Percent": 0},{"Date": red,"Percent": 100}]),
			});
		}
		  
	//兩條線的tool圓圈圈
		  svg.append("g").selectAll("circle")
			.data(outcome)
			.enter()
			.append("circle")
			.attr({    
	//			"cx":function(d) {return xScale(new Date(d.Date));},
				"cx":function(d) {return xScale(new Date(d3.min(outcome,function(d){return d.Date})))-margin.left-10;},
				"cy":function(d) {return yScale(d3.mean(outcome,function(d){return d.Percent}));},
				"r" : 5,
				"fill": "#9c0",//綠色
				"title" : function(d){return "日期: "+d.Date+"<br>支出: "+Math.abs(d.Amount)+"<br>比例: "+Math.floor(d.Percent)+"%";},
				"class" : "tool"
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.delay(500)
	  	.duration(1500)
	  	.attr({
	   		"cx":function(d) {return xScale(new Date(d.Date));},
				"cy":function(d) {return yScale(d.Percent);}
	 		});
	  svg.append("g").selectAll("circle")
			.data(income)
			.enter()
			.append("circle")
			.attr({
				"cx":function(d) {return xScale(new Date(d3.max(income, function(d) { return d.Date; })))+margin.right*3+10;},
				"cy":function(d) {return yScale(d3.mean(income,function(d){return d.Percent}));},
				"r" : 5,
				"fill": "#09F",//藍色
				"title" : function(d){return "日期: "+d.Date+"<br>收入: "+d.Amount+"<br>比例: "+Math.floor(d.Percent)+"%";},
				"class" : "tool"
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			.transition()
			.delay(700)
	    	.duration(1500)
	    	.attr({
	    		"cx":function(d) {return xScale(new Date(d.Date));},
				"cy":function(d) {return yScale(d.Percent);}
	   	});
	//tool顯示
	  $(".tool").mouseover(function(e){
	
			 $(this).attr("newTitle",$(this).attr("title"));
			 $(this).attr("title","");
			 var tooltip = "<div id='tooltip'style='position:absolute;border:1px solid #333;background:#f7f5d1;padding:5px;color:#333;min-width:140px;display:none;'>"+ $(this).attr("newtitle") +"<\/div>";
			 $("body").append(tooltip);
			 $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show("fast");
		 }).mouseout(function(){
			 $(this).attr("title",$(this).attr("newTitle"));
		         $("#tooltip").remove();
		 }).mousemove(function(e){
		         $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"});
		 });
	  
		$("#tabs").tabs( "option", "active", 1 );
	}
	function genSimuGraph(income, outlay, detailData, fincase, totalIncome, totalOutlay){
		
		d3.select("svg").remove();
		var outputDetail = [];
		var outputIncome = [];
		var outputOutlay = [];
		var outputAlarm = [];
		var firstDate;
		var endDate;
		var alarm = 0;
		var balance = 0;
		var maxAmount = 0;
	
		if(income.length>0){
	        var parseDate = d3.time.format("%Y-%m-%d").parse;
			alarm = Number(fincase[0].safety_money);
			balance = Number(fincase[0].Amount);
			//　取出Ｘ軸的最早日期及最晚日期
			firstDate = fincase[0].create_date;
			endDate = income[income.length-1].date;
			if (Date.parse(endDate) > Date.parse(outlay[outlay.length-1].date)){
				endDate = outlay[outlay.length-1].date;
			}
			// 如果第一筆收入日期大於期初日期，將期初資金及期初日期塞進 收入 的第一筆資料
			if (Date.parse(income[0].date) > Date.parse(fincase[0].create_date)){
				var firstIncome = "{\"date\":\"" + fincase[0].create_date + "\", \"pv\":\"" + fincase[0].Amount + "\"}";
				outputIncome.push(JSON.parse(firstIncome));
				outputDetail.push(JSON.parse(firstIncome));
				for(i=0;i<income.length;i++){
					var incomeData = "{\"date\":\"" + income[i].date + "\", \"pv\":\"" + income[i].pv + "\"}";
					outputIncome.push(JSON.parse(incomeData));
					outputDetail.push(JSON.parse(incomeData));
					if(Number(income[i].pv) > maxAmount){
						maxAmount = Number(income[i].pv);
					}
				}	
			} else {	
				var initial =  Number(fincase[0].Amount) + Number(income[0].pv);
				var firstIncome = "{\"date\":\"" + fincase[0].create_date + "\", \"pv\":\"" + initial + "\"}";
				outputIncome.push(JSON.parse(firstIncome));
				outputDetail.push(JSON.parse(firstIncome));
				if(initial > maxAmount){
						maxAmount = initial;
				}
			
				for(i=1;i<income.length;i++){
					var incomeData = "{\"date\":\"" + income[i].date + "\", \"pv\":\"" + income[i].pv + "\"}";
					outputIncome.push(JSON.parse(incomeData));
					outputDetail.push(JSON.parse(incomeData));
					if(Number(income[i].pv) > maxAmount){
						maxAmount = Number(income[i].pv);
					}
				}			
			}
					
			//以下處理支出資料
			for(j=0;j<outlay.length;j++){
				var outlayData = "{\"date\":\"" + outlay[j].date + "\", \"pv\":\"" + outlay[j].pv + "\"}";
				outputOutlay.push(JSON.parse(outlayData));
				outputDetail.push(JSON.parse(outlayData));
				if(Math.abs(Number(outlay[j].pv)) > maxAmount){
					maxAmount = Number(outlay[j].pv);
				}
			}	
			
			//以下處理安全警戒線
			for(k=0;k<detailData.length;k++){
				balance = Number(detailData[k].pv) + balance;
				if(balance<=alarm){
					var alarmData1 = "{\"date\":\"" + detailData[k].date + "\", \"pv\":\"" + 0 + "\"}";
					var alarmData2 = "{\"date\":\"" + detailData[k].date + "\", \"pv\":\"" + maxAmount + "\"}";
					outputAlarm.push(JSON.parse(alarmData1));
					outputAlarm.push(JSON.parse(alarmData2));	
					break;	
				}
			}	
		}
		CreateLines(outputIncome, outputOutlay, outputAlarm);
	
		function CreateLines(item1,item2,item3){
		    var dataset = [];
		    var lines = [];//保存折线图对象
		    var lineNames=["收入","支出","資金安全餘額"];//保存系列名称
		    var lineColor = ["#0F0","#09F","#F00"];
		    var currentLineNum = 0;
		    var maxdata;//y轴最大值
		    var margin = {top: 20, right: 80, bottom: 50, left: 80};
		    var width = 960;
		    var height = 450;
	
		    getData();
		    currentLineNum = dataset.length;
		    maxdata = getMaxData(dataset);
		    getDetailData(outputDetail);
		    
		    var svg = d3.select("#z").append("svg")
		    .attr("width", width + margin.left + margin.right)							
		    .attr("height", height + margin.top + margin.bottom);
	
		    var xScale = d3.time.scale()
	//	    				.domain([firstDate, endDate])
		   					.domain([d3.min(outputDetail, function(d) { return d.date; }),d3.max(outputDetail, function(d) { return d.date; })])
		    				.range([0, width - margin.left - margin.right]);		
		    var yScale = d3.scale.linear()
						    .domain([0,maxdata])
						    .range([height - margin.top - margin.bottom, 0]);
		    var xAxis = d3.svg.axis()
						    .scale(xScale)
						    .orient("bottom")
						    .tickFormat(d3.time.format('%Y-%m-%d'));				  
		    var yAxis = d3.svg.axis()
						    .scale(yScale)
						    .orient("left");
		    
		    svg.append("g")
		        .attr("class","axis")
		        .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
		        .call(xAxis)
				.selectAll("text")
				.attr("transform", "rotate(65)")
				.style("text-anchor", "start")
		        .append('text')
		        .text("時間")
		        .attr('transform', 'translate(' + (width - margin.left - margin.right-20) + ', 0)');
		        
		    svg.append("g")
		        .attr("class","axis")
		        .attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		        .call(yAxis)
		        .append('text')
		        .text("金額");
		    
		    svg.append('g').selectAll('text')
		                 .data(dataset).enter().append('text')
		                 .attr({'x': (width - margin.right) ,'y':30 }).text("總收入："+totalIncome)
		                 .attr("fill", "#0F0")
		                 .attr("font-size", "16px");
		    
		    svg.append('g').selectAll('text')
		                 .data(dataset).enter().append('text')
		                 .attr({'x':(width - margin.right) ,'y':60 }).text("總支出："+totalOutlay)
		                 .attr("fill", "#09F")
		                 .attr("font-size", "16px");
		    
		    svg.append('g').selectAll('text')
		                 .data(dataset).enter().append('text')
		                 .attr({'x':(width - margin.right) ,'y':90 }).text("資金安全餘額："+ alarm)
		                 .attr("fill", "#F00")
		                 .attr("font-size", "16px");
		    addlegend();
	
		    for(i=0;i<currentLineNum;i++){
		        var newLine=new CreateLineObject();
		        newLine.init(i);
		        lines.push(newLine);		
		    }
		    
		    function CreateLineObject(){
	
		        this.init = function(id){
		        	if(id<2){
			            var arr = dataset[id];
			            xScale = d3.time.scale()
			                .domain([d3.min(outputDetail, function(d) { return d.date; }),d3.max(outputDetail, function(d) { return d.date; })])
			                .range([0, width - margin.left - margin.right]);	        
			            var line = d3.svg.line()
							            .x(function(d) { return xScale(d.date); })
							            .y(function(d) { return yScale(d.pv); })
							            .interpolate('linear');
			            svg.append("path")
			                .datum(arr)
			                .attr("transform","translate(" + margin.left + "," +  margin.top  + ")")
			                .attr("class", "line")
			                .transition()
			                .ease("elastic")
			                .duration(1000)
			                .delay(function(d,j){
			                return 200*j;
			            	})
			                .style("stroke",lineColor[id])
			                .attr("d", line)
			            ;
			            svg.append('g').selectAll('circle')
			                .data(arr)
			                .enter() 
			                .append('circle')
			                .on('mouseover', function(d) {
			                d3.select(this).transition().duration(500).attr('r', 5);
			                d3.select('.tips').style('display', 'block');
			                var tx = parseFloat(d3.select(this).attr("cx"));
			                var ty = parseFloat(d3.select(this).attr("cy"));	
			                var tipRectx = tx+60+180>width?tx+10-180:tx+60,
			                    tipRecty= ty+20+60>height?ty+10-60:ty+20;
			                var theDate = d3.time.format('%Y-%m-%d')(d.date);
			                var thePv= d.pv;
			                var tips = svg.append("g")
			                .attr("id","tips");
			                var tipRect = tips.append("rect")
			                .attr("x",tipRectx)
			                .attr("y",tipRecty)
			                .attr("width",180)
			                .attr("height",60)
			                .attr("fill","#FFF")
			                .attr("stroke","#CCC")
			                var tipText = tips.append("text")
			                .attr("class","tiptools")
			                .text("日期: "+theDate)
			                .attr("x",tipRectx+20)
			                .attr("y",tipRecty+20);
			                var tipText = tips.append("text")
			                .attr("class","tiptools")
			                .text("金額: "+thePv)
			                .attr("x",tipRectx+20)
			                .attr("y",tipRecty+50);
			            })
			                .on('mouseout', function() {
			                d3.select(this).transition().duration(500).attr('r', 3.5);
			                d3.select('.tips').style('display', 'none');
			                d3.select("#tips").remove();
			            })
			                .transition()
			                .ease("elastic")
			                .duration(1000)
			                .delay(function(d,j){
			                return 200*j;
			            })
			                .attr("transform","translate(" + margin.left + "," +  margin.top  + ")")
			                .attr('cx', line.x())
			                .attr('cy', line.y())
			                .attr('r', 5)
			                .attr("fill",lineColor[id]);
			        } else {
			            var arr = dataset[id];
			            var arr1 = dataset[1];
			            xScale = d3.time.scale()
			                .domain([d3.min(outputDetail, function(d) { return d.date; }),d3.max(outputDetail, function(d) { return d.date; })])
			                .range([0, width - margin.left - margin.right]);	        
			            var line = d3.svg.line()
			            .x(function(d) { return xScale(d.date); })
			            .y(function(d) { return yScale(d.pv); })
			            .interpolate('linear');
			            
			            svg.append("path")
			                .datum(arr)
			                .attr("transform","translate(" + margin.left + "," +  margin.top  + ")")
			                .attr("class", "line")
			                .transition()
			                .ease("elastic")
			                .duration(1000)
			                .delay(function(d,j){
			                return 200*j;
			            	})
			                .style("stroke",lineColor[id])
			                .attr("d", line);
			            
			            svg.append('g').selectAll('circle')
			                .data(arr)
			                .enter() 
			                .append('circle')
			                .on('mouseover', function(d) {
				                d3.select(this).transition().duration(500).attr('r', 5);
				                d3.select('.tips').style('display', 'block');
				                var tx = parseFloat(d3.select(this).attr("cx"));
				                var ty = parseFloat(d3.select(this).attr("cy"));	
				                var tipRectx = tx+60+180>width?tx+10-180:tx+60,
				                    tipRecty= ty+20+60>height?ty+10-60:ty+20;
				                var theDate = d3.time.format('%Y-%m-%d')(d.date);
				                var thePv= d.pv;
				                var tips = svg.append("g")
				                .attr("id","tips");
				                var tipRect = tips.append("rect")
				                .attr("x",tipRectx)
				                .attr("y",tipRecty)
				                .attr("width",180)
				                .attr("height",30)
				                .attr("fill","#FFF")
				                .attr("stroke","#CCC")
				                var tipText = tips.append("text")
				                .attr("class","tiptools")
				                .text("日期: "+theDate)
				                .attr("x",tipRectx+20)
				                .attr("y",tipRecty+20);
				            })
			                .on('mouseout', function() {
				                d3.select(this).transition().duration(500).attr('r', 3.5);
				                d3.select('.tips').style('display', 'none');
				                d3.select("#tips").remove();
				            })
			                .transition()
			                .ease("elastic")
			                .duration(1000)
			                .delay(function(d,j){
				                return 200*j;
				            })
			                .attr("transform","translate(" + margin.left + "," +  margin.top  + ")")
			                .attr('cx', line.x())
			                .attr('cy', line.y())
			                .attr('r', 5)
			                .attr("fill",lineColor[id]);
			        }
		        }
		    }
	
		    function getData(){
		        dataset.push(item1);
		        dataset.push(item2);
		        if(item3.length>0){
		        	dataset.push(item3);
		        }
		    }
		    
		    function getMaxData(arr){
		        var maxdata = 0;
		        var parseDate = d3.time.format("%Y-%m-%d").parse;
		        for(var i =0;i<arr.length;i++){
		            arr[i].forEach(function(d) {
		    			d.date = parseDate(d.date);
		                d.pv = + d.pv;
		        	});
		            dataset[i]=arr[i];
		            maxdata = d3.max([maxdata,d3.max(arr[i], function(d) { return d.pv; })]);
		        }
		        return maxdata;
		    }
		    
		    function getDetailData(detailArr){
		        var parseDate = d3.time.format("%Y-%m-%d").parse;
		        detailArr.forEach(function(d) {
		    		d.date = parseDate(d.date);
		            d.pv = + d.pv;
		        });
		        outputDetail=detailArr;
		    }
		    
		    function addlegend(){
	
		        var legend = svg.append('g');
		        legend.selectAll("text")
		            .data(lineNames)
		            .enter()
		            .append("text")
		            .text(function(d){return d;})
		            .attr("class","legend")
		            .attr("x", function(d,i) {return i*100;}) 
		            .attr("y",0)
		            .attr("fill",function(d,i){ return lineColor[i];});
	
		        legend.selectAll("rect")
		            .data(lineNames)
		            .enter()
		            .append("rect")
		            .attr("x", function(d,i) {return i*100-20;}) 
		            .attr("y",-10)
		            .attr("width",12)
		            .attr("height",12)
		            .attr("fill",function(d,i){ return lineColor[i];});
		        
		        legend.attr("transform","translate("+((width-lineNames.length*100)/2)+","+(height+50)+")");	
		    }
		}
		
		$("#tabs").tabs( "option", "active", 1 );
	}
</script>

<jsp:include page="footer.jsp" flush="true"/>