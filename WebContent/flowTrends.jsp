<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/styles_commonMethod.css"/>
<link rel="stylesheet" href="css/styles_subTitleMenuBox.css"/>
<link rel="stylesheet" href="css/styles_ben.css"/>
<!-- 捷運有換顏色 -->
<!-- 木柵線 #CC9900換#ECA960 -->
<!-- 藍線 #398AFC換#89BAFC -->
<!-- 綠線 #009900換#40C940 -->
<!-- 紅線 #FD5B56換#FD8B86 -->
<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>

<!--個案分析 大家去哪兒? -->
<style>/* ↓↓↓微調預設格式↓↓↓ */
	
	#search-condition select,#search-condition input[type="text"] { 
	     width: 220px; 
	     font-family: "微軟正黑體";
	} 
	#search-ans td:nth-child(1){
		word-break:keep-all;
	}
	#search-ans td:nth-child(2){
		word-break:break-all;
		max-width:280px;
	}
 	#search-ans tr{ 
 		border-bottom:1px solid #dfdfdf; 
 	} 
	.info_window{
		font-family: "微軟正黑體";
	}

	.content-wrap{ 
	 	margin-bottom: 0px; 
	 	padding-bottom: 30px; 
	     overflow: hidden; 
	     width: 100%; 
	} 
	.search-result-wrap{
		padding: 2px 5px 0px 5px; 
		margin-bottom: 0px;
		height: 100%;
	}
	#map{
		height: 100%;
	}
</style>

<script>
	var map;
	$(function(){
		preparePage();
		prepare_trigger();
	});
