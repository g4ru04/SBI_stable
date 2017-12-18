<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../../import.jsp" flush="true"/>

<!-- Datatable CSS -->
<link rel="stylesheet" href="../../css/jquery.dataTables.min.css" />
<link rel="stylesheet" href="../../css/buttons.dataTables.min.css"/>

<!-- Datatable -->
<script type="text/javascript" src="../../js/jquery-ui.min.js"></script>
<script type="text/javascript" src="../../js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../../js/dataTables.buttons.min.js"></script>

<!-- Datatable validation -->
<script type="text/javascript" src="../../js/jquery.validate.min.js"></script>
<script type="text/javascript" src="../../js/additional-methods.min.js"></script>
<script type="text/javascript" src="../../js/messages_zh_TW.min.js"></script>
<style>
	#epaper-backstage-table td {
	    text-align: center; /* center checkbox horizontally */
	    vertical-align: middle; /* center checkbox vertically */
	}
	#epaper-backstage-table th {
	    height: 32px;
	}
	
</style>
<jsp:include page="../../header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">電子報訂閱後台管理</h2>	
		<div id="mainDiv">
			<!-- 查詢 -->
			<div class="input-field-wrap">
				<div class="form-wrap">			
					<div class="form-row">
						<label for="">
							<span class="block-label">人員名稱、email</span>
							<input type="text" id="search-epaper-keyword">
						</label>
					</div>
					<div class="form-row">
						<button class="btn btn-darkblue" id="search-epaper-backstage">查詢</button>
					</div>										
				</div>
			</div>
	
			<div class="row search-result-wrap" align="center" >
				<div class="ui-widget">
					<table id="epaper-backstage-table" class="result-table">
						<thead></thead>
						<tfoot></tfoot>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div id='my_dialog'></div>
