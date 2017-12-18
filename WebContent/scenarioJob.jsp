<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF8">

	<link rel="Shortcut Icon" type="image/x-icon" href="./images/cdri-logo.gif" />

	<link rel="stylesheet" href="css/jquery-ui.min.css">
	<link rel="stylesheet" href="css/font-awesome.min.css">
	<link rel="stylesheet" href="css/styles.css">
<!-- 	<link rel="stylesheet" href="./css/my_modal.css" type="text/css" media="screen" /> -->
	<script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="js/additional-methods.min.js"></script>
	<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
	<script type="text/javascript" src="js/common.js"></script>
	<script type="text/javascript" src="js/my_modal.js"></script>
	<script type="text/javascript" src="js/jquery.poptrox.min.js"></script>
	
<%
	String group_id = (String) session.getAttribute("group_id");
	String user_id = (String) session.getAttribute("user_id");
	Integer role = (Integer) session.getAttribute("role");
	String menu = (String) request.getSession().getAttribute("menu"); 
	String privilege = (String) request.getSession().getAttribute("privilege"); 
%>
<title>工作流程管理</title>
<style>
#tbl_main a.btn{
	word-break: keep-all;
}
.bentable th{
	text-align:center;
}
.bentable td{
	padding:5px 15px;
	word-break: keep-all;
	text-align:left;
	font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
}
.bentable td:nth-child(1){
	text-align:right;
}
.bentable td:nth-child(2){
	font-size:16px;
	word-break: normal;
	text-align:left;
	color: red;
}
.bentable.nobreak td:nth-child(2){
	word-break: keep-all;
}
.bentable td:nth-child(4){
	word-break: break-all;
	max-width:600px;
}
.ui-dialog-content{
	font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
}
div.txt-justify{
	 padding:0 5px;
	 text-align: justify;
	　text-justify: inter-ideograph;
	　-ms-text-justify: inter-ideograph; /*IE9*/
	　-moz-text-align-last:justify; /*Firefox*/
	　-webkit-text-align-last:justify; /*Chrome*/
}
div.txt-justify:after {
　content: '';
　display: inline-block;
　width: 100%;
}
.choosed{
	border:1px solid #f00;
	background-color:#ddd;
}
.ui-widget-content a.btn-wide {
    color: #fff;
}
table.less-padding td{
	padding:4px 20px;
}
table.less-padding td:nth-child(1){
	text-align:center;
}
.less-padding img{
	height:35px;
}
</style>

<script>


var jobInfo_global = {};
//by_id 用法ex: jobInfo_global('79d3c49b-14f9-11e7-8cba-005056af760c')["job_name"]
//可叫之key如下 21個
//id , job_id , job_name , group_id , job_time , result , finished , finish_time ,
//flow_id , flow_name , flow_function , flow_seq , page ,
//scenario_id , scenario_name ,
//next_flow_id , next_flow_name , next_flow_page ,
//next_flow_explanation , next_flow_guide , max_flow_seq