</script>

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	
<h2 class="page-title">人流趨勢分析</h2>
	<div class="search-result-wrap">
		<div id="map"></div>
		<div id='left-top-box' style=';'>
			<div id='menu-title-bar'>
				<div class="sbi-logo-line"></div>
				<span id='title' class='word-75pa'>各類人流資料</span>
				<span class='word-75pa'><span class='word-75pa'>查詢系統</span></span>
				<span id='getmenu-btn' class='fa fa-search func'></span>
			</div>
			<div id="search-ui" style='display:none;'>
				<div id="search-title" style='padding: 1px 12px;text-align:center;'></div>
			    <div id="accordion">
				  <h3>設定查詢條件</h3>
				  	<div>
				  		<table id='search-condition' class='with-padding' style="width: 100%;">
							<tr>
								<td>
									分析捷運站
								</td>
								<td>
									<select name="ID" id="metroStation">	
										<option style="background-color: #eee;" value=''>請選擇</option>				
				                 	 	<optgroup style="background-color: #ECA960;" label="BR 文湖線">
											<option style="background-color: #ECA960;" value="BR01-019">BR01
												動物園</option>
											<option style="background-color: #ECA960;" value="BR02-018">BR02
												木柵</option>
											<option style="background-color: #ECA960;" value="BR03-017">BR03
												萬芳社區</option>
											<option style="background-color: #ECA960;" value="BR04-016">BR04
												萬芳醫院</option>
											<option style="background-color: #ECA960;" value="BR05-015">BR05
												辛亥</option>
											<option style="background-color: #ECA960;" value="BR06-014">BR06
												麟光</option>
											<option style="background-color: #ECA960;" value="BR07-013">BR07
												六張犁</option>
											<option style="background-color: #ECA960;" value="BR08-012">BR08
												科技大樓</option>
											<option style="background-color: #ECA960;" value="BR09-011">BR09
												大安</option>
											<option style="background-color: #ECA960;" value="BR10-010">BR10
												忠孝復興</option>
											<option style="background-color: #ECA960;" value="BR11-009">BR11
												南京復興</option>
											<option style="background-color: #ECA960;" value="BR12-008">BR12
												中山國中</option>
											<option style="background-color: #ECA960;" value="BR13-007">BR13
												松山機場</option>
											<option style="background-color: #ECA960;" value="BR14-021">BR14
												大直</option>
											<option style="background-color: #ECA960;" value="BR15-022">BR15
												劍南路</option>
											<option style="background-color: #ECA960;" value="BR16-023">BR16
												西湖</option>
											<option style="background-color: #ECA960;" value="BR17-024">BR17
												港墘</option>
											<option style="background-color: #ECA960;" value="BR18-025">BR18
												文德</option>
											<option style="background-color: #ECA960;" value="BR19-026">BR19
												內湖</option>
											<option style="background-color: #ECA960;" value="BR20-027">BR20
												大湖公園</option>
											<option style="background-color: #ECA960;" value="BR21-028">BR21
												葫洲</option>
											<option style="background-color: #ECA960;" value="BR22-029">BR22
												東湖</option>
											<option style="background-color: #ECA960;" value="BR23-030">BR23
												南港軟體園區</option>
											<option style="background-color: #ECA960;" value="BR24-031">BR24
												南港展覽館</option>
										</optgroup>
										<optgroup style="background-color: #FD5B56;" label="R 淡水信義線">
											<option style="background-color: #FD5B56;" value="R02-099">R02
												象山</option>
											<option style="background-color: #FD5B56;" value="R03-100">R03
												台北101/世貿</option>
											<option style="background-color: #FD5B56;" value="R04-101">R04
												信義安和</option>
											<option style="background-color: #FD5B56;" value="R05-011">R05
												大安</option>
											<option style="background-color: #FD5B56;" value="R06-103">R06
												大安森林公園</option>
											<option style="background-color: #FD5B56;" value="R07-134">R07
												東門</option>
											<option style="background-color: #FD5B56;" value="R08-042">R08
												中正紀念堂</option>
											<option style="background-color: #FD5B56;" value="R09-050">R09
												台大醫院</option>
											<option style="background-color: #FD5B56;" value="R10-051">R10
												台北車站</option>
											<option style="background-color: #FD5B56;" value="R11-053">R11
												中山</option>
											<option style="background-color: #FD5B56;" value="R12-054">R12
												雙連</option>
											<option style="background-color: #FD5B56;" value="R13-055">R13
												民權西路</option>
											<option style="background-color: #FD5B56;" value="R14-056">R14
												圓山</option>
											<option style="background-color: #FD5B56;" value="R15-057">R15
												劍潭</option>
											<option style="background-color: #FD5B56;" value="R16-058">R16
												士林</option>
											<option style="background-color: #FD5B56;" value="R17-059">R17
												芝山</option>
											<option style="background-color: #FD5B56;" value="R18-060">R18
												明德</option>
											<option style="background-color: #FD5B56;" value="R19-061">R19
												石牌</option>
											<option style="background-color: #FD5B56;" value="R20-062">R20
												唭哩岸</option>
											<option style="background-color: #FD5B56;" value="R21-063">R21
												奇岩</option>
											<option style="background-color: #FD5B56;" value="R22-064">R22
												北投</option>
											<option style="background-color: #FD5B56;" value="R22A-065">R22A
												新北投</option>
											<option style="background-color: #FD5B56;" value="R23-066">R23
												復興崗</option>
											<option style="background-color: #FD5B56;" value="R24-067">R24
												忠義</option>
											<option style="background-color: #FD5B56;" value="R25-068">R25
												關渡</option>
											<option style="background-color: #FD5B56;" value="R26-069">R26
												竹圍</option>
											<option style="background-color: #FD5B56;" value="R27-070">R27
												紅樹林</option>
											<option style="background-color: #FD5B56;" value="R28-071">R28
												淡水</option>
										</optgroup>
										<optgroup style="background-color: #40C940;" label="G 松山新店線">
											<option style="background-color: #40C940;" value="G01-033">G01
												新店</option>
											<option style="background-color: #40C940;" value="G02-034">G02
												新店區公所</option>
											<option style="background-color: #40C940;" value="G03-035">G03
												七張</option>
											<option style="background-color: #40C940;" value="G03A-032">G03A
												小碧潭</option>
											<option style="background-color: #40C940;" value="G04-036">G04
												大坪林</option>
											<option style="background-color: #40C940;" value="G05-037">G05
												景美</option>
											<option style="background-color: #40C940;" value="G06-038">G06
												萬隆</option>
											<option style="background-color: #40C940;" value="G07-039">G07
												公館</option>
											<option style="background-color: #40C940;" value="G08-040">G08
												台電大樓</option>
											<option style="background-color: #40C940;" value="G09-041">G09
												古亭</option>
											<option style="background-color: #40C940;" value="G10-042">G10
												中正紀念堂</option>
											<option style="background-color: #40C940;" value="G11-043">G11
												小南門</option>
											<option style="background-color: #40C940;" value="G12-086">G12
												西門</option>
											<option style="background-color: #40C940;" value="G13-105">G13
												北門</option>
											<option style="background-color: #40C940;" value="G14-053">G14
												中山</option>
											<option style="background-color: #40C940;" value="G15-132">G15
												松江南京</option>
											<option style="background-color: #40C940;" value="G16-009">G16
												南京復興</option>
											<option style="background-color: #40C940;" value="G17-109">G17
												台北小巨蛋</option>
											<option style="background-color: #40C940;" value="G18-110">G18
												南京三民</option>
											<option style="background-color: #40C940;" value="G19-111">G19
												松山</option>
										</optgroup>
										<optgroup style="background-color: #FFCC66;" label="O 中和新蘆線">
											<option style="background-color: #FFCC66;" value="O01-048">O01
												南勢角</option>
											<option style="background-color: #FFCC66;" value="O02-047">O02
												景安</option>
											<option style="background-color: #FFCC66;" value="O03-046">O03
												永安市場</option>
											<option style="background-color: #FFCC66;" value="O04-045">O04
												頂溪</option>
											<option style="background-color: #FFCC66;" value="O05-041">O05
												古亭</option>
											<option style="background-color: #FFCC66;" value="O06-134">O06
												東門</option>
											<option style="background-color: #FFCC66;" value="O07-089">O07
												忠孝新生</option>
											<option style="background-color: #FFCC66;" value="O08-132">O08
												松江南京</option>
											<option style="background-color: #FFCC66;" value="O09-131">O09
												行天宮</option>
											<option style="background-color: #FFCC66;" value="O10-130">O10
												中山國小</option>
											<option style="background-color: #FFCC66;" value="O11-055">O11
												民權西路</option>
											<option style="background-color: #FFCC66;" value="O12-128">O12
												大橋頭</option>
											<option style="background-color: #FFCC66;" value="O13-127">O13
												台北橋</option>
											<option style="background-color: #FFCC66;" value="O14-126">O14
												菜寮</option>
											<option style="background-color: #FFCC66;" value="O15-125">O15
												三重</option>
											<option style="background-color: #FFCC66;" value="O16-124">O16
												先嗇宮</option>
											<option style="background-color: #FFCC66;" value="O17-123">O17
												頭前庄</option>
											<option style="background-color: #FFCC66;" value="O18-122">O18
												新莊</option>
											<option style="background-color: #FFCC66;" value="O19-121">O19
												輔大</option>
											<option style="background-color: #FFCC66;" value="O20-180">O20
												丹鳳</option>
											<option style="background-color: #FFCC66;" value="O21-179">O21
												迴龍</option>
											<option style="background-color: #FFCC66;" value="O50-178">O50
												三重國小</option>
											<option style="background-color: #FFCC66;" value="O51-177">O51
												三和國中</option>
											<option style="background-color: #FFCC66;" value="O52-176">O52
												徐匯中學</option>
											<option style="background-color: #FFCC66;" value="O53-175">O53
												三民高中</option>
											<option style="background-color: #FFCC66;" value="O54-174">O54
												蘆洲</option>
										</optgroup>
										<optgroup style="background-color: #89BAFC;" label="BL 板南線">
											<option style="background-color: #89BAFC;" value="BL01-076">BL01
												頂埔</option>
											<option style="background-color: #89BAFC;" value="BL02-077">BL02
												永寧</option>
											<option style="background-color: #89BAFC;" value="BL03-078">BL03
												土城</option>
											<option style="background-color: #89BAFC;" value="BL04-079">BL04
												海山</option>
											<option style="background-color: #89BAFC;" value="BL05-080">BL05
												亞東醫院</option>
											<option style="background-color: #89BAFC;" value="BL06-081">BL06
												府中</option>
											<option style="background-color: #89BAFC;" value="BL07-082">BL07
												板橋</option>
											<option style="background-color: #89BAFC;" value="BL08-083">BL08
												新埔</option>
											<option style="background-color: #89BAFC;" value="BL09-084">BL09
												江子翠</option>
											<option style="background-color: #89BAFC;" value="BL10-085">BL10
												龍山寺</option>
											<option style="background-color: #89BAFC;" value="BL11-086">BL11
												西門</option>
											<option style="background-color: #89BAFC;" value="BL12-051">BL12
												台北車站</option>
											<option style="background-color: #89BAFC;" value="BL13-088">BL13
												善導寺</option>
											<option style="background-color: #89BAFC;" value="BL14-089">BL14
												忠孝新生</option>
											<option style="background-color: #89BAFC;" value="BL15-010">BL15
												忠孝復興</option>
											<option style="background-color: #89BAFC;" value="BL16-091">BL16
												忠孝敦化</option>
											<option style="background-color: #89BAFC;" value="BL17-092">BL17
												國父紀念館</option>
											<option style="background-color: #89BAFC;" value="BL18-093">BL18
												市政府</option>
											<option style="background-color: #89BAFC;" value="BL19-094">BL19
												永春</option>
											<option style="background-color: #89BAFC;" value="BL20-095">BL20
												後山埤</option>
											<option style="background-color: #89BAFC;" value="BL21-096">BL21
												昆陽</option>
											<option style="background-color: #89BAFC;" value="BL22-097">BL22
												南港</option>
											<option style="background-color: #89BAFC;" value="BL23-031">BL23
												南港展覽館</option>
										</optgroup>
								</select>
								</td>
							</tr>
							<tr>
								<td>
									捷運站定位
								</td>
								<td>
									<input type="radio" id='from_mrt_name' name='metro_posi' value='from_mrt_name'>
									<label for="from_mrt_name"><span class="form-label">起站<!-- 大家去哪兒？--></span></label>
									<input type="radio" id='to_mrt_name' name='metro_posi' value='to_mrt_name'>
									<label for="to_mrt_name"><span class="form-label">迄站<!-- 大家從哪來？--></span></label>
								</td>
							</tr>
							<tr class="header-line">
								<td colspan='2'>
									<a class='btn btn-darkblue' id='do-search' >查詢</a>
									<a class='btn btn-gray' id='clear-search'>清除</a>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	
	
    <script>
    var rs_markers=[];
	    function initMap() {
			// Create the map.
			map = new google.maps.Map(document.getElementById('map'), {
				panControl: true,
			    zoomControl: true,
			    mapTypeControl: false,
			    scaleControl: true,
			    streetViewControl: true,
			    overviewMapControl: true,
			    zoom: 7,
				center: {lat: 23.598321171324468, lng: 120.97802734375}
			});
			map.addListener('zoom_changed', function() {
				if(+map.getZoom()<2){
					map.setZoom(2);
				}
			});
			
   		}
    </script>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8QEQE4TX2i6gpGIrGbTsrGrRPF23xvX4&signed_in=true&libraries=places,drawing&callback=initMap"></script> 
	</div>