<script>
$(function(){
	$("#search-epaper-keyword").keyup(function(event){
	    if(event.keyCode == 13){
	    	$("#search-epaper-backstage").trigger("click");
	    }
	});
	$("#search-epaper-backstage").click(function(){
		rebuildTable();
		drawDataTable();
	});
	$("#epaper-backstage-table").delegate(".btn_update", "click", function(e) {
		var name = $(this).attr("name");
		var email = $(this).attr("email");
		var occupation = $(this).attr("occupation");
		
		if($(this).attr("reg")=="1"){
			warningMsg("系統提示","此用戶目前已訂閱!!");
		}else if(email.length==0){
			warningMsg("系統提示","此用戶無信箱資料，無法發送電子報!!");
		}else if(email.indexOf('@')==-1){
			warningMsg("系統提示","此用戶信箱格式異常，無法發送電子報!!");
		}else{
			function register_epaper(){
				$.ajax({
					type : "POST",
					url : "../../registerEpaper.do",
					data : {
						action : "insertNewRegister",
						nickname : name,
						email : email,
						occupation : occupation,
						backstage : true
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						if (json_obj.strMessage == "success"){
							rebuildTable();
							drawDataTable(email);
							warningMsg("系統提示","已開啟 '"+name+"' 對 SBI每日產業重點新聞電子報 之訂閱!!");
						}
						
					}
				});
			}
			$("#my_dialog").html("此動作會將 '"+name+"' 加入<br><b>\"SBI每日產業重點新聞電子報\"</b>之發送名單內<br><br>(將寄至: "+email+")");
			$("#my_dialog").attr("title","增加訂閱電子報");
			setup_dialog(register_epaper);
		}
	});
	$("#epaper-backstage-table").delegate(".btn_delete", "click", function(e) {
		var name = $(this).attr("name");
		var email = $(this).attr("email");
		if($(this).attr("reg")=="0"){
			warningMsg("系統提示","此用戶未訂閱，無法取消!!");
		}else{
			function register_epaper(){
				$.ajax({
					type : "POST",
					url : "../../registerEpaper.do",
					data : {
						action : "cancelRegister",
						email : email,
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						if (json_obj.strMessage == "cancelSuccess"){
							rebuildTable();
							drawDataTable(email);
							warningMsg("系統提示","已取消對 '"+email+"' 發送SBI每日產業重點新聞電子報!!");
						}
						
					}
				});
			}
			$("#my_dialog").html("此動作會取消 '"+name+"' 對<br><b>\"SBI每日產業重點新聞電子報\"</b> 之訂閱");
			$("#my_dialog").attr("title","取消訂閱電子報");
			setup_dialog(register_epaper);
		}
	});
});

	var oColumnDefs =
    [
    	{
            targets: 4,
            render: function ( data, type, row ) {
            	var source_change ={"sys_user":"使用系統","epaper_order":"申請寄送電子報","both":"使用系統及電子報"}
            	
            	return source_change[row.source];
            }
        },{
            targets: 5,
            render: function ( data, type, row ) {
            	var is_order_change ={"0":"X","1":"<b>O</b>"}
            	
            	return is_order_change[row.is_order];
            }
        },{
			targets: -1,
		   	searchable: false,
		   	orderable: false,
		   	render: function ( data, type, row ) {
		   		var options =
		   			"<div class='table-row-func btn-in-table btn-gray'><i class='fa fa-ellipsis-h'></i>"+
					"	<div class='table-function-list' >"+
					"		<button class='btn-in-table btn-darkblue btn_update "+row.source+" ' reg='"+row.is_order+"' title='訂閱' name = '" + (row.sys_name==""?row.order_name:row.sys_name) + "' email = '" + row.e_mail + "' occupation = '" + row.occupation + "'>" +
					"		<i class='fa fa-check-square-o'></i></button>"+
					"		<button class='btn-in-table btn-alert btn_delete "+row.source+" ' reg='"+row.is_order+"' title='取消訂閱' name = '" + (row.sys_name==""?row.order_name:row.sys_name) + "' email = '" + row.e_mail + "' occupation = '" + row.occupation + "'>" +
					"		<i class='fa fa-square-o'></i></button>"+
					"	</div>"+
					"</div>";
		 		return options;
		   }
		}				        
    ];
	var oColumns = [
		{"data": "sys_name" ,"defaultContent":""},
		{"data": "order_name" ,"defaultContent":""},
		{"data": "e_mail" ,"defaultContent":""},
		{"data": "occupation" ,"defaultContent":""},
		{"data": "source" ,"defaultContent":""},
		{"data": "is_order" ,"defaultContent":""},
		{"data": null ,"defaultContent":""}
	];
	var tableThs =
		"<th>系統使用名稱</th>" +
		"<th>訂閱電子報暱稱</th>" +
		"<th>Email</th>" +
		"<th>職業</th>" +
		"<th>使用情形</th>" + //目前SBI系統之使用者 & 非使用者
		"<th>目前訂閱狀態</th>" + //訂閱中 & 並無訂閱
		"<th>功能</th>" ;	
	function drawDataTable(special_search){
		console.log("drawDataTable start");
		dataTableObj = 
			$("#epaper-backstage-table").DataTable({
				dom: "lfr<t>ip",
				destroy: true,
// 				dataSrc: 'list',
				"order": [[ 4, "desc" ]],
				language: {"url": "js/dataTables_zh-tw.txt","emptyTable": "<br><b>查 無 結 果</b	><br>　"},
				ajax: {
						url : "../../registerEpaper.do",
						dataSrc: "lstResult",
						type : "POST",
						data : {
							action : "selectWithKeyword",
							keyword : (special_search!=null?special_search:$("#search-epaper-keyword").val()),
						}
				},
		        columnDefs: oColumnDefs,
				columns: oColumns
			});	
	}
	
	function rebuildTable(){
		var table = document.getElementById("epaper-backstage-table");
		$(table).find("thead").find("tr").remove();
		$(table).find("thead").append($("<tr></tr>").val("").html(tableThs));
		
		$(table).find("tfoot").find('tr').remove();
		$(table).find("tfoot").append($("<tr></tr>").val("").html(tableThs));	
	}
	
	function setup_dialog(func){
		var dialog_element = $("#my_dialog");
		dialog_element.dialog({
			title: dialog_element.attr("title"),
			draggable : true,
			resizable : false,
			autoOpen : true,
			height : "auto",
			width : "auto",
			modal : true,
			buttons : [{
				text: "確定", 
				click: function() {
					if(func!=null){
						func();
					}
					$(this).dialog("close");
				}
			},{
				text: "取消", 
				click: function() {
					$(this).dialog("close");
				}
			}]
		});
	}
</script>
<jsp:include page="../../footer.jsp" flush="true"/>
