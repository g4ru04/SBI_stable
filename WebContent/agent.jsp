<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/jquery.dataTables.min.css" />
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<script>
$(function(){
	
	var validator_insert = $("#insert-dialog-form-post").validate({
		rules : {
			agent_name : {
				required : true
			}
		}
	});
	var validator_update = $("#update-dialog-form-post").validate({
		rules : {
			agent_name : {
				required : true
			}
		}
	});	
	
	$("#btn_query").click(function(e) {
		
		$("#table_agent").dataTable().fnDestroy();
		$("#table_agent").show();
		 
		table = $("#table_agent").DataTable({
			dom: 'lfrB<t>ip',
			paging: true,
			ordering: false,
			info: false,
			language: {"url": "js/dataTables_zh-tw.txt"},
			ajax: {
				url : "agent.do",
				dataSrc: "",
				type : "POST",
				data : {
					action : "search",
					agent_name : $("#search_agent_name").val()
				}
			},
			columnDefs: [	        					
				{
				   targets: -1,
				   searchable: false,
				   orderable: false,
				   render: function ( data, type, row ) {
					   var ch =
							"<div class='table-row-func btn-in-table btn-gray'><i class='fa fa-ellipsis-h'></i>"+
							"	<div class='table-function-list' >"+
							"		<button class='btn-update btn-in-table btn-green' title='修改' id = '" + row.agent_id + "'>" +
									"<i class='fa fa-pencil'></i></button>"+
							"		<button class='btn-delete btn-in-table btn-orange' title='刪除' id = '" + row.agent_id + "'>" +
							"<i class='fa fa-trash'></i></button>"+
							"	</div>"+
							"</div>";
					   
					   return ch;
				   }
				}				        
			],
			columns: [
				{"data": "agent_name" ,"defaultContent":""},
				{"data": "web_site" ,"defaultContent":""},
				{"data": "region_code" ,"defaultContent":""},
				{"data": "contact_mail" ,"defaultContent":""},
				{"data": "contact_phone" ,"defaultContent":""},
				{"data": "seed" ,"defaultContent":""},
				{"data": null ,"defaultContent":""}
			]				      	      
		});
		
		$("#table_agent").delegate(".btn-update", "click", function(e) {
			e.preventDefault();
			
			var row = jQuery(this).closest('tr');
			var data = $("#table_agent").dataTable().fnGetData(row);
		    
			$("#dialog-form-update input[name='agent_name']").val( data.agent_name );
			$("#dialog-form-update input[name='web_site']").val( data.web_site );
			$("#dialog-form-update input[name='region_code']").val( data.region_code );
			$("#dialog-form-update input[name='contact_mail']").val( data.contact_mail );
			$("#dialog-form-update input[name='contact_phone']").val( data.contact_phone );
			$("#dialog-form-update input[name='seed']").val( data.seed );
			
			update_dialog
				.data("agent_id", data.agent_id)
				.dialog("open");
		});
		
		$("#table_agent").delegate(".btn-delete", "click", function(e) {
			e.preventDefault();
			
			var row = jQuery(this).closest('tr');
			var data = $("#table_agent").dataTable().fnGetData(row);
			
			$("#delete_agent_name").html( data.agent_name );
			
			del_dialog
				.data("agent_id", data.agent_id)
				.dialog("open");
		});
	});
	
	$("#btn_insert_agent").click(function(e) {
		e.preventDefault();		
		insert_dialog.dialog("open");
	});
	
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
					$.ajax({
						type : "POST",
						url : "agent.do",
						data : {
							action : "insert",
							agent_name: $("#insert_agent_name").val(),
							web_site: $("#insert_web_site").val(),
							region_code: $("#insert_region_code").val(),
							contact_mail: $("#insert_contact_mail").val(),
							contact_phone: $("#insert_contact_phone").val(),
							seed: $("#insert_seed").val()
						},
						success : function(result) {
							var json_obj = $.parseJSON(result);
							$("#btn_query").trigger('click');
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
		} ],
		close : function() {
			validator_insert.resetForm();
			$("#insert-dialog-form-post").trigger("reset");
			insert_dialog.dialog("close");
		}
	});
	
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
					$.ajax({
						type : "POST",
						url : "agent.do",
						data : {
 							action : "update",
 							agent_id: $(this).data("agent_id"),
 							agent_name: $("#update_agent_name").val(),
 							web_site : $("#update_web_site").val(),
 							region_code: $("#update_region_code").val(),
 							contact_mail: $("#update_contact_mail").val(),
 							contact_phone : $("#update_contact_phone").val(),
 							seed : $("#update_seed").val()
						},
						success : function(result) {
							var json_obj = $.parseJSON(result);
							$("#btn_query").trigger('click');
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
		} ],
		close : function() {
			$("#update-dialog-form-post").trigger("reset");
			validator_update.resetForm();
			update_dialog.dialog("close");
		}
	});
	
	del_dialog = $("#dialog-form-delete").dialog({
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
		buttons : [{
			id : "delete",
			text : "確認刪除",
			click : function() {
				$.ajax({
					type : "POST",
					url : "agent.do",
					data : {
						action: "delete",
						agent_id: $(this).data("agent_id")
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						$("#btn_query").trigger('click');
					}
				});
				$(this).dialog("close");
			}
		}, {
			text : "取消",
			click : function() {
				$(this).dialog("close");
			}
		} ],
		close : function() {
			$(this).dialog("close");
		}
	});
	
	$("#search_agent_name").autocomplete({
	     minLength: 1,
	     source: function (request, response) {
	         $.ajax({
	             url : "agent.do",
	             type : "POST",
	             cache : false,
	             delay : 1500,
	             data : {
	             	action : "autocomplete_name",
	             	term : request.term
	             },
	             success: function(data) {
	             	var json_obj = $.parseJSON(data);
	             	response($.map(json_obj, function (item) {
						return {
							label: item.agent_name,
		                    value: item.agent_name
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
	             $(this).attr("placeholder","請輸入查詢通路商名稱");
	          }
	     },
	     response: function(e, ui) {
	         if (ui.content.length == 0) {
	             $(this).attr("placeholder","請輸入查詢通路商名稱");
	         }
	     }           
	 });
})
</script>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">通路商管理</h2>
		<div class="input-field-wrap">
			<div class="form-wrap">
				<div class="form-row">
					<label for="">
						<span class="block-label">通路商查詢</span>
						<input type="text" id="search_agent_name" placeholder="請輸入查詢通路商名稱">
					</label>
					<a href="#" id="btn_query" class="btn btn-darkblue">查詢</a>
				</div>
				<div class="btn-row">
					<a href="#" id="btn_insert_agent" class="btn btn-exec btn-wide">新增通路商</a>
				</div>
			</div><!-- /.form-wrap -->
		</div><!-- /.input-field-wrap -->
		
		<div class="search-result-wrap">
			<div class="result-table-wrap">
				<table id="table_agent" class="result-table">
					<thead>
						<tr>
							<th>通路商名稱</th>
							<th>網址</th>
							<th>區域碼</th>
							<th>聯絡人Email</th>
							<th>聯絡人電話</th>
							<th>加密因子</th>
							<th>功能</th>
						</tr>
					</thead>
					<tbody style="text-align:center">
					</tbody>
				</table>
			</div>
		</div>
		
		<!--對話窗樣式-新增 -->
		<div id="dialog-form-insert" title="新增資料" style="display:none">
			<form name="insert-dialog-form-post" id="insert-dialog-form-post" style="display:inline">
				<table style="border-collapse: separate;border-spacing: 10px 20px;">
					<tbody>
						<tr>
							<td><p>通路商名稱：</p></td>
							<td><input type="text" id="insert_agent_name" name="agent_name" ></td>
							<td><p>網址：</p></td>
							<td><input type="text" id="insert_web_site" name="web_site" ></td>
						</tr>
						<tr>
							<td><p>區域碼：</p></td>
							<td><input type="text" id="insert_region_code" name="region_code" ></td>
							<td><p>聯絡人Email：</p></td>
							<td><input type="text" id="insert_contact_mail" name="contact_mail" ></td>
						</tr>
						<tr>
							<td><p>聯絡人電話：</p></td>
							<td><input type="text" id="insert_contact_phone" name="contact_phone" ></td>
							<td><p>加密因子：</p></td>
							<td><input type="text" id="insert_seed" name="seed" ></td>
						</tr>
					</tbody>
				</table>	
			</form>
		</div>	
			
		<!--對話窗樣式-修改 -->
		<div id="dialog-form-update" title="修改資料" style="display:none">
			<form name="update-dialog-form-post" id="update-dialog-form-post">
				<table style="border-collapse: separate;border-spacing: 10px 20px;">
					<tbody>
						<tr>
							<td><p>通路商名稱：</p></td>
							<td><input type="text" id="update_agent_name" name="agent_name" ></td>
							<td><p>網址：</p></td>
							<td><input type="text" id="update_web_site" name="web_site" ></td>
						</tr>
						<tr>
							<td><p>區域碼：</p></td>
							<td><input type="text" id="update_region_code" name="region_code" ></td>
							<td><p>聯絡人Email：</p></td>
							<td><input type="text" id="update_contact_mail" name="contact_mail" ></td>
						</tr>
						<tr>
							<td><p>聯絡人電話：</p></td>
							<td><input type="text" id="update_contact_phone" name="contact_phone" ></td>
							<td><p>加密因子：</p></td>
							<td><input type="text" id="update_seed" name="seed" ></td>
						</tr>
					</tbody>
				</table>	
			</form>
		</div>	
		
		<!--對話窗樣式-刪除 -->
		<div id="dialog-form-delete" title="確認刪除此資料?" style="display:none">
			<form name="delete-dialog-form-post" id="delete-dialog-form-post" style="display:inline">
				<p>是否確認刪除：</p>
				<div style="text-align:center">
					<label id="delete_agent_name"></label>
				</div>
			</form>
		</div>	
	</div>
<jsp:include page="footer.jsp" flush="true"/>