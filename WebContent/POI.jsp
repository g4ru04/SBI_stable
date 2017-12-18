<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/jquery.dataTables.min.css"/>
<link rel="stylesheet" href="css/styles.css"/>
<link rel="stylesheet" href="css/jquery-ui.min.1.12.1.css"/>
<link rel="stylesheet" href="css/styles_map.css"/>
<link rel="stylesheet" href="importPKG/fancy-tree/skin-xp/ui.fancytree.css"/>
<link rel="stylesheet" href="css/styles_commonMethod.css"/>

<script type="text/javascript" src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<!-- 以下js為 import 畫POImenu和畫WKT的js  -->
<script type="text/javascript" src="importPKG/fancy-tree/jquery.fancytree.js"></script>
<script type="text/javascript" src="importPKG/oriSBI/js/wicket.js"></script>
<script type="text/javascript" src="importPKG/oriSBI/js/wicket-gmap3.js"></script>
<!-- 以下為自寫的map相關和POIMenu function -->
<script type="text/javascript" src="js/mapFunction.js"></script>
<script type="text/javascript" src="js/mapFunctionPOI.js"></script>
<script type="text/javascript" src="js/mapFunctionEnvAnal.js"></script>
<script type="text/javascript" src="js/menu_of_POI.js"></script>
<script type="text/javascript" src="js/commonGrandBuild.js"></script>
<style>
	#region_select,#warning{
		font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
	}
	#search-box-input-view{
		position: fixed;
		left: 140px;
		top: 62px;
		width: 300px;
		box-shadow:  0 2px 6px rgba(0, 0, 0, 0.3);
		z-index:2;
	}
	#search-box-enter{
		position: fixed;
		left: 450px;
		top: 66px;
		box-shadow:  0 2px 6px rgba(0, 0, 0, 0.3);
	}
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
		padding-bottom:18px;
		box-shadow:1px 1px 7px #888;
	}
	.poi-statistic-table .info_window, .poi-villageInfo-table .info_window{
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
</style>

<script>
var result="";
var map;
var ebookpath="<%=getServletConfig().getServletContext().getInitParameter("ebookpath") %>";

	$(function(){
		setTimeout(function(){
// 			map.setZoom(15);
// 			map.setCenter({lat:25.046671138745502,lng:121.44415235519409});
// 			$.ajax({
// 				type : "POST",
// 				url : "POI.do",
// 	//	 		async : false,
// 				data : {
// 					action : "select_poi_statistic_circle_villageinfo",
// // 					center_lat : "24.87646991083154",
// // 					center_lng : "121.4208984375",
// // 					radius : "5000.20"
// 					center_lat : "25.060099246452303",
// 					center_lng : "121.53762817382812",
// 					radius : "1000.20"
// 				},
// 				success : function(result) {
// 					var json_obj = $.parseJSON(result);
// 					$.each(json_obj,function(i, item) {
// 						var wkt = new Wkt.Wkt();
// 						wkt.read(item.geom);
// 			            var config = {
// 			                fillColor: '#F0F0F0',
// 			                strokeColor: '#5C5C5C',
// 			                fillOpacity: 0.5,
// 			                strokeOpacity: 1,
// 			                strokeWeight: 1,
// 			            };
			            
// 			            var polygen = wkt.toObject(config);
// 			            function setVillagePolygenInfo(polygen,item){
// 			            	if(!window.village_polygen){
// 				            	village_polygen=[];
// 				            }
// 			            	polygen.setMap(map);
// 			            	village_polygen.push(polygen);
			            	
// 			            	var villageInfoTable = "<table class='info_window' style='margin-left:25px;'>"
// 			            		+"<tr><th colspan='2'>"+item.name+"</th></tr>"
// 			            		+"<tr><td style='padding-left:0px;'>"+item.geom_info.replace(/：/g,":</td><td>").replace(/\n/g,"</td></tr><tr><td>")+"</td></tr>"
// 			            		+"</table>";
// 			            	var infowindow = new google.maps.InfoWindow({ content : villageInfoTable });
// 					        var infoMarker = new google.maps.Marker({
// 					            position: new google.maps.LatLng(item.lat,item.lng),
// 					            icon: {
// 					                path: google.maps.SymbolPath.CIRCLE,
// 					                scale: 0
// 					            },
// 					            map: map
// 					        });
			            	
// 			            	google.maps.event.addListener(polygen, 'click', function () {
// 			            		infowindow.open(map, infoMarker);
// 	        	            });
// 			            }
			            
			            
// 			            if (Wkt.isArray(polygen)) {
// 			                for (i in polygen) {
// 			                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
// 			                    	setVillagePolygenInfo(polygen[i],item);
// 			                    }
// 			                }
// 			            } else {
// 			            	setVillagePolygenInfo(polygen,item);
// 			            }
// 					});
// 					console.log(json_obj);
// 				}
// 			});
		},3000);
		
		
		$("body").append("<div id='msgAlert'></div>")
		$("#shpLegend").draggable({ containment: ".page-wrapper" });
		
		draw_menu_of_poi({
			action : "select_menu", 
			type : "POI"
		});

		$( "#opacity" ).slider({
	      range: "min",
	      value: 100,
	      min: 40,
	      max: 100,
	      slide: function( event, ui ) {
	        $( "#panel" ).css("opacity", (ui.value*0.01) );
	      }
	    });
	});
	
</script>

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	<input type='text' id='search-box-input-view' placeholder="輸入欲查詢地址">
	<a class='btn btn-primary' id='search-box-enter'>查詢</a>

<h2 class="page-title">商圈POI</h2>
	<div class="search-result-wrap">
	<a class="btn btn-orange" id="envAnal" style='position:absolute;right:5%;top:110px;z-index:99;'>環域分析</a>
	<div id="map"></div>
	
<!-- 目前shpLegend僅剩中國業態分布有用到而已 -->
<div id="shpLegend" class="shpLegend" style=" height: 235.5px;display:none;">
    <button onclick='$("#shpLegend").hide();'>x</button>
    <div style="display: block; background-color: #F0F0F0;">
        <table style="width:280px;">
            <tr>
                <th colspan="2" style="font-size: large;">
                    <span id="span_legend">TITLE</span>
                </th>
            </tr>
            <tr id="tr_year">
                <td style="width: 80px;">年份&nbsp;:</td><td><span id="ddl_year">Year</span></td>
            </tr>
            <tr>
                <td style="width: 80px;">單位&nbsp;:</td><td><span id="span_unit">Unit</span></td>
            </tr>
            <tr>
                <td style="vertical-align: top;">圖例&nbsp;:</td>
                <td>
                    <table>
                        <tr>
                            <td style="width: 10px;">
                                <div class="mapLegend" style="background-color: #41A85F;"></div>
                            </td>
                            <td><span id="span_level1">Level 1</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #8db444;"></div>
                            </td>
                            <td><span id="span_level2">Level 2</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #FAC51C;"></div>
                            </td>
                            <td><span id="span_level3">Level 3</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #d87926;"></div>
                            </td>
                            <td><span id="span_level4">Level 4</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #B8312F;"></div>
                            </td>
                            <td><span id="span_level5">Level 5</span></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</div>
	<div id="detail_1"></div>
	
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
			envAnalSetup("envAnal",map);
			
			$("#search-box-input-view").keypress(function(e) {
				if(e.which == 13) {
			    	e.preventDefault();
			    	$("#search-box-enter").trigger("click");
			    }
			});
			
			$("#search-box-enter").click(function(e){
				e.preventDefault();
				var search_str = $("#search-box-input-view").val();
				if($("#search-box-input-view").val().length==0){
					warningMsg('警告', "請輸入關鍵字以供查詢");
		            return;
				}
				//先查POI
				var poi_amount=0;
				$.ajax({
					type : "POST",
					url : "realMap.do",
					async : false,
					data : {
						action : "select_poi",
						name : search_str,
						lat : map.getCenter().lat,
						lng : map.getCenter().lng,
						zoom : map.getZoom()
					},success : function(result) {
						var json_obj = $.parseJSON(result);
						if(json_obj.length>0){
							poi_amount=json_obj.length;
							select_poi(search_str,"no_record");
						}
					}
				});
				
				if(poi_amount!=0){
					return;
				}
						
				//沒有POI接著查地址
				var str_to_place_service = new google.maps.places.AutocompleteService();
				str_to_place_service.getPlacePredictions({
					    input: search_str,
					    offset: search_str.length
					}, function listentoresult(list, status) {
						if (status != google.maps.places.PlacesServiceStatus.OK || list==null ) {
							warningMsg('警告', "查無結果請輸入更詳細關鍵字");
							return;
				    	}
						
						if(list[0].description.length-search_str.length>5){
							warningMsg('警告', 
									("<div style='display:contents;word-break:keep-all;'>下列地址搜尋無結果:<br>"
									+"<b style='font-size:1.2em;padding-left:16px;'>"+search_str+"</b><br><br>"
									+"您搜尋的地點或許是:<br>"
									+"<b style='font-size:1.2em;padding-left:16px;'>"+list[0].description+"</b></div>"
							));
// 							$("#search-box-input-view").val(list[0].description);
							return ;
						}
						var place_to_latlng_service = new google.maps.places.PlacesService(map);
						place_to_latlng_service.getDetails({ 
								placeId: list[0].place_id
							}, function(place, status) {
								if (status == google.maps.places.PlacesServiceStatus.OK) {
									address = [
				 					    (place.address_components[0] && place.address_components[0].short_name || ''),
				 					    (place.address_components[1] && place.address_components[1].short_name || ''),
				 					    (place.address_components[2] && place.address_components[2].short_name || '')
				 					  ].join(' ');
									
									//只放info
									var tmp_id=UUID();
									all_address.push(list[0].description);
									var infowindow = new google.maps.InfoWindow({
										content: ("<div style='padding:6px;'><strong>" + place.name + "</strong>"
												+"<br>" + address+" &nbsp;&nbsp;<a href='#' id='"+tmp_id+"' onclick='' class='tooltip' >刪除</a></div>")
									});
									var marker = new google.maps.Marker({
									    position: place.geometry.location,
									    icon: null,
									    map: map,
									});
									map.setCenter(place.geometry.location);
									if($("#region_select").dialog("isOpen")&& $("#draw_circle").css("display")=="none"){
									}else{
										map.fitBounds(place.geometry.viewport);
									}
									infowindow.open(map, marker);
									
									google.maps.event.addListener(marker, "click", function () {
										infowindow.open(map, marker);
									});
									
									google.maps.event.addListener(infowindow, "closeclick", function () {
										infowindow.setMap(null);
										if (all_address.indexOf(list[0].description)>-1){
											all_address.splice(all_address.indexOf(list[0].description), 1);
										}
							        });
									setTimeout(function(){
										$("#"+tmp_id).click(function(){
											marker.setMap(null);
											infowindow.setMap(null);
										});
									}, 1000);
										
								}
						});
				});
				return ;
			});
   		}

    </script>
    
<!--     libraries=visualization 是給熱力圖用的 -->
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBSQDx-_LzT3hRhcQcQY3hHgX2eQzF9weQ&signed_in=true&libraries=places,visualization&callback=initMap"></script>
	</div>
</div>

<jsp:include page="footer.jsp" flush="true"/>