var explane_txt_arr={"0":"<div style='height:180px;width:calc(50vw);text-align:center;line-height:160px;font-size:40px;'>請選擇情境</div>"};
	
	function draw_scenario(parameter){
		$(".result-table-wrap").hide();
		$(".btn-row").hide();
		$.ajax({
			type : "POST",
			url : "scenarioJob.do",
			data : parameter,
			success : function(result) {
				jobInfo_global = {};
				var json_obj = $.parseJSON(result);
				var result_table = "";
				$.each(json_obj, function(i, item) {
					jobInfo_global[item.job_id]={};
					$.each(item, function(key, value) {
						if(key=="result"){
							jobInfo_global[item.job_id][key]=$.parseJSON(value);
						}else{
							jobInfo_global[item.job_id][key]=value;
						}
					});
				});
				container = $("<div/>");
				$.each(json_obj, function(i, item) {
					container.append(
						$("<tr/>",{"job_id":item["job_id"]}).html([
							$("<td/>").html('<b style="font-size:16px;">'+item["job_name"]+"</b>"),
							$("<td/>").html(item["scenario_name"]+"<br>"+item["job_time"]),
							$("<td/>").html(item["flow_seq"]+'/'+item["max_flow_seq"]).css("text-align","center"),
							$("<td/>").html($("<div/>",{"style":"max-width:200px;"}).html(item["flow_name"])),
							$("<td/>").html($("<div/>",{"style":"max-width:200px;"}).html(item["next_flow_name"])),
							$("<td/>").html('<a class="btn btn-darkblue btn-update" job_id="'+item["job_id"]+'">內容</a>').css("text-align","center"),
							$("<td/>").html('<a class="btn btn-exec btn-delete" job_id="'+item["job_id"]+'">刪除</a>').css("text-align","center"),
							$("<td/>").html(
								(item["finished"] == "1"
									?"已"+(item["flow_seq"]==item["max_flow_seq"]?"完成":"中止")+"<br>於"+item["finish_time"]
									:'<a class="btn btn-primary btn-next" job_id="'+item["job_id"]+'">下一步</a>')
							).css("text-align","center")
						])
					);
				});
				$("#tbl_main tbody").html(container.html());
				$(".result-table-wrap").fadeIn();
				$(".btn-row").fadeIn();
				
			}
		});				
	}
	$(function() {

		draw_scenario({action : "getJobInfo"});
		$("#tbl_main").delegate(".btn-update", "click", function(e) {
			e.preventDefault();
			var this_job_id = $(this).attr("job_id");
			var result_t = "";
			var job_content_title ="";
			$.each(jobInfo_global[this_job_id]["result"], function(j, item) {
				job_content_title="工作歷程:";
				result_t += "<tr><td>"+item["step"]+"</td><td><div style='max-width:200px;word-break:keep-all;'>"+item["flow_name"]+"</div></td>"
							+"<td>"+item["category"]+"</td>"
							+"<td><div style='max-width:400px;'>"+item["result"]+"</div></td>"
							+"<td>"+(item["png_name"]==null||item["png_name"]==""?"":("<a href='./record.do?action=get_image&png_name="+item["png_name"]+"'><img src='./images/scenarioPic.png' class='func' title='歷程'></a>"))+"</td></tr>";
			});
			
			$(this).attr("result",result_t);
			
			$("#job_name_update").val(jobInfo_global[this_job_id]["job_name"]);
			$("#job_update").val(this_job_id);
			$("#job_content_title").html(job_content_title);
			$("#job_content_update tbody").html(result_t);
			
			
			var jump_array="";
			for(var i=0;i<=jobInfo_global[this_job_id]["max_flow_seq"];i++){
				if( i==jobInfo_global[this_job_id]["flow_seq"] && i== jobInfo_global[this_job_id]["max_flow_seq"]){
					jump_array+='<a class="choosed" title="已完成">完成流程</a>&nbsp;&nbsp;';
				}else if(i==jobInfo_global[this_job_id]["max_flow_seq"]){
					jump_array+='<a class="step_number" href="#" title="完成流程" onclick=\'jump_step(\"'+this_job_id+'\",\"'+i+'\",\"完成流程\")\'>完成流程</a>&nbsp;&nbsp;';
				}else if(i==jobInfo_global[this_job_id]["flow_seq"]){
					jump_array+='<a class="choosed step_number" title="目前步驟">'+(i+1)+'</a>&nbsp;&nbsp;';
				}else if(i<jobInfo_global[this_job_id]["flow_seq"]){
					jump_array+='<a class="step_number"href="#" title="重作步驟'+(i+1)+'" onclick=\'jump_step(\"'+this_job_id+'\",\"'+i+'\",\"重作步驟'+(i+1)+'\")\'>'+(i+1)+'</a>&nbsp;&nbsp;';
				}else if(i>jobInfo_global[this_job_id]["flow_seq"]){
					jump_array+='<a class="step_number"href="#" title="跳至步驟'+(i+1)+'" onclick=\'jump_step(\"'+this_job_id+'\",\"'+i+'\",\"跳至步驟'+(i+1)+'\")\'>'+(i+1)+'</a>&nbsp;&nbsp;';
				}
			}
			
			$("#job_content_step").html(jump_array);
			tooltip("step_number");
			if($(this).attr("result").length>2){
				$("#job_content_update").show();
			}else{
				$("#job_content_update").hide();
			}
			$("#output_pdf").hide();
			
			$.ajax({
				type : "POST",
				url : "record.do",
// 				async : false,
				data : {
					action : "output_record",
					job_id :this_job_id
				},
				success : function(result) {
					if(result=="success"){
						$("#output_pdf").attr("href","./uploaddoc.do?action=download_record_pdf&job_id="+this_job_id);
						$("#output_pdf").show();
					}else{
						console.log("輸出PDF失敗。");
					}
				}
			});
			
			
			$("#job_update").dialog("open");
			$('#job_content_update').poptrox({
				usePopupCloser: false,
				baseZIndex: 10000,
				popupPadding:0
			});
		});
		
		$("#tbl_main").delegate(".btn-delete", "click", function(e) {
			e.preventDefault();
			var this_job_id = $(this).attr("job_id");
			$("#job_delete").val(this_job_id);
			$("#job_delete").html("<table class='bentable nobreak'>"
				+"<tr><td>工作名稱</td><td>"+jobInfo_global[this_job_id]["job_name"]+"</td></tr>"
				+"<tr><td>所屬情境流程</td><td>"+jobInfo_global[this_job_id]["scenario_name"]+"</td></tr>"
			);
			$("#job_delete").dialog("open");
		});

		$("#tbl_main").delegate(".btn-next", "click", function(e) {
			e.preventDefault();
			var this_job_id = $(this).attr("job_id");
			$("#scenario_controller").remove();
			$("#job_next").html(
				"<table class='bentable'>"
				+"<tr><td>接下來的步驟: </td><td>"+jobInfo_global[this_job_id]["next_flow_name"]+"</td></tr>"
				+"<tr><td>步驟說明: </td><td style='max-width:calc(50vw);'>"+jobInfo_global[this_job_id]["next_flow_explanation"]+"</td></tr>"
				+"<tr><td>將跳至頁面:</td><td>"+(page_comparison[jobInfo_global[this_job_id]["next_flow_page"]]==null?"":page_comparison[jobInfo_global[this_job_id]["next_flow_page"]])+"</td></tr>"
				+"</table>"
			);
			$("#job_next").attr("job_id",this_job_id);
			
			$.ajax({
			    type : "POST",
			    url : "scenarioJob.do",
			    data : {
			    	action :"click_next_step",
			    	scenario_job_id : this_job_id,
			    	scenario_job_page : jobInfo_global[this_job_id]["next_flow_page"]
			    },success : function(result) {
			    	if(result=="success"){
			    		draw_scenario_controller_bar(this_job_id);
			    	}else{
			    		alert("系統異常，並未進入情境");
			    	}
			    }
			});
			$('#job_next').dialog('open');
		});
		
		$("#job_next").dialog({
			draggable : true, resizable : false, autoOpen : false,
			width : "auto" ,height : "auto", modal : true,
			show : {effect : "blind", duration : 300 },
			hide : { effect : "fade", duration : 300 },
			buttons : [{
				id : "job_next_enter",
				text : "確定跳轉",
				click : function() {
			    	window.location.href = jobInfo_global[$(this).attr("job_id")]["next_flow_page"];
			    	$(this).dialog("close");
				}
			},{
				text : "取消",
				click : function() {
					$(this).dialog("close");
				}
			}]
		});
		$("#job_next").show();
		
		$("#insert_job").dialog({
			draggable : true, resizable : false, autoOpen : false,
			width : "auto" ,height : "auto", modal : false,
			show : {effect : "blind", duration : 300 },
			hide : { effect : "fade", duration : 300 },
			buttons : [{
				text : "確定",
				click : function() {
					if($("#all_scenario_name").val()!=0 && $("#insert_job_name").val().length!=0){
						$(this).dialog("close");
						draw_scenario({
							action : "insert_job",
							scenario_id : $("#all_scenario_name").val(),
							job_name : $("#insert_job_name").val()
						});
					}else{
						alert("請填寫完整");
					}
				}
			},{
				text : "取消",
				click : function() {
					$(this).dialog("close");
				}
			},]
		});
		$("#insert_job").show();
		$("#job_update_button").click(function(){
			if($("#job_name_update").val().length!=0){
				$("#job_update").dialog("close");
				draw_scenario({
					action : "update_job",
					job_id : $("#job_update").val(),
					job_name : $("#job_name_update").val()
				});
			}else{
				alert("請填寫完整");
			}
		});
		$("#job_update").dialog({
			draggable : true, resizable : false, autoOpen : false,
			height : "auto", width : "auto", modal : true,
			show : {effect : "blind", duration : 300},
			hide : {effect : "fade", duration : 300},
			open : function(){
				$("#job_update").css("max-height", "calc(78vh)");
			},
			buttons : [{
				text : "確定",
				click : function() {
					$(this).dialog("close");
				}
			}]
		});
		
		$("#job_update").show();
		$("#job_delete").dialog({
			draggable : true, resizable : false, autoOpen : false,
			height : "auto", width : "auto", modal : true,
			show : {effect : "blind", duration : 300},
			hide : {effect : "fade", duration : 300},
			buttons : [{
				text : "確定刪除",
				click : function() {
					$(this).dialog("close");
					draw_scenario({
						action : "delete_job",
						job_id : $("#job_delete").val()
					});
					$.ajax({
						type : "POST",
						url : "scenarioJob.do",
						data : { 
							action : "clear_session",
						},success : function(result) {
							if(result=="success"){
								location.replace(location);	
							}
						}
					});
				}
			},{
				text : "取消",
				click : function() {
					$(this).dialog("close");
				}
			}]
		});
		$("#job_delete").show();
		
		$("#logout").click(function(e) {
			$.ajax({
				type : "POST",
				url : "login.do",
				data : {
					action : "logout"
				},
				success : function(result) {
					top.location.href = "login.jsp";
				}
			});
		});
		$.ajax({
		    type : "POST",
		    url : "scenarioJob.do",
		    data : {action :"select_all_scenario"},
		    success : function(result) {
		        var json_obj = $.parseJSON(result);
		        var option_str='<option value="0">請選擇</option>';
		        
		        $.each (json_obj, function (i) {
		        	if(json_obj[i].scenario_name.indexOf('教學')==-1){
			        	option_str+="<option value='"+json_obj[i].scenario_id+"'>"+json_obj[i].scenario_name+"</option>";
			        	var explane_txt = "<div style='text-align:center;font-size:30px;'>"+json_obj[i].scenario_name + "</div><div style='max-width:calc(50vw);margin:10px auto;padding:0px 40px;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+json_obj[i].result+"</div><hr>"
			        		+"<div style='max-width:calc(70vw);'><table>";
			        	$.each (json_obj[i].child, function (j) {
			        		if(json_obj[i].child[j].flow_seq!=0){
			        			explane_txt += "<tr><td style='vertical-align: text-top;'>步驟 " +json_obj[i].child[j].flow_seq+": </td><td style='vertical-align: text-top;'><div class='txt-justify' style='text-align: justify;text-justify: inter-ideograph;-ms-text-justify: inter-ideograph;-moz-text-align-last:justify;-webkit-text-align-last:justify;word-break:break-all;'>[&nbsp;"+json_obj[i].child[j].flow_name+"&nbsp;] </div></td><td><div style='max-width: calc(30vw);'>"+json_obj[i].child[j].next_flow_explanation + "</div></td></tr>";
			        		}
			        	});
			        	explane_txt += "</table></div>";
		        	}
		        	explane_txt_arr[json_obj[i].scenario_id] = explane_txt;
		        });
		        $("#explane_select").html(option_str);
		        $("#all_scenario_name").html(option_str);
		        $("#explane_txt").html(explane_txt_arr[0]);
		    }
		});
		
		$("#btn_main_create").click(function(e) {
			e.preventDefault();
			$("#insert_job_name").val('');
			$("#all_scenario_name").val('0');
			$("#insert_job").dialog("open");
		});
		
		$("#explane").dialog({
			draggable : true, resizable : false, autoOpen : false,
			width : "auto" ,height : "auto", modal : false,
			show : { effect : "blind", duration : 300 },
			hide : { effect : "fade", duration : 300 },
			open : function(){
				$("#explane").css("max-height", "calc(70vh)");
			},
			buttons : {
				"確定" : function() {$(this).dialog("close");}
			}
		});
		$("#explane").show();
		$("#explane_select").change(function(){
			$("#explane_txt").html(explane_txt_arr[$("#explane_select").val()]);
		});
	});