</div>

<script>
function preparePage(){
	$( "#accordion" ).accordion({
	    active: 0,
	    heightStyle: "content"
	});
	$( "#menu" ).menu();
	
}

function prepare_trigger(){
	
	$("#getmenu-btn").click(function() {
		$("#search-ui").slideToggle(200);
	});
	
	$("#clear-search").click(function(){
		$('input[name="metro_posi"]:checked').prop("checked",false);
		$('#metroStation').val('');
		if(window.global_marker){
			global_marker.setMap(null);
		}
		if(window.global_infowindow){
			global_infowindow.close();
		}
	});
	
	$("#do-search").click(function(){
		var warning = "";
		if($("#metroStation").val()==""){
			warning += "<br>　請選擇&nbsp;'分析捷運站'<br>";
		}
		if($('input[name="metro_posi"]:checked').length==0) {
			warning += "<br>　請選擇&nbsp;'捷運站定位'<br>";
		}
		if(warning.length>0){
			warningMsg("系統提示",warning);
			return ;
		}
		loading('now');
		
		$.ajax({
	 		type: "GET",
	 		url : "trendsAroundMetro.do",
	 		data : {
	 			action : "select_metro_from_and_to",
	 			metro_name : $("#metroStation option:selected").text().split("\t").pop(),
	 			to_and_from_type : $('input[name="metro_posi"]:checked').val(),
	 		},complete : function ( a,b ) {
	 			loading('over');
	 			if(b!="success"){
	 				warningMsg("系統訊息","發生未知錯誤，請通知系統管理員。")
	 			}
	 		},success : function ( result ) {
	 			var type = $('input[name="metro_posi"]:checked').val();
				var weekday_zh = ["日","一","二","三","四","五","六"];
	 			var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i, item) {
					var type_verb_name = "" , type_total = 0 ;
					if(type=="to_mrt_name"){
						json_obj[i]["title"]='<table style="margin:8px;"><tbody><tr><td colspan="3">　　捷運　"'+item["metro_name"].replace("站","")+"站"+'"　人口變化</td></tr>'
											+'<tr><td>資料日期：</td><td  colspan="2">'+item["flow_day"]+'('+weekday_zh[item["weekday"]]+')　'+item["flow_time"]+'時</td></tr>'
											+'<tr><td>出站人數：</td><td  colspan="2">'+item["get_out_metro"]+'</td></tr>';
						type_total = item["get_out_metro"];
						json_obj[i]["title"] += '<tr><td colspan="3" style="padding-top:12px;">　在"'+item["metro_name"].replace("站","")+"站"+'"&nbsp;出站人口來自:　</td></tr>'
												+'<tr><td>第一名：</td><td>來自&nbsp;<b>'+item["rank1_name"]+'</b></td><td>'+'　&nbsp;'+item["rank1_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank1_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'		
												+'<tr><td>第二名：</td><td>來自&nbsp;<b>'+item["rank2_name"]+'</b></td><td>'+'　&nbsp;'+item["rank2_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank2_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第三名：</td><td>來自&nbsp;<b>'+item["rank3_name"]+'</b></td><td>'+'　&nbsp;'+item["rank3_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank3_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第四名：</td><td>來自&nbsp;<b>'+item["rank4_name"]+'</b></td><td>'+'　&nbsp;'+item["rank4_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank4_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第五名：</td><td>來自&nbsp;<b>'+item["rank5_name"]+'</b></td><td>'+'　&nbsp;'+item["rank5_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank5_flow"]*100/type_total).toFixed(2))+'%)</td></tr>';
												+'</tbody></table>';
					}else if(type=="from_mrt_name"){
						json_obj[i]["title"]='<table style="margin:8px;"><tbody><tr><td colspan="3">　　捷運　"'+item["metro_name"].replace("站","")+"站"+'"　人口變化</td></tr>'
						+'<tr><td>資料日期：</td><td  colspan="2">'+item["flow_day"]+'('+weekday_zh[item["weekday"]]+')　'+item["flow_time"]+'時</td></tr>'
						+'<tr><td>進站人數：</td><td  colspan="2">'+item["go_into_metro"]+'</td></tr>';
						type_total = item["go_into_metro"];
						json_obj[i]["title"] += '<tr><td colspan="3" style="padding-top:12px;">　在 "'+item["metro_name"].replace("站","")+"站"+'"&nbsp;進站人口前往:　</td></tr>'
												+'<tr><td>第一名：</td><td>前往&nbsp;<b>'+item["rank1_name"]+'</b></td><td>'+'　&nbsp;'+item["rank1_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank1_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'		
												+'<tr><td>第二名：</td><td>前往&nbsp;<b>'+item["rank2_name"]+'</b></td><td>'+'　&nbsp;'+item["rank2_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank2_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第三名：</td><td>前往&nbsp;<b>'+item["rank3_name"]+'</b></td><td>'+'　&nbsp;'+item["rank3_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank3_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第四名：</td><td>前往&nbsp;<b>'+item["rank4_name"]+'</b></td><td>'+'　&nbsp;'+item["rank4_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank4_flow"]*100/type_total).toFixed(2))+'%)</td></tr>'
												+'<tr><td>第五名：</td><td>前往&nbsp;<b>'+item["rank5_name"]+'</b></td><td>'+'　&nbsp;'+item["rank5_flow"]+'&nbsp;人&nbsp;('+(new Number(item["rank5_flow"]*100/type_total).toFixed(2))+'%)</td></tr>';
												+'</tbody></table>';
					}else{
						console.log("stange encount");
						return;
					}
				});
				
				draw_metro_with_human_trends(type,json_obj);
	 		}
		});
		
		
	});
	
}

