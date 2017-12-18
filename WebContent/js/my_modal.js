/**
 * 教學模式已刪除 如需使用請搜尋git分支 develop_scenario 中 20170719以前之版本
 * 
 */

//------------------------------宣告參數----------------------------------------
$(window).load(function() {
//	自行引用my_modal.css 和檢查要用哪種 html2canvas
	head = document.getElementsByTagName('head')[0];
	link = document.createElement('link');
	link.rel = "stylesheet"
	link.href = "css/my_modal.css";
	head.appendChild(link);
	
	if(!window.html2canvas){
		url = "importPKG/html2canvas-0.5.0-alpha1/html2canvas.js";
//		var url = "importPKG/html2canvas-0.5.0-alpha1/html2canvas_ori.js";
//		if($("#map").length>0){
//			url = "importPKG/html2canvas-0.5.0-alpha1/html2canvas_20170413.js";
//		}
		head = document.getElementsByTagName('head')[0];
		link = document.createElement('script');
		link.type = "text/javascript";
		link.rel = "stylesheet"
		link.src = url;
		head.appendChild(link);
	}
});
if(!window.saving_now){
	var saving_now=0;
}
//if(!window.prepare_over){
//	var prepare_over=0;
//}
if(!window.draw_what){
	var draw_what={'bar':'產生長條圖','pie':'產生圓餅圖','area':'檢視資料表'};
}

//----------------------------------------------------------------------

var page_comparison={
	"realMap.jsp": "商圈資訊",
	"POI.jsp": "商圈POI",
	"country.jsp": "動態統計-國家",
	"city.jsp": "動態統計-目標城市",
	"industry.jsp": "動態統計-目標產業",
	"consumer.jsp": "動態統計-中國城市消費力",
	
	"costLiving.jsp": "生活費用",
	"population.jsp": "台灣人口社經",
	"populationNew.jsp": "台灣人口社會經濟資料",
	"upload.jsp": "產業分析基礎資料庫",
	"personaNew.jsp": "目標客群定位",
	"evaluate.jsp": "目標市場決策評估",
	"caseCompetitionEvaluation.jsp": "競爭力決策評估",
	"caseChannelEvaluation.jsp": "通路決策評估",
	"regionSelect.jsp": "區位選擇、環域分析",
	"finModel.jsp": "新創公司財務損益平衡評估工具",
	"productForecast.jsp": "新產品風向評估",
	
	"product.jsp": "商品管理",
	"agent.jsp": "通路商管理",
	"agentAuth.jsp": "通路商授權商品管理",
	"serviceAgentAssign.jsp": "服務識別碼指定通路商作業",
	"productVerify.jsp": "商品真偽顧客驗證作業",
	"authVerify.jsp": "商品真偽通路商驗證作業",
	"serviceVerify.jsp": "服務識別碼查詢作業",
	"serviceRegister.jsp": "商品售後服務註冊",
	"serviceQuery.jsp": "服務識別碼查詢作業",
	"persona.jsp": "東南亞商機定位工具",
	"marketPlace.jsp": "城市商圈",
	
	"news.jsp": "新聞專區",
	"uploaddocs.jsp": "商機觀測站",
	"groupBackstage.jsp": "公司後台管理",
	"uploaddocsManager.jsp": "商機觀測站後臺",
	"white_page.jsp" : "測試頁面",
	
	"pdf.jsp" : "電子書",
	"scenarioJob.jsp" : "情境流程",
	
	"marketingStrategy.jsp" : "市場進入策略定位",
		
}


//---------------修改流程狀態---------------

//jump_step(1,2,3) 跳到步驟X
//finish_step()完成情境流程
//over_step() 離開情境流程註明 並非完成 只是離開狀態