</script>
</head>
<body>
<input type="hidden" id="glb_menu" value='<%= menu %>' />
<input type="hidden" id="glb_privilege" value="<%= privilege %>" />

	<div class="page-wrapper" >
	
		<div class="header">
			<div class="userinfo">
				<p>使用者<span><%= (request.getSession().getAttribute("user_name")==null)?"":request.getSession().getAttribute("user_name").toString() %></span></p>
				<a href="#" id="logout" class="btn-logout">登出</a>
			</div>
		</div>
	
		<jsp:include page="menu.jsp"></jsp:include>
		<div id="msgAlert"></div>
	 	<h2 id="title" class="page-title">工作管理</h2>
		
		<!-- content-wrap -->
		<div class="content-wrap">
			<div id="caseAlert"></div>
		
			<div id="div_main" class="form-row" >
				<div class="search-result-wrap">
					<div class="form-row">
						<h2>決策管理</h2>
					</div>
					
					<div class="result-table-wrap">
						<table id="tbl_main" class="result-table">
							<thead>
								<tr>
									<th>工作名稱</th>
									<th>所屬情境流程</th>
									<th>進度</th>
									<th>已完成之步驟</th>
									<th>欲執行之步驟</th>
									<th colspan='3'>功能</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
					
					<div class="btn-row">
						<a id="btn_main_create" class="btn btn-exec btn-wide" >建立工作</a>
						<a id="btn_main_view" class="btn btn-exec btn-wide" onclick='$("#explane").dialog("open");'>查看情境流程</a>
