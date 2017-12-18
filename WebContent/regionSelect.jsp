<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/styles_map.css"/>
<link rel="stylesheet" href="css/jquery.dataTables.min.css"/>

<script src="js/d3.v3.min.js"></script>

<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<!-- <script type="text/javascript" src="js/jquery.validate.min.js"></script> -->
<!-- <script type="text/javascript" src="js/additional-methods.min.js"></script> -->
<!-- <script type="text/javascript" src="js/messages_zh_TW.min.js"></script> -->
<script type="text/javascript" src="js/mapFunctionEnvAnal.js"></script>


<script type="text/javascript" src="importPKG/oriSBI/js/wicket.js"></script>
<script type="text/javascript" src="importPKG/oriSBI/js/wicket-gmap3.js"></script>

<style>
	.content-wrap{
	    background: #fff;
	    float: left;
	    margin-left: 0px;
	    margin-top: 100px;
		margin-bottom: 0px;
		padding-bottom: 6px;
		height: calc(100vh - 136px);
	    overflow-y: hidden;
	    width: 100%;
		background-color: #EEF3F9;
	}
	.search-result-wrap{
 		padding: 2px 5px 0px 5px; 
		margin-bottom: 0px;
		height: 100%;
	}
	#map{
		height: 100%;
 	}
	.show_ori_checkbox input[type=checkbox]{ 
 	    position: static; 
 	} 

	.bentable2 {
		font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
	}
	.bentable2 img:hover{
		background: #d8d8d8;
		box-shadow:1px 1px 2px #999;
	}
	.bentable2 td:nth-child(2n+1){
		text-align:left;
		word-break: keep-all;
	}
	.bentable2 td{
		padding-left:5px;
		padding:3px 5px;
	}
	.bentable2 th{
		word-break: keep-all;
		padding:16px 0px 2px 0px;
		text-align:left;
		font-size:22px;
		font-weight: bold;
		color:#444;
	}
</style>
<style>
	.poi-statistic-table, .poi-villageInfo-table{
		position: fixed;
		z-index: 100;
		margin: auto;
  		width:360px; 
		background-color:#fefefe;
		border-radius: 2px;
 		left:calc(50vw - 250px);
 		top:calc(50vh - 240px );
 		left:50px;
 		top: 60px;
		padding:6px;
		padding-bottom:12px;
		box-shadow:1px 1px 7px #888;
	}
	.poi-statistic-table .info_window,.poi-villageInfo-table .info_window{
/* 		width:100%; */
		width: 95% !important;
	}
	.info_window td:nth-child(1), .info_window td:nth-child(2){
		text-align: center;
	}
	.info_window td:nth-child(2){
		font-size: 100%;
		padding: 3px 5px;
	}
	.info_window tr td div{
		width: 80px;
		margin: auto;
		text-align: right;
	}
	.word-120pa{
		font-size:120%; 
	}
	table.dataTable tbody tr {
		background-color: transparent;
	}
	.info_window table.dataTable tbody tr {
		background-color:transparent;
	}
	.ui-dialog-title{
		font-family: "微軟正黑體";
	}
	.ui-widget select, .ui-widget button, .ui-widget select option{
		font-family: "微軟正黑體";
	}