function draw_metro_with_human_trends(type, flow_data_obj ){
	$("#search-ui").slideToggle(200);
	var uuid = UUID();
	var title ="",metro_name ="",yAxis_name="",yAxis_index = "",yScale_construct="";
	var loca={lat:null,lng:null};
	$.each(flow_data_obj,function(i, item) {
		loca["lat"] = +item["lat"];
		loca["lng"] = +item["lng"];
		metro_name = item["metro_name"];
	});
	
	map.setCenter({lat:loca["lat"]+0.05,lng:loca["lng"]});
	map.setZoom(13);
	if(type=="to_mrt_name"){
		title="'"+metro_name.replace("站","")+"站'&nbsp;出站人數"
		yAxis_name = "人口(人)";
		yAxis_index = "get_out_metro";
		yScale_construct = ("d3.scale.linear().domain([0,"+d3.max(flow_data_obj,function(d){return +d["get_out_metro"];})+"]).range([height - margin.top - margin.bottom,0])");
	}else if(type=="from_mrt_name"){
		title="'"+metro_name.replace("站","")+"站'&nbsp;進站人數";
		yAxis_name = "人口(人)";
		yAxis_index = "go_into_metro";
		yScale_construct = ("d3.scale.linear().domain([0,"+d3.max(flow_data_obj,function(d){return +d["go_into_metro"];})+"]).range([height - margin.top - margin.bottom,0])");
	}
	
	var marker = new google.maps.Marker({
	    position: loca,
	    title: title,
	    map: map
	});
	
	var poi_icon_url = "./imageIcon.do?action=getPoiIconPath"
		+ "&pic_name="
		+ encodeURI(b64EncodeUnicode("poi_10603_metro.png")).replace(/\+/g,'%2b');
	marker.setIcon({
		url: poi_icon_url,
		scaledSize: new google.maps.Size(30, 30)
	});
	
	var weekday_select = '<select id="select_'+uuid+'" style="margin:4px;"><option value="0">星期日</option><option value="1">星期一</option><option value="2">星期二</option><option value="3">星期三</option><option value="4">星期四</option><option value="5">星期五</option><option value="6">星期六</option></select>';
	var infowindow = new google.maps.InfoWindow({content: 
		('<table class="info_window keyValueData">'
			+'<tr><th style="font-size:2em;">'+title+'</th></tr>'
			+'<tr><td style="text-align: right;">'+weekday_select+'<div id="data_chart_'+uuid+'"style="width:508px;height:308px"></div></td></tr>'
		+'</table>')});
	
	if(window.global_marker){
		global_marker.setMap(null);
	}
	if(window.global_infowindow){
		global_infowindow.close();
	}
	global_marker = marker ;
	global_infowindow = infowindow ;
	
	var dataSet = flow_data_obj;
	google.maps.event.addListener(infowindow, 'domready', function() {
		$('#select_'+uuid).val(new Date().getDay());
		var assignChartConfig = {
	 		"elementID":"#data_chart_"+uuid,
	 		"svg_width":"500",
	 		"svg_height":"300",
	 		"svg_margin":{top:30,right:20,bottom:50,left:60},
	 		"bar_width": "13",
	 		"title_name":"",
	 		"sub_title_name":"",
	 		"xAxis_name":"",
	 		"yAxis_name":yAxis_name,
	 		"xAxis_index":"flow_time",
	 		"yAxis_index":yAxis_index,
	 		"xScale_construct":"d3.scale.linear().domain([5,24]).range([0, width - margin.left - margin.right]);",
	 		"yScale_construct":yScale_construct,
	 		"fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}",
	 		"xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([6,12,18,24]).tickFormat(function(d){return d+\"時\";});",
	 		"yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"
	 	};
		
		drawBarChart_forTrend(
				dataSet.filter(function(d){
				return d["weekday"]==(new Date().getDay()) && d["flow_time"] != "00" && d["flow_time"] != "01";
			}),assignChartConfig
		);
		$('#select_'+uuid).change(function(){
			drawBarChart_forTrend(
					dataSet.filter(function(d){
					return d["weekday"]==$('#select_'+uuid).val()  && d["flow_time"] != "00" && d["flow_time"] != "01";
				}),assignChartConfig
			);
		});
	});
	google.maps.event.addListener(marker, "click", function(event) {
		var infowindow_open = infowindow.getMap();
	    if(infowindow_open !== null && typeof infowindow_open !== "undefined"){
	    	infowindow.close();
	    }else{
	    	infowindow.open(marker.get('map'), marker);
	    }
    });
	
	infowindow.open(map, marker);
}