<!-- 						<input id="checkbox-t" type="checkbox" style='top:-99999px;left:0px;'><label for="checkbox-t" style='float:right;display:none;'><span class="form-label">解說</span></label> -->
					</div>
				</div>
			</div>
			<div id='job_next' title='即將跳轉頁面' style='display:none;'></div>
			<div id='insert_job' title='新增情境流程工作' style='display:none;'>
				<table class='bentable'>
					<tr>
						<td>工作名稱:</td>
						<td><input type='text' id='insert_job_name'></td>
					</tr>
					<tr>
						<td>情境流程:</td>
						<td><select id='all_scenario_name'></select></td>
					</tr>
				</table>
				<div id='explane_txt_insert' style='line-height:26px;'></div>
			</div>
			
			<div id='job_update' title='工作內容' style='display:none;'>
				<table class='bentable'>
					<tr>
						<td>工作名稱:</td>
						<td><input type='text' id='job_name_update'></td>
						<td><button id='job_update_button' class='btn btn-exec'>修改名稱</button></td>
					</tr>
					<tr>
						<td>情境步驟:</td><td id='job_content_step'></td>
					</tr>
					<tr>
						<td id='job_content_title'>工作歷程:</td><td></td>
					</tr>
					<tr>
						<td colspan='3'>
							<table id = 'job_content_update' class="result-table less-padding">
								<thead>
									<tr>
										<th>步驟</th>
										<th>流程名稱</th>
										<th>項目</th>
										<th style='max-width:400px;'>結果</th>
										<th>圖</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>
											
										</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</table>
				<div style='text-align:center'><a href="#" target="_blank" id='output_pdf' class='btn btn-wide btn-alert'>輸出歷程</a></div>
			</div>
			<div id='job_delete' title='是否確認刪除此工作'>
				
			</div>
			<div id='explane' title='情境流程說明' style='display:none;'>
				<select id='explane_select'>
					<option value="0">餐飲設址</option>
				</select>
				<div id='explane_txt' style='line-height:26px;'>
					
				</div>
			</div>
		</div>
		<!-- content-wrap -->
		
		<script src="js/sbi/menu.js"></script>
		<footer class="footer">
			財團法人商業發展研究院  <span>電話(02)7707-4800 | 傳真(02)7713-3366</span> 
		</footer>
	</div>
	
</body>
</html>