</style>
<script>
var businessdistrict;
var map;
var all_BDs_region_select=[];
if(!window.change_to_click_mode){
	change_to_click_mode=0;
}
</script>

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
<h2 class="page-title">區位選擇</h2>
	<div class="search-result-wrap">

	<a class="btn btn-orange" id='region_btn' onclick='$("#regionselect").dialog("open");' style='position:absolute;float:left;margin-left:150px;margin-top:14px;z-index:1;'>區位選擇</a>
	<a class="btn btn-orange" id='env_btn' style='position:absolute;float:left;margin-left:280px;margin-top:14px;z-index:1;'>環域分析</a>

	<div id="map"></div>

	<div id='regionselect' title='區位選擇' style='display:none;'>
		<div id='over' style='display:none;'>
			<div style='padding:20px 40px;'>完成分析！請於地圖檢視結果。<br><br>請參考本系統<span id='insert'></span>提供之「決策建議」。<br><br>按下「確認」關閉視窗，或按「上一步」重新分析！</div>
			<hr style='height:1px;border:none;border-top:1px solid #ddd;'>
			<div style="margin:0px 20px;float:right;">
				<button class='ui-button' onclick='$("#over").hide();$("#choose").show();'>上一步</button>
				<button class='ui-button' onclick='$("#regionselect").dialog("close");'>確認</button>
			</div>
		</div>
		<div id='choose' style='padding:0 20px;background-color:#fafafa;'>
			<table class='bentable2'>
				<tr><th>一、請選擇欲觀察城市商圈範圍：</th></tr>
				<tr>
					<td>
						國家：<select id='selectcountry'><option value="0">請選擇國家</option><option value="Taiwan">台灣</option></select>
					</td>
				</tr>
				<tr>
					<td>
						城市：<select id='selectRegion'><option value="0">請先選擇國家</option></select>
					</td>
				</tr>
				<tr>
					<td id='BD'>
						商圈：請先選擇城市
					</td>
				</tr>
				
				<tr><th>二、城市商圈選擇評估試算：</th></tr>
				<tr>
					<td>
						<input type="radio" id='retail_radio' name='hee' value='零售業'>
						<label for="retail_radio"><span class="form-label">零售業</span></label>
						<input type="radio" id='dining_radio'name='hee' value='餐飲業'>
						<label for="dining_radio"><span class="form-label">餐飲業</span></label>
					</td>
				</tr>
				<tr>
					<td style="height:40px;vertical-align:bottom;">
						<br>權重：<div id="slider" style='position:relative;top:-14px;left:60px;'></div>
					</td>
				</tr>
			</table>
			
			<table class='show_ori_checkbox' style='width:600px;font-size:16px'>
				<tr>
					<td>
						未來潛力<input type='text' id='rs1' style='width:60px' value='33.56' disabled/>%<br>
						　├<input type="checkbox" name='check1' checked>未來區域規劃<br>　└<input type="checkbox" name='check2' checked>消費發展潛力
					</td>
					<td>
						<td>現況發展<input type='text' id='rs2' style='width:60px' value='39.74' disabled/>%<br>
						　├<input type="checkbox" name='check3' checked>市場消費規模<br>　└<input type="checkbox" name='check4' checked>經營成本
					</td>
					<td>
						<td>競爭強度<input type='text' id='rs3' style='width:60px' value='26.70' disabled/>%<br>
						　├<input type="checkbox" name='check5' checked>業種業態組成<br>　└<input type="checkbox" name='check6' checked>商圈競爭狀況
					</td>
				</tr>
				<tr>
					<td colspan=3>
					<br>
					(權重預設為該業平均值，可依實際需求進行調整。)
					</td>
				</tr>
			</table>
			<hr style='height:1px;border:none;border-top:1px solid #ddd;'>
			<div style="margin:0px 20px;float:right;">
				<button class='ui-button' onclick='$("#regionselect").dialog("close");'>取消</button>
				<button class='ui-button' onclick='do_region_analysis()'>執行分析</button>
			</div>
		</div>
	</div>
	
	
    <script type="text/javascript">
	    var markers = [];
	    var rs_markers=[];
	    
	    function initMap() {
			map = new google.maps.Map(document.getElementById('map'), {
				zoom: 7,
				center: {lat: 23.900, lng: 121.000},
				mapTypeId: google.maps.MapTypeId.ROADMAP
			});
			
			envAnalSetup("env_btn",map);
			
			trafficLayer = new google.maps.TrafficLayer();
			transitLayer = new google.maps.TransitLayer();
   		
   		}
	    
	    function draw_BDS(BDs,n){
	    	var BD_name=BDs.replace("商圈","")+"商圈";
	    	if(BDs=="新板"){BD_name="新板特區商圈";}
	    	if(BDs=="桃園觀光"){BD_name="桃園觀光夜市";}
	    	if(BDs=="中壢觀光"){BD_name="中壢觀光夜市";}
	    	if(BDs=="中壢區火車站前"){BD_name="中壢站前商圈";}
	    	
	    	if(BDs=="國華友愛"){BD_name="國華友愛新商圈";}
	    	if(BDs=="桃園區火車站前商圈"){BD_name="桃園站前商圈";}
	    	if(BDs=="濱江道"){BD_name="濱江道和平路商圈";}
	    	if(BDs=="南大街"){BD_name="海港路南大街商圈";}
	    	
	    	
	    	$.ajax({
	    		type : "POST",
	    		url : "realMap.do",
	    		async: false,
	    		data : {
	    			action : "select_BD",
	    			name : BD_name
	    		},
	    		success : function(result) {
	    			
	    			var json_obj = $.parseJSON(result);
	    			$.each(json_obj,function(i, item) {
		    			var bermudaTriangle = new google.maps.Polygon({
							paths: json_obj[i].center,
							strokeColor: '#FF0000',
							strokeOpacity: 0.8,
							strokeWeight: 2,
							fillColor: '#FF0000',
							fillOpacity: 0.1
						});
						bermudaTriangle.setMap(map);
						var marker = new google.maps.Marker({
						    position: businessdistrict[BDs].center,
// 							label : n,
							icon : (location.href.replace(
						    		location.href.split("/").pop(),
				    		'')+"images/envMarkers/rsMarker-"+n+".png"),
						    title: businessdistrict[BDs].name,
						    map: map
						});
						var timer;
// 						var tmp_id=UUID();
// 						var infowindow = new google.maps.InfoWindow({content: "<div style='padding:8px 15px;font-size:16px;'>"+businessdistrict[BDs].name+"&nbsp;&nbsp;<a href='#' id='"+tmp_id+"' onclick='' class='tooltip' >刪除</a></div>"});
// 						console.log(result);
						var tmp_table='<table class="info_window">'+
						'<tr><th colspan="2">'+json_obj[i].BD_name+'　</th></tr>'+
						(json_obj[i].city.length<2?"":'<tr><td>城市：</td><td>'+json_obj[i].city+'</td></tr>')+
						(json_obj[i].area.length<2?"":'<tr><td>區域：</td><td>'+json_obj[i].area+'</td></tr>')+
						(json_obj[i].population.length<2?"":'<tr><td>人流：</td><td>'+json_obj[i].population+'</td></tr>')+
						(json_obj[i].status.length<2?"":'<tr><td>地位：</td><td>'+json_obj[i].status+'</td></tr>')+
						(json_obj[i].radiation.length<2?"":'<tr><td>輻射：</td><td>'+json_obj[i].radiation+'</td></tr>')+
						(json_obj[i].traffic.length<2?"":'<tr><td>通達：</td><td>'+json_obj[i].traffic+'</td></tr>')+
						(json_obj[i].business_cost.length<2?"":'<tr><td>經營成本：</td><td>'+json_obj[i].business_cost+'</td></tr>')+
						'</table>';
						var infowindow = new google.maps.InfoWindow({content:tmp_table});
						
// 						var infowindow = new google.maps.InfoWindow({content: "<div style='padding:8px 15px;font-size:16px;'>"+businessdistrict[BDs].name+"</div>"});
						
						
						google.maps.event.addListener(marker, "click", function(event) {
							map.setCenter(businessdistrict[BDs].center);
							map.setZoom(map.getZoom()+2);
						});
						google.maps.event.addListener(marker, "mouseover", function(event) { 
// 							if(!change_to_click_mode){
// 								$("#"+tmp_id).click(function(){
// 									bermudaTriangle.setMap(null);
// 						            infowindow.setMap(null);
// 						            marker.setMap(null);
// 								});
					        	infowindow.open(marker.get('map'), marker);
					        	clearTimeout(timer);
// 							}
				        }); 
						google.maps.event.addListener(marker, "mouseout", function(event) {
// 							if(!change_to_click_mode){
				        		timer = setTimeout(function () { infowindow.close(); }, 1500);
// 							}
				        });
						
						google.maps.event.addListener(infowindow, "closeclick", function(event) {
							infowindow.close();
				        });
						all_BDs_region_select.push(bermudaTriangle);
						all_BDs_region_select.push(infowindow);
						all_BDs_region_select.push(marker);
						google.maps.event.addListener(bermudaTriangle, "click", function(event) {
							if($("#env_analyse").dialog("isOpen")&& $("#draw_circle").css("display")=="none"){
								google.maps.event.trigger(map, 'click',event);
							}else{
								infowindow.open(marker.get('map'), marker);
							}
				        });
	    			});
	    		}
	    	});
	    }
	    
	    $(function(){

	    	$.ajax({
				type : "POST",
				url : "regionselect.do",
				data : {
					action : "select_area"
				},
				success : function(result) {
	    			var json_obj = $.parseJSON(result);
					var result_table = "<option value='0'>請選擇國家</option>";
					if(json_obj.length>0){
						$.each(json_obj,function(i, item) {
							result_table+="<option value='"+item+"'>"+item+"</option>";
						});
		    			$("#selectcountry").html(result_table);
					}else{
						$("#selectcountry").html("<option value='0'>無國家資料</option>");
					}
				}
	    	});
	    	$("#selectcountry").change(function(){
	    		$("#BD").html('<div style="float:left;">商圈：</div>');
	    		$.ajax({
					type : "POST",
					url : "regionselect.do",
					data : {
						action : "select_city",
						area : $(this).val()
					},
					success : function(result) {
		    			var json_obj = $.parseJSON(result);
						var result_table = "<option value='0'>請選擇城市</option>";
						if(json_obj.length>0){
							$.each(json_obj,function(i, item) {
								result_table+="<option value='"+item+"'>"+item+"</option>";
							});
			    			$("#selectRegion").html(result_table);
						}else{
							$("#selectRegion").html("<option value='0'>請先選擇國家</option>");
						}
					}
		    	});
	    		
	    	});
	    	$("#selectRegion").change(function(){
	    		$.ajax({
					type : "POST",
					url : "regionselect.do",
					data : {
						action : "select_CBD",
						area : $("#selectcountry").val(),
						city : $(this).val()
					},
					success : function(result) {
						
		    			var json_obj = $.parseJSON(result);
						var result_table = '<div style="float:left;">商圈：</div><div style="max-width:500px;word-break: break-all;float:left;">';
						if(json_obj.length>0){
							businessdistrict={};
							var midlng=0.0,midlat=0.0;
							$.each(json_obj,function(i, item) {
								businessdistrict[json_obj[i].name]={};
								businessdistrict[json_obj[i].name].n=i;
								businessdistrict[json_obj[i].name].name=json_obj[i].name;
								businessdistrict[json_obj[i].name].center={};
								businessdistrict[json_obj[i].name].center.lat=parseFloat(json_obj[i].lat);
								businessdistrict[json_obj[i].name].center.lng=parseFloat(json_obj[i].lng);
								midlat+=parseFloat(json_obj[i].lat);
								midlng+=parseFloat(json_obj[i].lng);
								if(i!=0) {
									result_table+= "、";
								}
								result_table += json_obj[i].name;
							});
							
							midlng/=json_obj.length;
							midlat/=json_obj.length;
							map.panTo(new google.maps.LatLng(midlat,midlng));
							map.setZoom(11);
							result_table += "</div>";
							$("#BD").html(result_table);
						}else{
							$("#BD").html("商圈：請選擇國家、城市。");
						}
					}
		    	});
	    	});
	    	
			$("#slider").slider({
				range: true,
		        min: 0,
		        max: 1,
		        step: 0.0001,
		        values: [0.3, 0.6],
		        slide: function (event, ui) {
		            var v0 = ui.values[0];
		            var v1 = ui.values[1];
		            var p1 = v0;
		            var p2 = v1 - v0;
		            var p3 = 1 - p1 - p2;
		            $("#rs1").val((p1 * 100).toFixed(2));
		            $("#rs2").val((p2 * 100).toFixed(2));
		            $("#rs3").val((p3 * 100).toFixed(2));
		        }
			});
			$("#regionselect").dialog({
				draggable : true, resizable : false, autoOpen : false,
				height : "auto", width : "auto", modal : true,
				show : {effect : "blind",duration : 300},
				hide : {effect : "fade",duration : 300},
			});
			$("#regionselect").show();

		    
		});
    </script>
    <script>
	    function do_region_analysis(){
	    	var warning = "";
			if($("#selectcountry").val()==0) {
				warning+="請選擇國家<br/>";
			}
			if($("#selectRegion").val()==0) {
				warning+="請選擇城市<br/>";
			}
			if($('#choose input[name="hee"]:checked').length==0) {
				warning+="請選擇產業<br/>";
			}
			if(warning.length>0){
				warningMsg('警告', warning);
				return;
			}
			
			var scoreSTR = "";
			$.ajax({
				type : "POST",
				url : "regionselect.do",
				async : false,
				data : {
					action : "call_web_service",
					country : $("#selectcountry").val(),
					region : $("#selectRegion").val(),
					industry : $('#choose input[name="hee"]:checked').val(),
					check1 : $('#choose input[name="check1"]:checked').length,
					check2 : $('#choose input[name="check2"]:checked').length,
					check3 : $('#choose input[name="check3"]:checked').length,
					check4 : $('#choose input[name="check4"]:checked').length,
					check5 : $('#choose input[name="check5"]:checked').length,
					check6 : $('#choose input[name="check6"]:checked').length,
					rs1 : $('#rs1').val(),
					rs2 : $('#rs2').val(),
					rs3 : $('#rs3').val(),
					score : ""
				},
				success : function(result) {
	 	    		for (var i = 0; i < all_BDs_region_select.length; i++) {   
	 	    			all_BDs_region_select[i].setMap(null);
	 	            }   
	 	    		all_BDs_region_select.length=0;
	 	    		
					var json_obj = $.parseJSON(result);
					var result_table = "";
					if(window.scenario_record){
						scenario_record("區位選擇",
							"['"+$("#selectcountry").val()+"','"+$("#selectRegion").val()+"','"
								+$('#choose input[name="hee"]:checked').val()+"',"
								+$('#choose input[name="check1"]:checked').length+","
								+$('#choose input[name="check2"]:checked').length+","
								+$('#choose input[name="check3"]:checked').length+","
								+$('#choose input[name="check4"]:checked').length+","
								+$('#choose input[name="check5"]:checked').length+","
								+$('#choose input[name="check6"]:checked').length+","
								+$('#rs1').val()+","+$('#rs2').val()+","+$('#rs3').val()+", "
								+result.replace(/"([^"]*)"/g, "'$1'")+"]");
					}
					$.each(json_obj,function(i, item) {
						scoreSTR += json_obj[i].City+ "," +json_obj[i].Score+";" ;
						draw_BDS(json_obj[i].City,(i+1)+"");
					});
					if(json_obj.length>0){
						$("#insert").html("對這"+json_obj.length+"個商圈")
						map.panTo(new google.maps.LatLng(businessdistrict[json_obj[0].City].center.lat,businessdistrict[json_obj[0].City].center.lng));
					}else{
						warningMsg('警告', "此分析有點困難，你安靜點讓我好好想想。<br/>半個小時後給你答案。");
					}								
				}
			});
			
			$.ajax({
				type : "POST",
				url : "regionselect.do",
				data : {
					action : "insert_Regionselect",
					country : $("#selectcountry").val(),
					region : $("#selectRegion").val(),
					industry : $('#choose input[name="hee"]:checked').val(),
					check1 : $('#choose input[name="check1"]:checked').length,
					check2 : $('#choose input[name="check2"]:checked').length,
					check3 : $('#choose input[name="check3"]:checked').length,
					check4 : $('#choose input[name="check4"]:checked').length,
					check5 : $('#choose input[name="check5"]:checked').length,
					check6 : $('#choose input[name="check6"]:checked').length,
					rs1 : $('#rs1').val(),
					rs2 : $('#rs2').val(),
					rs3 : $('#rs3').val(),
					score : scoreSTR
				},
				success : function(result) {
					//alert(result);
	    			if("success"==result){
						$("#choose").hide();
						$('#over').show();
	    			}else{
	    				warningMsg('警告', "產生異常問題，請重整");
	    			}
				}
	    	});
			
	    }

    </script>
    
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBSQDx-_LzT3hRhcQcQY3hHgX2eQzF9weQ&signed_in=true&libraries=places&callback=initMap">
	</script>
	<script src="js/mapFunction.js"></script>
	</div>
</div>

<jsp:include page="footer.jsp" flush="true"/>