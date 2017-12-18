<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/styles_map.css"/>
<link rel="stylesheet" href="css/styles_commonMethod.css"/>
<link rel="stylesheet" href="css/styles_subTitleMenuBox.css"/>
<link rel="stylesheet" href="css/styles_ben.css"/>
<link rel="stylesheet" href="css/styles.css"/>

<script type="text/javascript" src="js/mapFunctionPOI.js"></script>
<script type="text/javascript" src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/commonGrandBuild.js"></script>
<style>
	
	#search-condition select,#search-condition input[type="text"] { 
	     width: 220px; 
	     font-family: "微軟正黑體";
	} 
	.info_window{
		font-family: "微軟正黑體";
	}
	.content-wrap{ 
	 	margin-bottom: 0px; 
	 	margin-top:56px;
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
	
<h2 class="page-title">捷運人流氣象資訊</h2>
	<div class="search-result-wrap">
		<div id="map"></div>
		<div id='left-top-box'>
			<div id='menu-title-bar'>
				<div class="sbi-logo-line"></div>
				<span id='title' class='word-75pa'>查詢捷運類別</span>
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
									捷運站種類
								</td>
								<td>
									<select name="metro_type" id="metro_type">	
										<option value=''>請選擇</option>
										<option value='臺北捷運' >臺北捷運</option>
<!-- 										<option value='高雄捷運' >高雄捷運</option> -->
<!-- 										<option value='機場捷運' >機場捷運</option> -->
									</select>
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
	    function initMap() {
	    	
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
function clearMap(){
	if(window.global_markers){
		while(window.global_markers.length>0){
			global_markers.pop().setMap(null);
		}
	}
	if(window.global_infowindows){
		while(window.global_infowindows.length>0){
			global_infowindows.pop().close();
		}
	}
}
function preparePage(){
	
	$( "#accordion" ).accordion({
	    active: 0,
	    heightStyle: "content"
	});
	
}

function prepare_trigger(){
	
	$("#getmenu-btn").click(function() {
		$("#search-ui").slideToggle(200);
	});
	
	$("#clear-search").click(function(){
		$('#metro_type').val('');
		clearMap();
	});
	
	$("#do-search").click(function(){
		loading('now');
		$.ajax({
			type : "POST",
			url : "POI.do",
			async : false,
// 			beforeSend : function(){ loading('now'); },
			complete : function(){ loading('over'); },
			data : {
				action : "select_poi_of_metro",
				metro_type : $("#metro_type").val(),
				metro_name : ""
			},
			success : function(result) {
				clearMap();
				var json_obj = $.parseJSON(result);
				
				$.each(json_obj,function(i, item) {
					var marker = new google.maps.Marker({
					    position: {lat:+item["lat"],lng:+item["lng"]},
					    title: item["name"],
					    map: map
					});
					if(item["icon"].length>3){
						var poi_icon_url = "./imageIcon.do?action=getPoiIconPath"
							+ "&pic_name=" + encodeURI(
								b64EncodeUnicode(item["icon"])
							).replace(/\+/g,'%2b');
						
						marker.setIcon({
							url: poi_icon_url,
							scaledSize: new google.maps.Size(30, 30)
						});
					}
					
					
					var poi_type ="normal";
					poi_type = (item.type =="捷運" && item.address.indexOf("高雄市") == -1 &&  item.name.indexOf("機場捷運") == -1) ? "metro" : poi_type;
					poi_type =  item.type=="交流道" ?"interchange":poi_type;
					
					var poi_item = new POI_ITEMs(json_obj[i],poi_type);
					var infowindow = poi_item["infowindows"];
					
					google.maps.event.addListener(marker, "click", function(event) {
						loading('now');
						poi_item["prepareData"]();
						loading('over');
						var infowindow_open = infowindow.getMap();
					    if(infowindow_open !== null && typeof infowindow_open !== "undefined"){
					    	infowindow.close();
					    }else{
					    	poi_item["infowindowsPrepare"](infowindow);
					    	infowindow.open(marker.get('map'), marker);
					    }
			        });
					
					poi_item["googlemapEvent"](marker,infowindow);
					if(!window.global_markers){
						global_markers = [];
					}
					if(!window.global_infowindows){
						global_infowindows = [];
						
					}
					global_markers.push(marker);
					global_infowindows.push(infowindow);
				});
				//改變地圖center和zoom
				var avg_lat=0, avg_lng=0, avg_count=0;
				$.each(json_obj,function(i,item) {
					avg_lng+=+item["lng"];
					avg_lat+=+item["lat"];
					avg_count++;
				});
				
				if(avg_lat!=0){
					map.setCenter({lat:(avg_lat / avg_count),lng:(avg_lng / avg_count)});
					map.setZoom(12);
				}
				
			}
		});
		
	});
	
}

</script>
<jsp:include page="footer.jsp" flush="true"/>