function jump_step(job_id,goto_seq,title){
	$("#jump_confirm").remove();
	$("html").append("<div id='jump_confirm' title='情境流程變更' style='margin:10px 20px;'>"
			+"<table class='bentable-style1'>"
			+"<tr><td colspan='2'>確定要 "+title+"?</td></tr>"
//			+(title=="完成流程"?"":"<tr><td>注1:</td><td>重做完成儲存新的結果後，舊結果將被刪除。</td></tr>")
			+"</table>"
			+"</div>");
	
	$("#jump_confirm").dialog({
		draggable : true, resizable : false, autoOpen : true,
		width : "auto" ,height : "auto", modal : false, minWidth: 300,
		show : {effect : "blind", duration : 300 },
		hide : { effect : "fade", duration : 300 },
		buttons : [{
			text : "確定",
			click : function() {
				$.ajax({
					type : "POST",
					url : "scenarioJob.do",
					data : { 
						action : "jump_step",
						scenario_job_id : job_id,
						goto_flow : goto_seq
					},success : function(result) {
						if(result=="jump_to_success"){
							history.go(0);
							location.replace(location);	
						}else{
							alert("執行"+title+"異常");
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
	
}

function finish_step(){
	if($("#current_job_finish").length==0){
		var step_name="";
		$.ajax({
			type : "POST",
			url : "scenarioJob.do",
			async : false,
			data : { action : "get_session" },
			success : function(result) {
				var json_obj = $.parseJSON(result);
				var scenario_job_id = json_obj.scenario_job_id;
				$.ajax({
					type : "POST",
					async : false,
					url : "scenarioJob.do",
					data : { 
						action : "get_current_job_info",
						job_id : scenario_job_id,
						
					},success : function(result) {
						var json_obj = $.parseJSON(result);
						step_name = json_obj.next_flow_name;
					}
				});
			}
		});
		$("html").append("<div id='current_job_finish' title='是否完成此步驟' style='margin:10px 20px;color:red'>"+step_name+"</div>");				
		
		$("#current_job_finish").dialog({
			draggable : true, resizable : false, autoOpen : true,
			width : "auto" ,height : "auto", modal : true, minWidth: 300,
			show : {effect : "blind", duration : 300 },
			hide : { effect : "fade", duration : 300 },
			close : function(){
				if($("#current_job_finish").attr("over")=="true"){
					var this_node;
					$.ajax({
						type : "POST",
						async : false,
						url : "scenarioJob.do",
						data : { 
							action : "clear_result_to_store",
						},success : function(result) {
							if(result=="clear_success"){
							}else{
								alert("清空原資料異常？可能並未清空上次所選結果");
							}
						}
					});
					if(window.all_address){
						if(all_address.length>0){
							if(window.scenario_record){
								scenario_record("查詢地址","['"+all_address.join("','")+"']");
							}
						}
					}
					if($("#tree").length>0){
						var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
						sibling_node.forEach(function(sib_node) {
							if($(sib_node.span.childNodes[1]).hasClass('poi')){
								if(window.scenario_record){
									scenario_record("查詢POI","[ "
											+$(sib_node.span.childNodes[1]).attr('poi_node_key')+", "
											+$(sib_node.span.childNodes[1]).attr('scenario_border_north')+", "
											+$(sib_node.span.childNodes[1]).attr('scenario_border_east')+", "
											+$(sib_node.span.childNodes[1]).attr('scenario_border_south')+", "
											+$(sib_node.span.childNodes[1]).attr('scenario_border_west')+", "
//											+$(sib_node.span.childNodes[1]).attr('scenario_lat')+","
//											+$(sib_node.span.childNodes[1]).attr('scenario_lng')+","
//											+$(sib_node.span.childNodes[1]).attr('scenario_zoom')+",'"
											+"'"+sib_node.title+"']");
								}
							}else if($(sib_node.span.childNodes[1]).hasClass('heat_map')){
								if(window.scenario_record){
									scenario_record("查詢熱力圖","["
											+$(sib_node.span.childNodes[1]).attr('scenario_lat')+","
											+$(sib_node.span.childNodes[1]).attr('scenario_lng')+","
											+$(sib_node.span.childNodes[1]).attr('scenario_zoom')+",'"
											+sib_node.title+"']");
								}
							}else if($(sib_node.span.childNodes[1]).hasClass('BD')){
								if(window.scenario_record){scenario_record("查詢商圈",sib_node.title);} 
							}
						});
					}
					if(window.rs_markers){
						if(window.rs_markers.length>0){
							var result_str="[";
							result_str+="['名稱','經度','緯度','半徑','時速','時間']";
							$.each(rs_markers, function(i, node){
								result_str+=",['點"+(i+1)+"', '"+new Number(node.marker.position.lat()).toFixed(4)+"', '"+new Number(node.marker.position.lng()).toFixed(4)+"', '"+new Number(node.circle.radius).toFixed(4)+"m', '"+node.speed+"km/hr', '"+node.time+"mins']";
							});
							result_str+="]";
							if(window.scenario_record){scenario_record("環域分析",result_str);}
						}
					}
					if(["POI.jsp","realMap.jsp","regionSelect.jsp"].indexOf(location.pathname.split("/").pop())!=-1){
						//要存圖片
						console.log("記錄流程: 含地圖，要存圖片");
						scenario_record("完成此步驟","地圖資訊圖片","selector","body");
					}else{
						//不存圖片
						console.log("記錄流程: 不含地圖，不存圖片");
					}
					
					//跳頁
					function check_saving () {
						setTimeout(function () {
							console.log(saving_now);
							if(saving_now){
								if(saving_now>80){
									alert("儲存圖片失敗，原因不明。");
									window.location.reload();
								}
								saving_now++;
								check_saving();
							}else{
								//saving_over then over
								$.ajax({
									type : "POST",
									url : "scenarioJob.do",
									data : { 
										action : "over_a_step",
										scenario_lat : ((window.map)?(map.getCenter().lat()+""):""),
										scenario_lng : ((window.map)?(map.getCenter().lng()+""):""),
										scenario_zoom : ((window.map)?(map.getZoom()+""):"") 
									},success : function(result) {
										if(result.indexOf(".jsp")!=-1){
											warningMsgFunc("系統","下一步。將跳至"+(page_comparison[result]==null?"":page_comparison[result])+"介面",function(){
												window.location.href =  result ;
											});
										}else if(result=='finished'){
											warningMsgFunc("系統","完成此情境流程。將跳轉回工作介面", function(){
												window.location.href = "scenarioJob.jsp" ;
											});
										}else{
											alert("完成步驟發生異常?\n執行失敗。");
										}
									}
								});
							}
						}, 1*100);
					}
					check_saving();
				}
			},buttons : [{
				text : "確定完成",
				click : function() {
					$("#current_job_finish").attr("over","true");
					$(this).dialog("close");
				}
			},{
				text : "取消",
				click : function() {
					$("#current_job_finish").attr("over","false");
					$(this).dialog("close");
				}
			}]
		});
	}else{
		$("#current_job_finish").dialog("open");
	}
}
function over_step(){
	$.ajax({
		type : "POST",
		url : "scenarioJob.do",
		data : { 
			action : "over_scenario",
		},success : function(result) {
			if(result=="success"){
				window.location.reload();
			}
		}
	});
}

//---------------callee---------------
//draw_scenario_controller_bar() 畫左下角[情境流程 操作區塊]
//job_explanation() 讀取現在流程的資料
//scenario_record() 記錄

//printscreen()
function draw_scenario_controller_bar(scenario_job_id){
	$.ajax({
		type : "POST",
		url : "scenarioJob.do",
		data : { 
			action : "get_current_job_info",
			job_id : scenario_job_id
		},success : function(result) {
			
			var json_obj = $.parseJSON(result);
			
			$("html").append("<div id='scenario_controller' class='scenario_controller' style='z-index:10000;'>"
					+ "    <span id = 'job_title' class='focus'  onclick='job_explanation(\""+json_obj.job_id+"\")'>"+json_obj.job_name+" "+json_obj.flow_seq+'/'+ json_obj.max_flow_seq+"</span>"
					+ "    <img id='check_btn' class='func' onclick='finish_step()' style='float:right;height:22px;margin-left:10px;' title='完成此步驟' src='./images/scenarioCheck.png'>"
					+ "</div>");
			
			tooltip("func");
		}
	});
}

function job_explanation(job_id){
	if($("#current_job_detail").length==0){
		$.ajax({
			type : "POST",
			url : "scenarioJob.do",
			data : { 
				action : "get_current_job_info",
				job_id : job_id
			},success : function(result) {
				var json_obj = $.parseJSON(result);
				$("html").append("<div id='current_job_detail' title='情境流程說明' style='margin:10px 20px;'>"
						+"<table class='bentable-style1'>"
						+"<tr><td>工作名稱:</td><td>"+json_obj.job_name+"</td></tr>"
						+"<tr><td>所屬情境:</td><td>"+json_obj.scenario_name+"</td></tr>"
						+"<tr><td>項目:</td><td>"+json_obj.next_flow_name+"</td></tr>"
						+"<tr><td>說明:</td><td style='max-width:calc(50vw);'>"+json_obj.next_flow_explanation+"</td></tr>"
						+"</table>"
						+"</div>");
				
				$("#current_job_detail").dialog({
					draggable : true, resizable : false, autoOpen : true,
					width : "auto" ,height : "auto", modal : false, minWidth: 300,
					show : {effect : "blind", duration : 300 },
					hide : { effect : "fade", duration : 300 },
					buttons : [{
						text : "確定",
						click : function() {$(this).dialog("close");}
					},{
						text : "跳至流程頁面",
						click : function() {
							window.location.href = json_obj.next_flow_page;
							$(this).dialog("close");
						}
					},{
						text : "自訂截圖",
						click : function() {
							$(this).dialog("close");
							setTimeout(function () {
								//png_name = printscreen("body");
								scenario_record("自定義之截圖","","selector","body",function(){warningMsg("訊息","截圖成功");});
							},1000);
						}
					},{
						text : "暫停情境流程",
						click : function() {
							$.ajax({
								type : "POST",
								url : "scenarioJob.do",
								data : { 
									action : "clear_session",
								},success : function(result) {
									if(result=="success"){
										window.location.reload();	
									}
								}
							});
							$(this).dialog("close");
						}
					},{
						text : "結束情境流程",
						click : function() {
							over_step();
							$(this).dialog("close");
						}
					}]
				});
			}
		});
	}else{
		$("#current_job_detail").dialog("open");
	}
}

function scenario_record(category,result,option,parameter,func){
	console.log("記錄流程: [ "+category+", "+result+"]");
	var not_in_scenario=0;
	$.ajax({
		type : "POST",
		url : "scenarioJob.do",
		async : false,
		data : { action : "get_session" },
		success : function(result) {
			var json_obj = $.parseJSON(result);
			if(location.pathname.split("/").pop()!= json_obj.scenario_job_page){
				not_in_scenario=1;
				console.log("非情境中");
			}
		}
	});
	if(not_in_scenario){
		if(func!=null){
			func();
		}
		return;
	}
	//	console.log(option+" ### "+pa);
	var png_name="";
	if(option=="selector"){
		if(parameter!=null){
			var print_selecter = parameter;
			saving_now=1;
			png_name = printscreen(print_selecter,func);//+".png";
			console.log("得到png_name: "+png_name+" from: "+print_selecter);
		}else{
			saving_now=0;
		}
	}else{ 
		if(option=="url"){
			png_name = getURLimgsave(parameter);
		}else if(option=="name"){
			png_name = parameter;
		}else if(option!=null){
			alert("option strange?");
		}
		if(func!=null){
			func();
		}
	}
	$.ajax({
		type : "POST",
		url : "scenarioJob.do",
		async : false,
		data : { 
			action : "set_scenario_result",
			current_page : location.pathname.split("/").pop(),
			category : category,
			result : result,
			png_name : png_name
		},success : function(result) {
			if(result!='success' && result!='not_in_scenario'){
				alert("程序異常?\n儲存結果失敗。");
			}
		}
	});
}


function printscreen(selector,func){
	//錄影用
//	saving_now=0;
//	if(func!=null){
//		func();
//	}
//	return "41a25aac-82c3-45c4-96b0-29c3e4012217.png";
	//錄影用
	
	var png_name = UUID()+".png";
	var cssStr =''
	var oStyle = document.createElement('style');
	if(selector==null || selector=='body'){
		selector = 'body';
//		if($("#map").length>0){
//			$(".header").css("background-color","rgb(204,264,255)");
//			$(".content-wrap").css("background-color","rgb(236,236,236)");
//			$("#logout").css("background-color","#FF4646");
//			$("input").attr("placeholder","");
//			$("body").css("height","100vh");
//		}
//		$("#logout").css("border-radius","20px");
//		$("body").append($(".page-title").clone());
//		$("body").css("height","calc(100vh)");
		cssStr = '.html2canvas-container { width: 100% !important; height: 100% !important; }';
	}else{
		cssStr = '.html2canvas-container { width: 3000px !important; height: 3000px !important; }';
	}
	
	document.getElementsByTagName('head')[0].appendChild(oStyle);
	if(document.all) { // IE
		oStyle.styleSheet.cssText = str;
	}
	oStyle.textContent = cssStr;

	var transform=$(".gm-style>div:first>div").css("transform")==null?"":$(".gm-style>div:first>div").css("transform");
	var comp=transform.split(",") //split up the transform matrix
	var mapleft=parseFloat(comp[4]) //get left value
	var maptop=parseFloat(comp[5])  //get top value
	$(".gm-style>div:first>div").css({ //get the map container. not sure if stable
	  "transform":"none",
	  "left":mapleft,
	  "top":maptop,
	})
	html2canvas($(selector),
	{
	  //allowTaint:true,	
	  useCORS: true,
	  onrendered: function(canvas)
	  {
		$("body").css("height","auto"); 
		var dataURL = "";
		try{
			dataURL = canvas.toDataURL('data:image/png;');
			//好像在某些特定情況會有 報 The operation is insecure 的情況 選了很多資訊的地圖視窗 不肯定原因為何
		}catch(err){
			png_name = "failed";
			warningMsg("警告","截圖失敗，請洽系統管理員。");
			try{
				console.log("success use canvas but can't dataURL;");
			}catch(err){
				console.log("no!~~~~ canvas very seriously can't use.");
			}
			
			dataURL = "                                                  ";
		}
		$.ajax({
			type : "POST",
			url : "record.do",
			async : false,
			data : {
				action : "dataURL_to_PNG",
				dataURL : dataURL,
				png_name : png_name,
				current_page : location.pathname.split("/").pop()
			},
			success : function(result) {
				if(result=="not_in_scenario"){
					//pass
					warningMsg("警告","非scenario頁面 ，不儲存。");
					console.log("非scenario頁面 ，不儲存。");
				}else if(result=="error"){
					warningMsg("警告","截圖存檔異常");
//					alert("截圖存檔異常");
				}else{
//					warningMsg("訊息","截圖成功");
					
					if(!window.errorHandling){
						console.log("儲存圖片: "+result);
					}
					
					if(func!=null){
						func();
					}
				}
			}
		});
		saving_now=0;
	  }
	});
	
	return png_name;
}

//------------------loading時檢查 scenario------------------

$(function(){
	$(window).load(function(){
		var scenario_job_id = "";
		var scenario_job_page ="";
		$.ajax({
			type : "POST",
			url : "scenarioJob.do",
			async : false,
			data : { action : "get_session" },
			success : function(result) {
				var json_obj = $.parseJSON(result);
				scenario_job_id = json_obj.scenario_job_id;
				scenario_job_page = json_obj.scenario_job_page;
			}
		});
		
		var current_page = location.pathname.split("/").pop();
		if( scenario_job_id.length > 2 && $("#scenario_controller").length==0){
			draw_scenario_controller_bar(scenario_job_id);
		}
		if( scenario_job_id.length > 2 && current_page == scenario_job_page){
			var this_job_info;
			$.ajax({
				type : "POST",
				url : "scenarioJob.do",
				async : false,
				data : { 
					action : "get_current_job_info",
					job_id : scenario_job_id,
				},success : function(result) {
					this_job_info = $.parseJSON(result);
					eval(this_job_info.next_flow_guide);
				}
			});
			
			//個別處理 [情境流程中 同時包含(市場進入策略定位,目標客群定位)的情況 代入彼此的答案
			//要使用記得 marketingStrategy personaNew 兩個頁面記錄下來的結果要符合json格式 不然會出錯
			//主要是 產品名 年齡 性別
			if(["marketingStrategy.jsp","personaNew.jsp"].indexOf(location.pathname.split("/").pop())!=-1){
				var result_obj = $.parseJSON(this_job_info.result);
				$.each(result_obj, function(i, item) {
					if(["進入市場策略","目標客群定位"].indexOf(result_obj[i].category)!=-1){
						var questionnaire_obj = eval(result_obj[i].result);
						console.log(questionnaire_obj);
						$("input[name='px9'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px8'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px7'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px6'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px5'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px4'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$("input[name='px3'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
						$.each(questionnaire_obj.pop(),function(i,item){
							$("input[name='age'][value=" + item + "]").prop('checked', true);
						});
						$.each(questionnaire_obj.pop(),function(i,item){
							$("input[name='sex'][value=" + item + "]").prop('checked', true);
						});
						$("#product").val(questionnaire_obj.pop());
						$("input[name='px1'][value=" + questionnaire_obj.pop() + "]").prop('checked', true);
					}
				});
			}
			
			//如果有地圖 畫出之前選的結果
			if(window.map){
				loading();
				var result_obj = $.parseJSON(this_job_info.result);
				$.each(result_obj, function(i, item) {
					if(!window.map || !window.draw_env_analyse){
						return;
					}
					if(result_obj[i].category=="查詢商圈"){
						select_BD(result_obj[i].result,"no_record");
					}else if(result_obj[i].category=="查詢POI"){
						var poi_obj = eval(result_obj[i].result);
	//					map.setCenter(new google.maps.LatLng(poi_obj[0], poi_obj[1]));
	//					map.setZoom(poi_obj[2]);
						choosed_node_key = poi_obj[0] + "";
						select_poi(poi_obj[5],"no_record",poi_obj[1],poi_obj[2],poi_obj[3],poi_obj[4]);
					}else if(result_obj[i].category=="查詢熱力圖"){
						var poi_obj = eval(result_obj[i].result);
	//					map.setCenter(new google.maps.LatLng(poi_obj[0], poi_obj[1]));
	//					map.setZoom(poi_obj[2]);
						
						heatmap_poi(poi_obj[3],"no_record",poi_obj[0],poi_obj[1],poi_obj[2]);
					}else if(result_obj[i].category=="區位選擇"){
						draw_region_select(result_obj[i].result);
					}else if(result_obj[i].category=="環域分析"){
						console.log("環域"+result_obj[i].result);
						draw_env_analyse(result_obj[i].result);	
					}else if(result_obj[i].category=="查詢地址"){
						var addr_array = eval(result_obj[i].result);
						for(var j=0;j<addr_array.length;j++){
							draw_address(addr_array[j]);
						}
					}
				});
				$.ajax({
					type : "POST",
					url : "scenarioJob.do",
					async : false,
					data : { 
						action : "get_session_latlngzoom"
					},success : function(result) {
						if(result.length>0){
							var json_obj = $.parseJSON(result);
							map.setCenter(new google.maps.LatLng(json_obj.scenario_lat, json_obj.scenario_lng));
							map.setZoom(parseInt(json_obj.scenario_zoom));
						}
					}
				});
				loading('over');
			}
		}
	});
});


//------------------共用function------------------

function getURLimgsave(url){
	if(url.length<2){
		return "";
	}
//	loading();
	var png_name = UUID()+".png";
	$.ajax({
		type : "POST",
		url : "record.do",
		data : {
			action : "save_URL_to_PNG",
			imageUrl :url,
			png_name : png_name,
		},
		success : function(result) {
			if(result=="success"){
//				loading('over');
			}else{
				warningMsg("警告","抓取圖片失敗，請洽系統管理員");
				png_name="failed";
			}
		}
	});
	return png_name;
}