function drawBarChart_forTrend(data,assignChartConfig){
// 	console.log(assignChartConfig);
	console.log("call drawBarChart");
	
	if(assignChartConfig==null){
		assignChartConfig={};
	}
	chartConfig={};
	
	if( assignChartConfig["xAxis_index"]==null
		||assignChartConfig["yAxis_index"]==null
		||assignChartConfig["xScale_construct"]==null
		||assignChartConfig["yScale_construct"]==null
		||assignChartConfig["xAxis_construct"]==null
		||assignChartConfig["yAxis_construct"]==null
	){
		console.log(assignChartConfig["xAxis_index"]);
		console.log(assignChartConfig["yAxis_index"]);
		console.log(assignChartConfig["xScale_construct"]);
		console.log(assignChartConfig["yScale_construct"]);
		console.log(assignChartConfig["xAxis_construct"]);
		console.log(assignChartConfig["yAxis_construct"]);
		console.log("使用drawBarChart時必填欄位未給定。不執行。");
		return;
	}
	
	chartConfig["elementID"] = assignChartConfig["elementID"] || "#data_chart";
	chartConfig["svg_width"] = assignChartConfig["svg_width"] || "400";
	chartConfig["svg_height"] = assignChartConfig["svg_height"] || "200";
	chartConfig["svg_margin"] = assignChartConfig["svg_margin"] || {top:10,right:10,bottom:20,left:10};
	chartConfig["bar_width"] = assignChartConfig["bar_width"] || "10";
	chartConfig["title_name"] = assignChartConfig["title_name"] || "";
	chartConfig["sub_title_name"] = assignChartConfig["sub_title_name"] || "";
	chartConfig["xAxis_name"] = assignChartConfig["xAxis_name"] || "";
	chartConfig["yAxis_name"] = assignChartConfig["yAxis_name"] || null;
	chartConfig["fScale_construct"] = assignChartConfig["fScale_construct"] || "d3.scale.category20()";
	chartConfig["xAxis_index"] = assignChartConfig["xAxis_index"] ;
	chartConfig["yAxis_index"] = assignChartConfig["yAxis_index"] ;
	chartConfig["xScale_construct"] = assignChartConfig["xScale_construct"] ;
	chartConfig["yScale_construct"] = assignChartConfig["yScale_construct"] ;
	chartConfig["xAxis_construct"] = assignChartConfig["xAxis_construct"] ;
	chartConfig["yAxis_construct"] = assignChartConfig["yAxis_construct"] ;
	
	chartConfig["xAxis_index_offset"] = assignChartConfig["xAxis_index_offset"] ;
	
	var width = chartConfig["svg_width"];
	var height = chartConfig["svg_height"];
	var margin = chartConfig["svg_margin"];
	var svg ;
	var build_round;
	
	if($(chartConfig["elementID"]+" > svg").length==0){
		if($(chartConfig["elementID"]).length==0){
			$("body").append($("<div/>",{"id":chartConfig["elementID"].replace("#","")}));
		}
		svg = d3.select(chartConfig["elementID"]).append("svg")
				.attr("width",chartConfig["svg_width"])
				.attr("height",chartConfig["svg_height"]);
		svg.append("g").append("text").text(chartConfig["title_name"])
				.style({
					"text-anchor":"middle",
					"font-size":"30px",
				})
				.attr("x", chartConfig["svg_width"] / 2).attr("y", 30);
		svg.append("g").append("text").text(chartConfig["sub_title_name"])
				.attr("fill", "#666")
				.style({
					"text-anchor":"middle",
					"font-size":"18px",
				})
				.attr("x", chartConfig["svg_width"] / 2).attr("y", 56);
		
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "rects");
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "paths");
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "circles");
		
		svg.append("g").attr("id", "axisX").append("text");
		svg.append("g").attr("id", "axisY").append("text");
		build_round=true;
	}else{
		svg = d3.select(chartConfig["elementID"]+" > svg");
		build_round=false;
	}
	
	bind();
	render();
	
	function bind(){
		var selection_1 = d3.select(chartConfig["elementID"]+" > svg > #rects").selectAll("rect").data(data);
	        selection_1.enter().append("rect");
	        selection_1.exit().remove();
	    var selection_2 = d3.select(chartConfig["elementID"]+" > svg > #paths").selectAll("path").data(data);
	        selection_2.enter().append("path");
	        selection_2.exit().remove();
	    var selection_3 = d3.select(chartConfig["elementID"]+" > svg > #circles").selectAll("circle").data(data);
	        selection_3.enter().append("circle");
	        selection_3.exit().remove();
	}
	function render(){
		var xScale = eval(chartConfig["xScale_construct"]);//d3.scale.linear().domain([-1,25]).range([0, width - margin.left - margin.right]); 
		var yScale = eval(chartConfig["yScale_construct"]);
		var fScale = eval(chartConfig["fScale_construct"]);
		
		var xAxis = eval(chartConfig["xAxis_construct"]);
		var yAxis = eval(chartConfig["yAxis_construct"]);
			
		var colorTmp ,titleTmp="";
		d3.select(chartConfig["elementID"]+" > svg").selectAll("rect")
			.attr({	
				"tmp_title" : function(d){
					return show_if_significant(d["title"],"#P#");
				}
			})
			.transition()
		    .duration(build_round?0:500)
			.attr("transform","translate(" + margin.left + "," + ( 0 ) + ")")
			.attr({	
				'x': function(d, i){
					return xScale(d[chartConfig["xAxis_index"]])
						- chartConfig["bar_width"] * 0.5;
				},
				'y': function(d){
					if(+d[chartConfig["yAxis_index"]]>0){
						return margin.top + yScale(d[chartConfig["yAxis_index"]]);
					}else{
						return margin.top + yScale(0);
					}
				},
				'rx':'2',
				'width': chartConfig["bar_width"],
				'height':function(d){
					if(+d[chartConfig["yAxis_index"]]>0){
						return (yScale(0)-yScale(d[chartConfig["yAxis_index"]])+1);
					}else{
						return yScale(d[chartConfig["yAxis_index"]])-yScale(0);
					}
				},
				'fill': function(d){
					return fScale(chartConfig["yAxis_index"]);
				},
				"class" : function(d){ 
					return show_if_significant(d["title"],"tips_on_barchart");
				}
		    });
		
		d3.select(chartConfig["elementID"]+" > svg").selectAll("rect")
		    .on("mouseover",function(d){
		    	 colorTmp = d3.select(this).attr("fill");
	             d3.select(this).attr({fill:"gold"});
				 
				 var tooltip = "<div id='tooltip'>"+  $(this).attr("tmp_title") +"<\/div>";
				 $(".content-wrap").append(tooltip);
				 $("#tooltip").css({"top": (d3.event.pageY+20) + "px","left": (d3.event.pageX+10)  + "px"}).show();
		    	 $("body").css("overflow","hidden");
		    }).on("mouseout",function(d){
                d3.select(this).attr({fill:colorTmp});
		        $("#tooltip").remove();
		    }).on("mousemove",function(d){
		    	$("#tooltip").css({"top": (d3.event.pageY+20) + "px","left": (d3.event.pageX+10)  + "px"});
		    });
		
		if(chartConfig["xAxis_name"]!=null){
			d3.select(chartConfig["elementID"]+" > svg").select("g#axisX")
					.attr("class","axis")
					.attr("transform","translate(" + margin.left + "," + ( margin.top + yScale(0)) + ")")
					.call(xAxis)
				.select('text')
					.text(chartConfig["xAxis_name"])
					.attr("class","bigger-word")
					.attr('transform', 'translate('+ (width-margin.left * 2 +20) +', '+(40)+')')
					.style("text-anchor", "middle");
		}	
		if(chartConfig["yAxis_name"]!=null){
			d3.select(chartConfig["elementID"]+" > svg").select("g#axisY")
					.attr("class","axis")
					.attr("transform","translate(" + (margin.left)+ "," + (margin.top) + ")")
					.call(yAxis)
			  .select('text')
					.text(chartConfig["yAxis_name"])
					.attr("class","bigger-word")
					.attr('transform', 'translate(-30, -8)');
		}
		
	}
}

</script>
<jsp:include page="footer.jsp" flush="true"/>
